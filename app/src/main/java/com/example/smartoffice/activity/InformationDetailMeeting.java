package com.example.smartoffice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartoffice.R;
import com.example.smartoffice.adapter.AdapterDetailUserRoom;
import com.example.smartoffice.common.Common;
import com.example.smartoffice.model.AttachmentFiles;
import com.example.smartoffice.model.MeetingDetail;
import com.example.smartoffice.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InformationDetailMeeting extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private  MeetingDetail meetingDetail;
    private TextView txt_date_detail,txt_time_detail,
                     txt_address_detail,txt_title_detail,
                     txt_content_detail,txt_toi_da_file;
    private Button btn_pdf, btn_docx;
    private String nameFilePdf, nameFileDocx, userBooking;

    private StorageReference storageReference;
    private AdapterDetailUserRoom adapterDetailUserRoom;
    private RecyclerView recycler_view_detail_booking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail_meeting);
        init();
        getDataFromMain();
    }


    //region init view and property
    private void init() {
        txt_address_detail = findViewById(R.id.txt_address_detail);
        txt_date_detail = findViewById(R.id.txt_date_detail);
        txt_time_detail = findViewById(R.id.txt_time_detail);
        txt_content_detail = findViewById(R.id.txt_content_detail);
        txt_title_detail = findViewById(R.id.txt_title_detail);
        btn_docx = findViewById(R.id.btn_attach_docx);
        btn_pdf = findViewById(R.id.btn_attach_pdf);
        txt_toi_da_file = findViewById(R.id.txt_toi_da_flie);
        recycler_view_detail_booking = findViewById(R.id.recycler_view_detail_booking);
        storageReference = FirebaseStorage.getInstance().getReference().child(Common.Attachment);

        //set invisible btn_pdf, btn_docx
        btn_pdf.setVisibility(View.GONE);
        btn_docx.setVisibility(View.GONE);

        //declare event onclicklistenr
        btn_pdf.setOnClickListener(this);
        btn_docx.setOnClickListener(this);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Chi tết cuộc họp");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InformationDetailMeeting.this, MainActivity.class));
                finish();
            }
        });
    }
    //endregion

    //region get all data send from MainActivity
    private void getDataFromMain() {
        Intent intent = getIntent();
        meetingDetail = intent.getParcelableExtra(Common.KEY_MEETING_ALL_ACTIVITY);
        txt_title_detail.setText(Common.cutStringIfSoLong(100, meetingDetail.getTitle()));
        txt_content_detail.setText(Common.cutStringIfSoLong(100, meetingDetail.getContent()));
        txt_address_detail.setText(Common.cutStringIfSoLong(40, meetingDetail.getRoombooking().getAddress()));
        userBooking = meetingDetail.getUsernamebooking();

        String startHour = meetingDetail.getStarttime();
        String endHour = meetingDetail.getEndtime();

        txt_time_detail.setText("Từ "+startHour+" đến "+endHour);
        txt_date_detail.setText(Common.dateSelected);

        showAttachmentFiles(meetingDetail);

        setUpRecyclerView(meetingDetail.getMemberjoin());
    }
    //endregion

    //region display information user to recyclerview
    private void setUpRecyclerView(List<User> listUser) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL, false);
        recycler_view_detail_booking.setLayoutManager(linearLayoutManager);
        adapterDetailUserRoom = new AdapterDetailUserRoom(this, R.layout.custom_recycler_layout_information_detail,listUser);
        recycler_view_detail_booking.setAdapter(adapterDetailUserRoom);
    }
    //endregion

    //region handle logic show attachments file
    private void showAttachmentFiles(MeetingDetail meetingDetail) {
        //handle logic btnPDF and btnDocx
        if (meetingDetail.getAttachmentfile() != null && meetingDetail.getAttachmentfile().size() > 0)
        {
            Drawable img = null;
            nameFileDocx = null;
            nameFilePdf = null;
            boolean isAgainPDF = false;
            boolean isAgainDOCX = false;
            for (int i = 0 ; i< meetingDetail.getAttachmentfile().size() ; i++)
            {
                switch (meetingDetail.getAttachmentfile().get(i).getType())
                {
                    case Common.PDF :
                    {
                        btn_pdf.setVisibility(View.VISIBLE);
                        btn_pdf.setPadding(15,15,15,15);
                        img  = this.getResources().getDrawable(R.drawable.pdf_24);
                        if (!isAgainPDF)
                        {
                            btn_pdf.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                            btn_pdf.setBackground(getDrawable(R.drawable.bg_attachment_pdf));
                            btn_pdf.setText(Common.cutStringIfSoLong(25,meetingDetail.getAttachmentfile().get(i).getName()));
                            isAgainPDF = true;
                            nameFilePdf = meetingDetail.getAttachmentfile().get(i).getName();
                        }
                        else
                        {
                            btn_docx.setVisibility(View.VISIBLE);
                            btn_docx.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                            btn_docx.setBackground(getDrawable(R.drawable.bg_attachment_pdf));
                            btn_docx.setText(Common.cutStringIfSoLong(25,meetingDetail.getAttachmentfile().get(i).getName()));
                            nameFileDocx = meetingDetail.getAttachmentfile().get(i).getName();
                        }


                    }break;
                    case Common.DOCX :
                    {
                        btn_docx.setVisibility(View.VISIBLE);
                        btn_docx.setPadding(15,15,15,15);
                        img  = this.getResources().getDrawable(R.drawable.docx_24);
                        if (!isAgainDOCX)
                        {
                            btn_docx.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                            btn_docx.setBackground(getDrawable(R.drawable.bg_attachment_docx));
                            btn_docx.setText(Common.cutStringIfSoLong(25,meetingDetail.getAttachmentfile().get(i).getName()));
                            isAgainPDF = true;
                            nameFileDocx = meetingDetail.getAttachmentfile().get(i).getName();
                        }
                        else
                        {
                            btn_pdf.setVisibility(View.VISIBLE);
                            btn_pdf.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                            btn_pdf.setBackground(getDrawable(R.drawable.bg_attachment_docx));
                            btn_pdf.setText(Common.cutStringIfSoLong(25,meetingDetail.getAttachmentfile().get(i).getName()));
                            nameFilePdf = meetingDetail.getAttachmentfile().get(i).getName();
                        }

                    }break;

                    default:
                        break;
                }
            }
        }
        else
        {
            txt_toi_da_file.setText(Html.fromHtml("<b><<i>"+getString(R.string.chuacofile)+"</i></b>"));
        }
    }
    //endregion

    //region init menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail_meeting, menu);
        if (!userBooking.toLowerCase().trim().equals(Common.currentUser))
        {
            menu.removeItem(R.id.edit_menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu: {
                Intent iEditDetailMeeting = new Intent(InformationDetailMeeting.this, EditDetailMeetingActivity.class);
                iEditDetailMeeting.putExtra(Common.KEY_MEETING_ALL_ACTIVITY, (Parcelable) meetingDetail);
                startActivity(iEditDetailMeeting);
                return true;
            }
            default:
                return false;

        }
    }

    //endregion

    //region handle event click on view
    @Override
    public void onClick(View view) {
      switch (view.getId())
      {
          case R.id.btn_attach_docx :
          {
              DownloadFileFromFirebase(nameFileDocx);
          }
          break;
          case R.id.btn_attach_pdf :
          {
              DownloadFileFromFirebase(nameFilePdf);
          }
          break;

          default: break;
      }
    }
    //endregion

    //region download file from firebase storage
    private void DownloadFileFromFirebase(final String nameFile)
    {
        if (TextUtils.isEmpty(nameFile)) return;

        final String pathSaveData = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        storageReference.child(nameFile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadFile(InformationDetailMeeting.this, nameFile,pathSaveData,uri.toString() );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InformationDetailMeeting.this, e.getMessage()+"", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void DownloadFile(Context context , String filename, String destination, String Url)
    {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(Url);
            DownloadManager.Request request  = new DownloadManager.Request(uri);

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(context, destination, filename);

            downloadManager.enqueue(request);
        }
        catch (Exception ex)
        {
            Log.e("kiemtra", ex.getMessage());
        }
    }
    //endregion

}