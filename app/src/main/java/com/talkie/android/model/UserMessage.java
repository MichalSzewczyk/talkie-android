package com.talkie.android.model;

public class UserMessage {
    private final Integer fromWho;
    private final Integer toWhom;

    private final String messageBody;

    public UserMessage(Integer fromWho, Integer toWhom, String messageBody) {
        this.fromWho = fromWho;
        this.toWhom = toWhom;
        this.messageBody = messageBody;
    }

    public Integer getFromWho() {
        return fromWho;
    }

    public Integer getToWhom() {
        return toWhom;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
