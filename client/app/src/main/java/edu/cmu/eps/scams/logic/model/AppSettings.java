package edu.cmu.eps.scams.logic.model;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;

/**
 * Created by jeremy on 4/13/2018.
 * Some application settings classes for application logic
 */
public class AppSettings {

    private static final int IDENTIFIER_BYTES = 16;
    private final String identifier;
    private final boolean registered;
    private final String secret;
    private final String profile;
    private final String recovery;
    private final JSONObject profileJson;

    public AppSettings(String identifier, boolean registered, String secret, String profile, String recovery) throws JSONException {
        this.identifier = identifier;
        this.registered = registered;
        this.secret = secret;
        this.profile = profile;
        this.recovery = recovery;
        this.profileJson = new JSONObject(this.profile);
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getSecret() {
        return secret;
    }

    public String getProfile() {
        return profile;
    }

    public String getRecovery() {
        return recovery;
    }

    public static AppSettings defaults() throws JSONException {
        SecureRandom random = new SecureRandom();
        byte identifierBytes[] = new byte[IDENTIFIER_BYTES];
        random.nextBytes(identifierBytes);

        byte secretBytes[] = new byte[IDENTIFIER_BYTES];
        random.nextBytes(secretBytes);

        return new AppSettings(
                android.util.Base64.encodeToString(identifierBytes, Base64.NO_WRAP),
                false,
                android.util.Base64.encodeToString(secretBytes, Base64.NO_WRAP),
                "{}",
                "{}"
        );
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(String.format("\"registered\": \"%s\",", this.registered));
        result.append(String.format("\"identifier\": \"%s\",", this.identifier));
        result.append(String.format("\"secret\": \"%s\",", this.secret));
        result.append(String.format("\"profile\": \"%s\",", this.profile));
        result.append(String.format("\"recovery\": \"%s\",", this.recovery));
        result.append("}");
        return result.toString();
    }

    public String getName() throws JSONException {
        return this.profileJson.getString("name");
    }

    public String getUserType() throws JSONException {
        return this.profileJson.getString("userType");
    }
}
