package com.talkie.android.services.interfaces;

public interface MessageService {
    void sendMessage(int from, int to, String message);
    void connect();
}
