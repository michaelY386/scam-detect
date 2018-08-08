package edu.cmu.eps.scams.logic.model;

import org.json.JSONException;
import org.json.JSONObject;

/*
* Messages received from the server
* */
public class IncomingMessage {

    private final String identifier;
    private final String sender;
    private final String recipient;
    private final int state;
    private final Long created;
    private final Long received;
    private final Long recipientReceived;
    private final String content;
    private JSONObject contentObject;

    public IncomingMessage(String identifier, String sender, String recipient, String content, int state, Long created, Long received, Long recipientReceived) {
        this.identifier = identifier;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.state = state;
        this.created = created;
        this.received = received;
        this.recipientReceived = recipientReceived;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getState() {
        return state;
    }

    public Long getCreated() {
        return created;
    }

    public Long getReceived() {
        return received;
    }

    public Long getRecipientReceived() {
        return recipientReceived;
    }

    public String getContent() {
        return content;
    }

    public String getType() throws JSONException {
        JSONObject jsonObject = new JSONObject(this.getContent());
        return jsonObject.getString("type");
    }


}
