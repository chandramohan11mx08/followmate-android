package com.example.chandramohanr.followmate.app.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.adapters.ContactListAdapter;
import com.example.chandramohanr.followmate.app.models.ContactModel;
import com.noveogroup.android.log.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.contact_list_view)
public class ContactListActivity extends BaseActivity {

    @ViewById(R.id.list)
    ListView contactListView;

    Activity thisActivity;

    @AfterViews
    public void afterViews() {
        thisActivity = this;
        populateContacts();
    }

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
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE;
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            addPhoneNumberIfNotDuplicate(phoneNumbers, phoneNo);
                    }
                    pCur.close();
                    for(String phno: phoneNumbers){
                        ContactModel contactModel = new ContactModel(id, name, phno);
                        contactModelList.add(contactModel);
                    }
                }
            }
        }
        ContactListAdapter contactListAdapter = new ContactListAdapter(this, contactModelList);
        contactListView.setAdapter(contactListAdapter);
    }

    private void addPhoneNumberIfNotDuplicate(List<String> phoneNumbers, String phoneNo) {
        if(phoneNumbers.size() ==0){
            phoneNumbers.add(phoneNo);
        }else{
            for(String phno: phoneNumbers){

                if(phno.equals(phoneNo)){
                    return;
                }
            }
            phoneNumbers.add(phoneNo);
        }
    }
}
