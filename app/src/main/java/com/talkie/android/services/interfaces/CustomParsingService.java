package com.talkie.android.services.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.talkie.dialect.parser.impl.JsonParsingFacade;
import com.talkie.dialect.parser.impl.MessageTypeMatcher;

public class CustomParsingService extends JsonParsingFacade {
    public CustomParsingService() {
        super(new ObjectMapper(), new MessageTypeMatcher());
    }
}
