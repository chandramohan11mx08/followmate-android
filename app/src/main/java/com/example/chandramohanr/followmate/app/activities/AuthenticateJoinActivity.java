package com.example.chandramohanr.followmate.app.activities;

import android.content.Intent;
import android.widget.EditText;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.Constants.AppConstants;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_join_auth)
public class AuthenticateJoinActivity extends BaseActivity{
    @ViewById(R.id.session_code)
    EditText vSessionCodeText;

    @Click(R.id.join_session)
    public void onJoinSession(){
        Intent data = new Intent();
        data.putExtra(AppConstants.SESSION_ID,vSessionCodeText.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }
}
