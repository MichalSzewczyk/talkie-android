package com.talkie.android.services.impl;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.talkie.android.services.interfaces.MessageCachingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageCachingServiceImpl implements MessageCachingService {
    private final Map<Integer, ArrayAdapter<String>> storage;
    private final Context context;
    private final int id;

    public MessageCachingServiceImpl(Context context, int id) {
        this.context = context;
        this.id = id;
        this.storage = new HashMap<>();
    }

    @Override
    public void persist(Integer userId, Integer fromWho, Integer toWhom, String messageBody) {
        if (!storage.containsKey(userId)) {
            storage.put(userId, new ArrayAdapter<>(context, id, new ArrayList<>()));
        }

        storage.get(userId).add("from: "+fromWho+" to: "+toWhom+" message: "+ messageBody);
    }

    @Override
    public ArrayAdapter<String> getHistory(Integer userId) {
        if (!storage.containsKey(userId))
            storage.put(userId, new ArrayAdapter<>(context, id, new ArrayList<>()));
        return storage.get(userId);
    }
}
