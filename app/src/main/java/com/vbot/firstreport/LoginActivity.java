package com.vbot.firstreport;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.vbot.firstreport.ApiClient.ApiClient;
import com.vbot.firstreport.ApiInterface.ApiInterface;
import com.vbot.firstreport.VOs.TokenRequest;
import com.vbot.firstreport.VOs.TokenResponse;
import com.vbot.firstreport.VOs.UserBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("Registered")
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public static final String FAPREFS = "faPrefs";
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    public static final String USERID = "userId";
    Context context;
    private ApiInterface apiInterface;
    String userName ;
    String password ;
    String sessionToken;
    String  userType;
    Long userId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String domain;
    EditText emailText;
    EditText passwordText;
    ProgressDialog progressDialog;
    Button loginButton;
    TextView forgotPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeStatusBarColor();
        context= LoginActivity.this;
        apiInterface = ApiClient.getRetrofitApiClient().create(ApiInterface.class);
        sharedPreferences = context.getSharedPreferences(FAPREFS, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName",null);
        userId = sharedPreferences.getLong("userId",0);
        userType=sharedPreferences.getString("domain",null);
        passIntents();

        if (userName != null && userId!=null && userType!=null) {
            if (userId == 0) {
                startActivity(new Intent(this, HomePage.class));
            }
        }
        emailText = findViewById(R.id.editTextEmail);
        passwordText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.cirLoginButton);
        forgotPassword = findViewById(R.id.forgotPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), HomePage.class));
                progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyDialogTheme);
                progressDialog.setIndeterminate(true);
//                progressDialog.setIcon(R.drawable.logo);
                progressDialog.setTitle("Let's Report");
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

                login(progressDialog);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
//                intent.putExtra("domain", domain );
                startActivity(intent);
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
        domain = i.getStringExtra("KEY_ID");
    }

    public void login(final ProgressDialog progressDialog) {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
        }else {

            loginButton.setEnabled(false);

            userName = emailText.getText().toString();
            password = passwordText.getText().toString();

            TokenRequest tokenRequest = new TokenRequest();
            tokenRequest.setUsername(userName);
            tokenRequest.setPassword(password);
            tokenRequest.setDomain(domain);
            Call<TokenResponse> applogmodel = apiInterface.authenticate(tokenRequest);
            applogmodel.enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    TokenResponse response_model = response.body();
                    if (response.code() == 401) {
                        progressDialog.dismiss();
                        emailText.setError("Invalid Login Details");
                        passwordText.setError("Invalid Login Details");
                        loginButton.setEnabled(true);
                    } else {
                        sessionToken = response_model.getSessionToken();
                        if (!sessionToken.equals("") && !sessionToken.equals("null") && sessionToken.trim().length() > 0) {
                            editor = sharedPreferences.edit();
                            editor.putString("sessionToken", sessionToken);
                            editor.putString("userName", userName);
                            editor.apply();
                            editor.commit();
                            getUserDetails(userName, password, sessionToken, progressDialog);

                        } else {
                            onLoginFailed();
                            progressDialog.dismiss();

                        }
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    onLoginFailed();
                    progressDialog.dismiss();

                }
            });
        }
    }

    public void getUserDetails(final String userName, final String password, final String sessionToken, final ProgressDialog progressDialog) {
        UserBean loginmodel = new UserBean();
        loginmodel.setUserName(userName);
        loginmodel.setPassword(password);
        loginmodel.setUserDomain(domain);
        if (domain.equals("N") || domain.equals("F")) {
            Call<UserBean> applogmodel = apiInterface.authenticateUser(loginmodel, sessionToken);
            applogmodel.enqueue(new Callback<UserBean>() {

                @Override
                public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                    UserBean status = response.body();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", status.getUserName());
                    editor.putLong("userId", status.getUser());
                    editor.putString("domain", status.getUserDomain());
                    editor.apply();
                    editor.commit();

                    onLoginSuccess(progressDialog);
                }

                @Override
                public void onFailure(Call<UserBean> call, Throwable t) {

                }
            });
        } else if (domain.equals("C") || domain.equals("T")){
            Call<UserBean> applogmodel = apiInterface.authenticateUser(loginmodel, sessionToken);
            applogmodel.enqueue(new Callback<UserBean>() {
                @Override
                public void onResponse(Call<UserBean> call, Response<UserBean> response) {
                    UserBean status = response.body();
                    if (response.code() == 512) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setIcon(R.drawable.ic_warning);
                        builder.setTitle("Not yet verified by NGO");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        builder.show();
                    } else if (response.code() == 516) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View dialogView = inflater.inflate(R.layout.password_reset_dialog, null);
                        builder.setCancelable(false);
                        builder.setView(dialogView);
                        final EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
                        final EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
                        etOldPassword.setText(password);
                        Button btnPositive = dialogView.findViewById(R.id.dialog_positive);
                        btnPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                resetPassword();
                            }

                            private void resetPassword() {
                                UserBean loginmodel = new UserBean();
                                loginmodel.setUserName(userName);
                                loginmodel.setPassword(password);
                                loginmodel.setUserDomain(domain);
                                loginmodel.setNewPassword(etNewPassword.getText().toString().trim());
                                try {
                                    Call<Integer> applogmodel = apiInterface.resetPassword(loginmodel, sessionToken);
                                    applogmodel.enqueue(new Callback<Integer>() {
                                        @Override
                                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                                            Integer resp = response.body();
                                            if (resp == 200) {
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("userName", userName);
                                                editor.putLong("userId", 3);
                                                editor.putString("domain", domain);
                                                editor.apply();

                                                onLoginSuccess(progressDialog);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Integer> call, Throwable t) {
                                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (Exception e) {

                                }
                            }
                        });
                        builder.show();
                    } else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", status.getUserName());
                        editor.putLong("userId", status.getUser());
                        editor.putString("domain", status.getUserDomain());
                        editor.apply();
                        editor.commit();

                        onLoginSuccess(progressDialog);
                    }

                }

                @Override
                public void onFailure(Call<UserBean> call, Throwable t) {
                    onLoginFailed();
                    progressDialog.dismiss();
                }
            });
        }
    }

    /*Registering*/
    public void onLoginClick(View view){

        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
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


    public void onLoginSuccess(ProgressDialog progressDialog) {
        loginButton.setEnabled(true);
        progressDialog.dismiss();
        startActivity(new Intent(this, HomePage.class));
        finish();
    }

    public void onLoginFailed() {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
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
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty()) {//|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

            String errorString = "Enter a valid User Name";  // Error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            emailText.setError(spannableStringBuilder);
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            String errorString = "Enter a Valid Password";  // Error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            passwordText.setError(spannableStringBuilder);
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}