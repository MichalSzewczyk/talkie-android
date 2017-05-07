package com.talkie.android.factories;

import com.talkie.android.ParsingServiceType;
import com.talkie.dialect.parser.interfaces.ParsingService;

import java.util.HashMap;
import java.util.Map;

public class ParsingServiceFactory {
    private final static Map<ParsingServiceType, ParsingService> services = new HashMap<>();
    public static ParsingService getService(ParsingServiceType parsingServiceType){
        if(services.containsKey(parsingServiceType)){
            return services.get(parsingServiceType);
        } else {
            ParsingService tmp = null;
            try {
                tmp = parsingServiceType.getService().newInstance();
                services.put(parsingServiceType, tmp);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return tmp;
        }
    }
}
