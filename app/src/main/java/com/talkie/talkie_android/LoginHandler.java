package com.talkie.talkie_android;

import android.app.Activity;

import java.util.Optional;

public interface LoginHandler {
    void storeCredentials(String login, String password);

    Optional<Tuple> getStoredCredentials();
}
