package com.example.chandramohanr.followmate.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.chandramohanr.followmate.app.network.ContactSyncAdapter;
import com.noveogroup.android.log.Log;

public class ContactSyncService  extends Service{

    private ContactSyncAdapter contactSyncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    public ContactSyncService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.a("Contact Sync Service created");
        synchronized (sSyncAdapterLock) {
            if (contactSyncAdapter == null) {
                contactSyncAdapter = new ContactSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.a("Contact sync service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.a("Contact sync service return iBinder");
        return contactSyncAdapter.getSyncAdapterBinder();
    }
}
