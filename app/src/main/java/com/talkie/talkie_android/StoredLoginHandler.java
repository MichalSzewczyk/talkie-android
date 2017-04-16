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
    private static StoredLoginHandler instance;
    private static Context context;

    private StoredLoginHandler(Context context) {
        parsingService = new JsonParsingFacade(new ObjectMapper(), new MessageTypeMatcher());
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.encryptionService = new EncryptionServiceImpl();
    }

    public static void init(Context context){
        if(context != null) throw new IllegalStateException("Reinitialization of login handler.");
        StoredLoginHandler.context = context;
    }

    public static StoredLoginHandler getStoredLoginHandler (){
        if(instance == null){
            synchronized (instance) {
                if(instance == null) {
                    instance = new StoredLoginHandler(context);
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized Optional<Tuple> getStoredCredentials() {
        String serializedTuple = settings.getString(CRED_KEY, "null");
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
    public synchronized boolean isDataAvailable() {
        return settings.contains(CRED_KEY);
    }

    @Override
    public synchronized void storeCredentials(String login, String password) {
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
