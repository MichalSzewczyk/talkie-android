package com.talkie.android.services.interfaces;

import com.talkie.android.utils.Tuple;

import java.util.Optional;

public interface LoginHandler {
    void storeCredentials(String login, String password);

    Optional<Tuple> getStoredCredentials();

    boolean isDataAvailable();
}
