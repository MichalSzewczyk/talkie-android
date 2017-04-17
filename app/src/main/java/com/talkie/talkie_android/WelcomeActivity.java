package com.talkie.talkie_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;


public class WelcomeActivity extends AppCompatActivity {
    private Button fingerprintLoginButton;
    private Button normalLoginButton;
    private LoginHandler loginHandler;

    private void initElements(){
        fingerprintLoginButton = (Button) findViewById(R.id.fingerprint_login_button);
        normalLoginButton = (Button) findViewById(R.id.normal_login_button);
        if(loginHandler.isDataAvailable()) normalLoginButton.setEnabled(false);
    }

    private void setListeners(){
        fingerprintLoginButton.setOnClickListener((a) -> {
            Intent intent = new Intent(this, FingerprintActivity.class);
            startActivity(intent);
        });

        normalLoginButton.setOnClickListener((a) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoredLoginHandler.init(this.getApplicationContext());
        loginHandler = StoredLoginHandler.getStoredLoginHandler();
        setContentView(R.layout.activity_welcome);
        initElements();
        setListeners();
    }
}
