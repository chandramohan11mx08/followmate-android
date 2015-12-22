package com.example.chandramohanr.followmate.app.activities;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;

import com.example.chandramohanr.followmate.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.contact_list_view)
public class ContactListActivity extends Activity{

    @ViewById(R.id.list)
    ListView contactListView;

    SimpleCursorAdapter mCursorAdapter;

    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    Activity thisActivity;

    @AfterViews
    public void afterViews(){
        thisActivity = this;
    }

}
