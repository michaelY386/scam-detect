package edu.cmu.eps.scams.communication;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerResponse {

    private final JSONObject body;

    public ServerResponse(JSONObject body) {
        this.body = body;
    }

    public JSONObject getBody() {
        return body;
    }
}
