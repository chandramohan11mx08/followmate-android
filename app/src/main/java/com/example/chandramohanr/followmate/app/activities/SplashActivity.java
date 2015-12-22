package com.example.chandramohanr.followmate.app.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.noveogroup.android.log.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    @AfterViews
    public void afterViewInjection() {
//        fetchContacts();
        String userId = AppUtil.getLoggedInUserId();
        Intent intent = null;
        if (userId.isEmpty()) {
            intent = new Intent(this, SignInActivity_.class);
        } else {
            intent = new Intent(this, MainActivity_.class);
        }
        startActivity(intent);
    }

//    @Click(R.id.contacts)
    @Background
    public void fetchContacts() {
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
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.a("Name: " + name + ", Phone No: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
    }
}
