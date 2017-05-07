package com.talkie.android.rest.tasks;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.talkie.android.R;
import com.talkie.android.activities.MessengerActivity;
import com.talkie.dialect.messages.enums.DatabaseOperationMessage;
import com.talkie.dialect.messages.model.User;
import com.talkie.dialect.parser.interfaces.ParsingService;

import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class UserLoginTask extends AbstractTask<User> {
    protected final String login;
    protected final String mPassword;
    private final ParsingService parsingService;
    protected final Activity activity;

    public UserLoginTask(String login, String password, ParsingService parsingService, Activity activity) {
        this.login = login;
        this.mPassword = password;
        this.parsingService = parsingService;
        this.activity = activity;
    }

    @Override
    protected User doInBackground(Void... params) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForEntity(ORIGIN, String.format(LOGIN_REQUEST, login, mPassword), String.class).getBody();
        result = result.replace("{\"login\":", "");
        result = result.replace("}}", "}");
        Optional<User> deserializedResult = parsingService.deserialize(result, User.class);
        try {
            return deserializedResult.orElseThrow(() -> new RuntimeException("Deserialization failed."));
        } catch (Throwable throwable) {
            Log.e("Deserialization failed.", throwable.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(final User result) {
        if (result == null) {
            return;
        }
        switch (DatabaseOperationMessage.valueOf(result.getMessage())) {
            case SUCCESS:
                handleSuccess(result);
                break;
            default:
                throw new RuntimeException("Unsupported login result.");

        }
    }

    protected void handleSuccess(User result) {
        Intent intent = new Intent(activity, MessengerActivity.class);
        String serializedResult = null;
        try {
            serializedResult = parsingService.serialize(result).orElseThrow(() -> new RuntimeException("Could not serialize result"));
        } catch (Throwable throwable) {
            Log.e(throwable.getLocalizedMessage(), throwable.toString());
        }
        intent.putExtra(activity.getString(R.string.user_data), serializedResult);
        activity.startActivity(intent);
    }

}
