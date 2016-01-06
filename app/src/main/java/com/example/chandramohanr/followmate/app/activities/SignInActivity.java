package com.example.chandramohanr.followmate.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.View;
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

    @ViewById(R.id.verification_code)
    EditText vVerificationCode;

    @ViewById(R.id.proceed_button)
    Button vProceedButton;

    private final int REGISTER_MOBILE = 1;
    private final int VERIFY_CODE = 2;
    private int actionForProceedButton = REGISTER_MOBILE;

    private final int REQUEST_SMS_PERMISSION = 8;

    final EventBus eventBus = EventBus.getDefault();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    @Click(R.id.proceed_button)
    public void onProceedClick() {
        if (actionForProceedButton == REGISTER_MOBILE) {
            String mobileNumber = vMobileNumber.getText().toString();
            if (!mobileNumber.isEmpty()) {
                String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Intent intent = new Intent(this, UserService.class);
                intent.putExtra(AppConstants.SERVICE_TYPE, 1);
                intent.putExtra(AppConstants.MOBILE_NUMBER, mobileNumber);
                intent.putExtra(AppConstants.DEVICE_ID, deviceId);
                startService(intent);
            } else {
                vMobileNumber.setError(getString(R.string.enter_valid_mobile_number));
            }
        } else if (actionForProceedButton == VERIFY_CODE) {
            String codeEntered = vVerificationCode.getText().toString();
            String codeSent = SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_CODE);
            if (codeEntered.equals(codeSent)) {
                postVerification();
            } else {
                vVerificationCode.setError(getString(R.string.wrong_code));
            }
        }
    }

    public void onEventMainThread(RegisterMobileNumberResponse registerMobileNumberResponse) {
        if (registerMobileNumberResponse.is_user_created || registerMobileNumberResponse.isVerificationRequired) {
            String userId = registerMobileNumberResponse.userId;
            String mobileNumber = registerMobileNumberResponse.mobileNumber;

            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_USER_ID, userId);
            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_MOBILE_NUMBER, mobileNumber);

            AppUtil.setAccountManager(mobileNumber, getApplicationContext());

            actionForProceedButton = VERIFY_CODE;
            vMobileNumber.setVisibility(View.GONE);
            vVerificationCode.setVisibility(View.VISIBLE);

            SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_CODE, registerMobileNumberResponse.code);

            sendSmsForVerification();

        } else {
            setErrorMessage(registerMobileNumberResponse.message);
        }
    }

    private void sendSmsForVerification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ) {
            requestSendSmsPermission();
        }else{
            sendSms();
        }
    }

    private void sendSms() {
        SmsManager smsManager = SmsManager.getDefault();
        String codeToSend = SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_CODE);
        String mobileNumber = SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_MOBILE_NUMBER);
        smsManager.sendTextMessage(mobileNumber, null, "Use " + codeToSend + " code to verify your followmate user identity", null, null);
    }

    private void requestSendSmsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                REQUEST_SMS_PERMISSION);
    }

    private void postVerification() {
        Toast.makeText(getApplicationContext(), "Mobile number verified", Toast.LENGTH_SHORT).show();
        SharedPreferenceHelper.set(SharedPreferenceHelper.KEY_MOBILE_VERIFIED, true);
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity_.class);
        setResult(RESULT_OK);
        finish();
        startActivity(mainActivityIntent);
    }

    private void setErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS_PERMISSION: {
                boolean isGranted = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (isGranted) {
                    sendSms();
                }
                break;
            }
        }
    }
}
