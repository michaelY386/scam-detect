package edu.cmu.eps.scams.logic.model;

import org.json.JSONException;
import org.json.JSONObject;

/*
* Base class for parsing JSON messages (JSON so they can have flexible formatting).
* */
public abstract class MessageContent {

    private final String jsonText;
    protected final JSONObject jsonObject;

    public MessageContent(String jsonText) throws JSONException {
        this.jsonText = jsonText;
        this.jsonObject = new JSONObject(jsonText);
    }

    public String getType() throws JSONException {
        return this.jsonObject.getString("type");
    }

}
