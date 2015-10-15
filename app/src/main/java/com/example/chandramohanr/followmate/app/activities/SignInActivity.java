package com.example.chandramohanr.followmate.app.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.helpers.SharedPreferenceHelper;
import com.example.chandramohanr.followmate.app.models.RegisterMobileNumberResponse;
import com.example.chandramohanr.followmate.app.services.UserService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_sigin)
public class SignInActivity extends BaseActivity {

    @ViewById(R.id.mobile_number)
    EditText vMobileNumber;

    @ViewById(R.id.proceed_button)
    Button vProceedButton;

    final EventBus eventBus = EventBus.getDefault();

    @Click(R.id.proceed_button)
    public void onProceedClick() {
        String mobileNumber = vMobileNumber.getText().toString();
        if (!mobileNumber.isEmpty()) {
            Intent intent = new Intent(this, UserService.class);
            intent.putExtra(AppConstants.SERVICE_TYPE, 1);
            intent.putExtra(AppConstants.MOBILE_NUMBER, mobileNumber);
            startService(intent);
        } else {
            setErrorMessage(getString(R.string.enter_valid_mobile_number));
        }
    }

    public void onEventMainThread(RegisterMobileNumberResponse registerMobileNumberResponse) {
        if (registerMobileNumberResponse.is_user_created) {
            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_USER_ID, registerMobileNumberResponse.userId);
            Intent intent = new Intent(this, MainActivity_.class);
            startActivity(intent);
        } else {
            setErrorMessage(registerMobileNumberResponse.message);
        }
    }

    private void setErrorMessage(String errorMessage) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show();
    }
}
