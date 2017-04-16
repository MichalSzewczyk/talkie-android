package com.talkie.talkie_android;

public interface EncryptionService {
    String decrypt(String encrypted);
    String encode(String pass);
}
