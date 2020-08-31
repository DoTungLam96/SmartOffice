package com.example.smartoffice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartoffice.R;
import com.example.smartoffice.common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences luuUsername;
    private String userName;
    ImageView logo1,logo2;
    private TextView txt_username, txt_hot_line;
    Animation animationLogo2, animationLogo1;
    private LinearLayout rootLayout, layoutLoading;
    private ProgressBar progressBar;

    private static final int PERMISSION_TOTAL = 10;
    private RelativeLayout btnLogin;
    private EditText edtUsername, edtPassword;
    public static FirebaseAuth firebaseAuth;
    private AlertDialog alertDialog;
    private int key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initView();
        ignorLoginIfExistAccount();
        getPermission();

        //Chuyen man hinh
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setAnimationView();
                progressBar.setVisibility(View.GONE);
                txt_username.setVisibility(View.GONE);
                txt_hot_line.setVisibility(View.VISIBLE);

            }
        },2000);

        luuUsername = getSharedPreferences(Common.KEY_CURRENT_USER, Context.MODE_PRIVATE);
        userName = luuUsername.getString(Common.Email, "");
        if (!userName.equals(""))
        {
            txt_username.setText("Hello, "+userName.substring(0,userName.length()-15).toUpperCase());
        }
    }

    //region setup animation from Splash screen to Login screen
    private void setAnimationView() {
        logo1.setVisibility(View.VISIBLE);
        rootLayout.setVisibility(View.VISIBLE);
        txt_hot_line.setVisibility(View.GONE);
        txt_username.setVisibility(View.VISIBLE);
        animationLogo2 = AnimationUtils.loadAnimation(this, R.anim.bg_anim);
        animationLogo1 = AnimationUtils.loadAnimation(this, R.anim.bg_anim_logo2);
        logo2.setAnimation(animationLogo2);
        logo1.setAnimation(animationLogo1);
        rootLayout.setAnimation(animationLogo1);
        logo2.setVisibility(View.GONE);
    }
   //endregion

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null)
        {
            key = intent.getIntExtra(Common.ACTION_SEND_DATA_LOGIN, 0);
            if (key == 1)
                edtUsername.setText(Common.currentUser);
        }
    }

    // region ignor login screen if user is logined before
    private void ignorLoginIfExistAccount() {
        if (firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    //endregion

    //region declare view in activity_login
    private void initView() {
        btnLogin = findViewById(R.id.btnLogin);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        txt_hot_line = findViewById(R.id.txtHotline);
        txt_username = findViewById(R.id.txt_user_splashscreen);
        logo1 = findViewById(R.id.imgLogo1);
        logo2 = findViewById(R.id.imgLogo2);
        rootLayout = findViewById(R.id.linearLayout);
        layoutLoading = findViewById(R.id.layoutLoading);
        progressBar = findViewById(R.id.processBar);

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(this);

        rootLayout.setVisibility(View.GONE);
        logo1.setVisibility(View.GONE);
        txt_hot_line.setVisibility(View.GONE);

        alertDialog = new SpotsDialog.Builder().setContext(SplashScreen.this).setCancelable(false).build();
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    //endregion

    //region Request permission
    private void getPermission() {
        int CheckPermissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int CheckPermissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CheckInternet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int CheckPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int CheckPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int CheckCalendarRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int CheckCalendarWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        if (CheckPermissionRead != PackageManager.PERMISSION_GRANTED || CheckPhone != PackageManager.PERMISSION_GRANTED
                || CheckPhoneState != PackageManager.PERMISSION_GRANTED || CheckCalendarRead != PackageManager.PERMISSION_GRANTED
                || CheckCalendarWrite != PackageManager.PERMISSION_GRANTED
                || CheckPermissionWrite != PackageManager.PERMISSION_GRANTED || CheckInternet != PackageManager.PERMISSION_GRANTED )
        {
            // Permission is not granted
             ActivityCompat.requestPermissions(SplashScreen.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                     Manifest.permission.INTERNET,
                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.WRITE_CALENDAR,
                     Manifest.permission.READ_CALENDAR,
                     Manifest.permission.READ_PHONE_STATE,
                     Manifest.permission.CALL_PHONE},PERMISSION_TOTAL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_TOTAL && permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {

        }
        else {
            Toast.makeText(this, "Permission denie", Toast.LENGTH_SHORT).show();
        }
    }

    //endregion

    //region handle login wirh firebase authorise
    private void LoginWithFirebaseAuth(){
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (!username.contains(Common.prefixEmail))
        {
            username = username+Common.prefixEmail;
        }

        if (username.isEmpty() || password.isEmpty())
        {
            Common.ShowMessageError(SplashScreen.this,"Username or password is not empty.", Toasty.LENGTH_LONG);
            alertDialog.dismiss();
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (alertDialog.isShowing())
                            alertDialog.dismiss();
                        Common.ShowMessageSuccess(SplashScreen.this, "Login success.", Toasty.LENGTH_LONG);
                        Common.currentUser = firebaseAuth.getCurrentUser().getEmail();
                        Intent iTrangChu = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(iTrangChu);
                        finish();
                    }
                    else {
                        alertDialog.dismiss();
                        Common.ShowMessageError(SplashScreen.this, "Username or password incorrect.", Toasty.LENGTH_LONG);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                  //  Common.ShowMessageError(SplashScreen.this,e.getMessage(),Toasty.LENGTH_LONG);
                }
            });
        }

    }
    //endregion

    //region event click on view
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnLogin :
            {
                alertDialog.show();
                LoginWithFirebaseAuth();
            }
            break;

            default:
                break;
        }
    }
    //endregion
}