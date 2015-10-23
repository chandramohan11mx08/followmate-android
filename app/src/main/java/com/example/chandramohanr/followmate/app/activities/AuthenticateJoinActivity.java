package com.example.chandramohanr.followmate.app.activities;

import android.widget.EditText;

import com.example.chandramohanr.followmate.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_join_auth)
public class AuthenticateJoinActivity extends BaseActivity{
    @ViewById(R.id.session_code)
    EditText vSessionCodeText;

    @Click(R.id.join_session)
    public void onJoinSession(){

    }
}
