package com.talkie.android.services.impl;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.talkie.android.rest.tasks.UserLoginTask;
import com.talkie.android.services.interfaces.LoginHandler;
import com.talkie.android.utils.Tuple;
import com.talkie.dialect.parser.interfaces.ParsingService;

@TargetApi(24)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;
    private LoginHandler loginHandler;
    private ParsingService parsingService;
    private Activity activity;

    public FingerprintHandler(Context mContext, ParsingService parsingService, Activity activity) {
        this.context = mContext;
        this.loginHandler = new StoredLoginHandler(mContext);
        this.parsingService = parsingService;
        this.activity = activity;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
        PackageManager pm = activity.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, activity.getClass()),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        Tuple crud = loginHandler.getStoredCredentials().orElse(null);
        UserLoginTask loginTask = new UserLoginTask(crud.getKey(), crud.getValue(), parsingService, activity);
        loginTask.execute();
    }

}