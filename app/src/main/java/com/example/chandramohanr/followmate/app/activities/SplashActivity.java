package com.example.chandramohanr.followmate.app.activities;

import android.content.Intent;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.helpers.SharedPreferenceHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity{

    @AfterViews
    public void afterViewInjection(){
        String userId = SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_USER_ID);
        Intent intent = null;
        if(userId.isEmpty()){
            intent = new Intent(this, SignInActivity_.class);
        }else{
            intent = new Intent(this, MainActivity_.class);
        }
        startActivity(intent);
    }
}
