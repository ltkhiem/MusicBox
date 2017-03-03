package com.nimah.khiem.musicbox;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText et_LoginName, et_LoginPass;
    private Button btn_OriginLogin, btn_FacebookLogin;
    private TextView tv_LoginTitle2;
    private CallbackManager callbackManager;
    private LoginButton btn_RealFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);
        InitComponents();

        Typeface sketch_nice_font = Typeface.createFromAsset(getAssets(), "sketch_nice.ttf");
        tv_LoginTitle2.setTypeface(sketch_nice_font);

        CheckLoggedIn();

        btn_OriginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_LoginName.getText().toString();
                String password = et_LoginPass.getText().toString();
                if (username.equals("admin") && password.equals("123456")) {
                    //Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        //Facebook Login
        callbackManager = CallbackManager.Factory.create();
        btn_RealFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(LoginActivity.this, "User ID: " + loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login Attempt Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "Login Attempt Failed", Toast.LENGTH_SHORT).show();
            }
        });

        btn_FacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_RealFacebookLogin.performClick();
            }
        });

    }

    private void CheckLoggedIn() {
        if (isLoggedIn()) {
            //Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void InitComponents() {
        tv_LoginTitle2 = (TextView) findViewById(R.id.tv_logintitle2);
        et_LoginName = (EditText) findViewById(R.id.et_loginname);
        et_LoginPass = (EditText) findViewById(R.id.et_loginpassword);
        btn_RealFacebookLogin = (LoginButton) findViewById(R.id.btn_real_facebook_login);
        btn_FacebookLogin = (Button) findViewById(R.id.btn_facebooklogin);
        btn_OriginLogin = (Button) findViewById(R.id.btn_originlogin);
    }


}
