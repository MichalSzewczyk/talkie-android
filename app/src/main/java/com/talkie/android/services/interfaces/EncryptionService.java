package com.talkie.android.services.interfaces;

public interface EncryptionService {
    String decrypt(String encrypted);
    String encode(String pass);
}
