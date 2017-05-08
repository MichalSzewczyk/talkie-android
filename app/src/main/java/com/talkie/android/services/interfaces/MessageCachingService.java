package com.talkie.android.services.interfaces;

import android.widget.ArrayAdapter;

import com.talkie.android.model.UserMessage;

import java.util.ArrayList;

public interface MessageCachingService {
    void persist(Integer userId, Integer fromWho, Integer toWhom, String messageBody);

    ArrayAdapter<String> getHistory(Integer userId);
}
