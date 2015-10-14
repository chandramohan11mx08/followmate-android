package com.example.chandramohanr.followmate.activities;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chandramohanr.followmate.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_sigin)
public class InitiateSessionActivity extends BaseActivity {

    @ViewById(R.id.mobile_number)
    EditText vMobileNumber;

    @ViewById(R.id.error_view)
    TextView vErrorMsg;

    @ViewById(R.id.proceed_button)
    Button vProceedButton;

    @AfterViews
    public void afterViewInjection() {

    }
}
