package com.talkie.android.rest.tasks;

import android.os.AsyncTask;
//http://52.42.71.54:90/
public abstract class AbstractTask<T> extends AsyncTask<Void, Void, T> {
    protected final static String ORIGIN = "http://10.0.2.2:8090";
    protected final static String LOGIN_REQUEST = "            query{\n" +
            "              login(login:\"%s\", password:\"%s\"){\n" +
            "                name\n" +
            "                lastName\n" +
            "                avatar\n" +
            "                id\n" +
            "                friends{\n" +
            "                    id\n" +
            "                    name\n" +
            "                    lastName\n" +
            "                    avatar\n" +
            "                    description\n" +
            "                }\n" +
            "                error\n" +
            "                message\n" +
            "              }\n" +
            "            }";
}
