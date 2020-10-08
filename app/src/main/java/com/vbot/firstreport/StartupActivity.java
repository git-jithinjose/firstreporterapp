package com.vbot.firstreport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.vbot.firstreport.ApiClient.ApiClient;
import com.vbot.firstreport.ApiInterface.ApiInterface;


public class StartupActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String FAPREFS = "faPrefs";
    Context context;
    private ApiInterface apiInterface;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeStatusBarColor();
        context= StartupActivity.this;
        apiInterface = ApiClient.getRetrofitApiClient().create(ApiInterface.class);

        sharedPreferences = context.getSharedPreferences(FAPREFS, Context.MODE_PRIVATE);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

}