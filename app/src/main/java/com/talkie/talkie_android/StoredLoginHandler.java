package com.talkie.talkie_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talkie.dialect.parser.impl.JsonParsingFacade;
import com.talkie.dialect.parser.impl.MessageTypeMatcher;
import com.talkie.dialect.parser.interfaces.ParsingService;

import java.util.Optional;

public class StoredLoginHandler implements LoginHandler {
    private final static String CRED_KEY = "cred";
    private final ParsingService parsingService;
    private final SharedPreferences settings;
    private final EncryptionService encryptionService;

    public StoredLoginHandler(Context context) {
        parsingService = new JsonParsingFacade(new ObjectMapper(), new MessageTypeMatcher());
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.encryptionService = new EncryptionServiceImpl();
    }

    @Override
    public Optional<Tuple> getStoredCredentials() {
        String serializedTuple = settings.getString(CRED_KEY, "");
        Optional<Tuple> deserialized = parsingService.deserialize(serializedTuple, Tuple.class);
        if(!deserialized.isPresent()){
            return deserialized;
        }
        String encrypted = (String) deserialized.get().getValue();
        String decrypted = encryptionService.decrypt(encrypted);
        Tuple result = new Tuple(deserialized.get().getKey(), decrypted);
        return Optional.of(result);
    }

    @Override
    public void storeCredentials(String login, String password) {
        SharedPreferences.Editor editor = settings.edit();
        String encodedPassword = encryptionService.encode(password);
        Tuple tuple = new Tuple<>(login, encodedPassword);
        String serializedTuple = null;
        try {
            serializedTuple = parsingService.serialize(tuple).orElseThrow(NullPointerException::new);
        } catch (Throwable throwable) {
            Log.e("Serialization failed", throwable.toString());
        }
        editor.putString(CRED_KEY, serializedTuple);
        editor.commit();
    }
}
