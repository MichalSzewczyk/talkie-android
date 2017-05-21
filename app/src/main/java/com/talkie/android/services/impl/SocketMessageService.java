package com.talkie.android.services.impl;

import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.talkie.android.factories.ParsingServiceFactory;
import com.talkie.android.services.interfaces.MessageService;
import com.talkie.dialect.MessageType;
import com.talkie.dialect.messages.requests.SendMessage;
import com.talkie.dialect.parser.interfaces.ParsingService;
import com.talkie.dialect.payloads.SendMessagePayload;

import java.net.URISyntaxException;
import java.sql.Timestamp;

import static com.github.nkzawa.engineio.client.Socket.EVENT_MESSAGE;
import static com.talkie.android.ParsingServiceType.CUSTOM_SERVICE;

public class SocketMessageService implements MessageService {
    private static final String SOCKET_CONNECTION_ERROR = "Failed to establish socket connection due to reason: %s";
    private static final Long CONNECTION_TIMEOUT = Long.MAX_VALUE;
    private final Socket socket;
    private final ParsingService parsingService;

    public SocketMessageService(String host, String port, Integer userId) {
        try {
//            IO.Options opts = new IO.Options();
//            opts.transports = new String[]{ WebSocket.NAME};
//            this.socket = IO.socket(host+":"+port, opts);
//            System.out.println("foo"+socket.io().timeout(CONNECTION_TIMEOUT));
//
//            socket.connect();
//            socket.open();
//
//            socket.send("hello");

            IO.Options opts = new IO.Options();
            opts.transports = new String[]{ WebSocket.NAME};
            opts.timeout = CONNECTION_TIMEOUT;
            opts.reconnection = true;
            opts.reconnectionAttempts = 10;
            opts.reconnectionDelay = 1000;
            opts.forceNew = true;
            socket = IO.socket(host+":"+port, opts);
            socket.connect();
            socket.open();
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format(SOCKET_CONNECTION_ERROR, e));
        }
        this.parsingService = ParsingServiceFactory.getService(CUSTOM_SERVICE);
    }



    @Override
    public void sendMessage(int from, int to, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setId(from);
        sendMessage.setType(MessageType.SEND_MESSAGE.toString());
        SendMessagePayload payload = new SendMessagePayload();
        payload.setBody(message);
        payload.setReceiverId(to);
        payload.setTimestamp(new Timestamp(System.currentTimeMillis()));
        sendMessage.setPayload(payload);
        socket.emit(EVENT_MESSAGE, message);

        System.out.println("foo"+socket.connected());

    }

    @Override
    public void connect() {

    }
}
