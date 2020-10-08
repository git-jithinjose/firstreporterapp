package com.vbot.firstreport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.vbot.firstreport.ApiClient.ApiClient;
import com.vbot.firstreport.ApiInterface.ApiInterface;

@SuppressLint("Registered")
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final int REQUEST_SIGNUP = 0;
    Context context;
    public ApiInterface apiInterface;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText userName, userMail, userMobile, userAddress;
    Button signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeStatusBarColor();
        context= SignupActivity.this;
        apiInterface = ApiClient.getRetrofitApiClient().create(ApiInterface.class);
        passIntents();

        userName = findViewById(R.id.etUserName);
        userMobile = findViewById(R.id.etUserMobile);
        userMail = findViewById(R.id.etUserEmail);
        userAddress = findViewById(R.id.etUserAddress);

        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

    private void passIntents() {
        Intent i = this.getIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    public boolean validate() {
        boolean valid = true;
        //Get the defined errorColor from color resource.

        int errorColor;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            errorColor = ContextCompat.getColor(getApplicationContext(), R.color.white);
        } else {
            errorColor = getResources().getColor(R.color.colorAccent);
        }
        String email = userMail.getText().toString();
        String mobile = userMobile.getText().toString();

        if (email.isEmpty()) {//|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

            String errorString = "Enter a valid Email Id";  // Error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            userMail.setError(spannableStringBuilder);
            valid = false;
        } else {
            userMail.setError(null);
        }

        if (mobile.isEmpty()) {
            String errorString = "Enter a Valid Mobile Number";  // Error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            userMobile.setError(spannableStringBuilder);
            valid = false;
        } else {
            userMobile.setError(null);
        }

        return valid;
    }
}