package com.talkie.android.services.interfaces;

public interface SocketService {
    void sendMessage(int from, int to, String message);
    void connect();
}
