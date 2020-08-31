package com.example.smartoffice.interfaces;

import com.example.smartoffice.model.AttachmentFiles;
import com.example.smartoffice.model.User;

public interface IRemoveItem {
    void RemoveItemInListUser(int positionUser);
    void RemoveItemInListAttachment(int positionAttachment);

}
