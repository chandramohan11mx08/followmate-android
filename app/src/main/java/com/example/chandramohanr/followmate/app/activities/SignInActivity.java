package com.example.chandramohanr.followmate.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.helpers.SharedPreferenceHelper;
import com.example.chandramohanr.followmate.app.models.RegisterMobileNumberResponse;
import com.example.chandramohanr.followmate.app.services.UserService;

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

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        eventBus.register(this);
    }

    @Override
    public void onDestroy(){
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Click(R.id.proceed_button)
    public void onProceedClick() {
        String mobileNumber = vMobileNumber.getText().toString();
        if (!mobileNumber.isEmpty()) {
            String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            Intent intent = new Intent(this, UserService.class);
            intent.putExtra(AppConstants.SERVICE_TYPE, 1);
            intent.putExtra(AppConstants.MOBILE_NUMBER, mobileNumber);
            intent.putExtra(AppConstants.DEVICE_ID, deviceId);
            startService(intent);
        } else {
            setErrorMessage(getString(R.string.enter_valid_mobile_number));
        }
    }

    public void onEventMainThread(RegisterMobileNumberResponse registerMobileNumberResponse) {
        if (registerMobileNumberResponse.is_user_created) {
            String userId = registerMobileNumberResponse.userId;
            String mobileNumber = registerMobileNumberResponse.mobileNumber;

            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_USER_ID, userId);
            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_MOBILE_NUMBER, mobileNumber);

            AppUtil.setAccountManager(mobileNumber, getApplicationContext());

            Intent mainActivityIntent = new Intent(this, MainActivity_.class);
            setResult(RESULT_OK);
            finish();
            startActivity(mainActivityIntent);
        } else {
            setErrorMessage(registerMobileNumberResponse.message);
        }
    }

    private void setErrorMessage(String errorMessage) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show();
    }
}
