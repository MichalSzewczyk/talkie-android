package com.talkie.talkie_android;


import android.util.Base64;

public class EncryptionServiceImpl implements EncryptionService {
    @Override
    public String decrypt(String encoded) {
        byte[] decoded = Base64.decode(encoded, 0);
        return new String(decoded);
    }

    @Override
    public String encode(String pass) {
        byte[] encoded = Base64.encode(pass.getBytes(), 0);
        return new String(encoded);
    }
}
