package com.talkie.android;

import com.talkie.android.services.interfaces.CustomParsingService;
import com.talkie.dialect.parser.interfaces.ParsingService;

public enum ParsingServiceType {
    CUSTOM_SERVICE(CustomParsingService.class);
    private final Class<? extends ParsingService> service;

    ParsingServiceType(Class<? extends ParsingService> service) {
        this.service = service;
    }

    public Class<? extends ParsingService> getService() {
        return service;
    }
}
