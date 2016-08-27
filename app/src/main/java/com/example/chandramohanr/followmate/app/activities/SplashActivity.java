package com.example.chandramohanr.followmate.app.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.models.ContactModel;
import com.example.chandramohanr.followmate.app.models.SyncContactRequestBody;
import com.example.chandramohanr.followmate.app.services.UserService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    @AfterViews
    public void afterViewInjection() {
        boolean isVerified = AppUtil.isUserMobileNumberVerified();
        Intent intent = null;
        if (isVerified) {
            intent = new Intent(this, MainActivity_.class);
        } else {
            intent = new Intent(this, SignInActivity_.class);
        }
        startActivity(intent);
    }

//        @Click(R.id.contacts)
    public void launchContactList() {
        populateContacts();
    }

    @Background
    public void populateContacts() {
        List<ContactModel> contactModelList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    List<String> phoneNumbers = new ArrayList();
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        addPhoneNumberIfNotDuplicate(phoneNumbers, phoneNo);
                    }
                    pCur.close();
                    for (String phno : phoneNumbers) {
                        ContactModel contactModel = new ContactModel(id, name, phno);
                        contactModelList.add(contactModel);
                    }
                }
            }
        }
        if (contactModelList.size() > 0) {
            SyncContactRequestBody syncContactRequestBody = new SyncContactRequestBody(AppUtil.getLoggedInUserId(), contactModelList);
            Intent intent = new Intent(this, UserService.class);
            intent.putExtra(AppConstants.SERVICE_TYPE, UserService.SYNC_CONTACTS);
            intent.putExtra(AppConstants.DATA, Parcels.wrap(syncContactRequestBody));
            startService(intent);
        }
    }

    private void addPhoneNumberIfNotDuplicate(List<String> phoneNumbers, String phoneNo) {
        phoneNo = phoneNo.replaceAll("[^\\d]","");
        int length = phoneNo.length();
        if(length > 10){
            phoneNo = phoneNo.substring(length - 10);
        }
        if (phoneNumbers.size() == 0) {
            phoneNumbers.add(phoneNo);
        } else {
            for (String phno : phoneNumbers) {
                if (phno.equals(phoneNo)) {
                    return;
                }
            }
            phoneNumbers.add(phoneNo);
        }
    }

}
