package com.example.chandramohanr.followmate.activities.activities;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.activities.Constants.AppConstants;
import com.example.chandramohanr.followmate.activities.services.UserService;
import com.noveogroup.android.log.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_sigin)
public class SignInActivity extends BaseActivity {

    @ViewById(R.id.mobile_number)
    EditText vMobileNumber;

    @ViewById(R.id.error_view)
    TextView vErrorMsg;

    @ViewById(R.id.proceed_button)
    Button vProceedButton;

    @AfterViews
    public void afterViewInjection() {
        Log.a("hii");
        Intent intent = new Intent(this, UserService.class);
        intent.putExtra(AppConstants.SERVICE_TYPE, 1);
        startService(intent);
    }
}