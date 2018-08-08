package edu.cmu.eps.scams.logic.model;

import org.json.JSONException;

/**
 * Format for notification message content, parsed from JSON
 */
public class ReviewMessageContent extends MessageContent {

    public ReviewMessageContent(String jsonText) throws JSONException {
        super(jsonText);
    }

    public String getTranscript() throws JSONException {
        return this.jsonObject.getString("call.transcript");
    }

    public String getPhoneNumber() throws JSONException {
        return this.jsonObject.getString("call.number");
    }


}
