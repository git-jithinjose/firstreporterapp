package com.vbot.firstreport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.vbot.firstreport.ApiClient.ApiClient;
import com.vbot.firstreport.ApiInterface.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("Registered")
public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etMobNo;
    Button btnSend;
    String userDomain;
    ApiInterface apiInterface;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        changeStatusBarColor();
        context = this;
        btnSend = findViewById(R.id.btnSend);
        apiInterface = ApiClient.getRetrofitApiClient().create(ApiInterface.class);
        passIntentValues();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etMobNo.getText().toString().length()<10) {
                    Snackbar snackbar = Snackbar.make(view, "Enter valid Mobile Number!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.YELLOW)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    etMobNo.getText().clear();
                                }
                            });
                    snackbar.show();
                } else {
                    sendOtp();
                }
            }
        });
    }

    private void passIntentValues() {
        Intent intent = getIntent();
        userDomain = intent.getStringExtra("domain");
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }
    private void sendOtp() {
        String mobile = etMobNo.getText().toString();

        Call<Integer> call = apiInterface.forgotPassword(userDomain,mobile);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}