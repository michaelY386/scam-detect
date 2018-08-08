package edu.cmu.eps.scams.communication;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.eps.scams.logic.model.OutgoingMessage;
import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.logic.model.IncomingMessage;
import edu.cmu.eps.scams.logic.model.Telemetry;
import edu.cmu.eps.scams.utilities.TimestampUtility;

/**
 * Contains logic for interacting with server (mostly authentication wrapping server calls)
 */
public class ServerFacade implements IServerFacade {

    private String identifier;
    private String secret;
    private String profile;
    private String recovery;
    private String token;
    private final Gson gson;
    private boolean initialized;

    public ServerFacade() {
        this.gson = new Gson();
        this.initialized = false;
    }


    /**
     * Initialize the parameters.
     * @param isRegistered
     * @param identifier
     * @param secret
     * @param profile
     * @param recovery
     * @throws JSONException
     * @throws CommunicationException
     */
    public void init(boolean isRegistered, String identifier, String secret, String profile, String recovery) throws JSONException, CommunicationException {
        this.identifier = identifier;
        this.secret = secret;
        this.profile = profile;
        this.recovery = recovery;
        String result = null;
        // If user has already registered, directly login, else register.
        if (isRegistered) {
            ServerResponse loginResponse = ServerApi.login(this.identifier, this.secret);
            JSONObject response = loginResponse.getBody();
            JSONObject resultItem = response.getJSONObject("result");
            result = resultItem.getString("access_token");
        } else {
            ServerResponse registerResponse = ServerApi.register(this.identifier, this.secret, this.profile, this.recovery);
            JSONObject registerObject = registerResponse.getBody();
            ServerResponse loginResponse = ServerApi.login(this.identifier, this.secret);
            JSONObject loginObject = loginResponse.getBody();
            JSONObject resultItem = loginObject.getJSONObject("result");
            result = resultItem.getString("access_token");
        }
        this.initialized = true;
        this.token = result;
    }

    public void updateIdentity(String profile, String recovery) throws JSONException, CommunicationException {
        this.profile = profile;
        this.recovery = recovery;
        ServerApi.updateIdentity(
                this.token,
                this.profile,
                this.recovery);
    }

    /**
     * Send messages to server.
     * @param toSend
     * @throws CommunicationException
     */
    @Override
    public void sendMessage(OutgoingMessage toSend) throws CommunicationException {
        try {
            ServerApi.createMessage(
                    this.token,
                    toSend.getRecipient(),
                    this.gson.toJson(toSend.getProperties()),
                    TimestampUtility.now());
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
    }


    /**
     * Inform server the messsage has been received.
     * @param toAcknowledge
     * @throws CommunicationException
     */
    @Override
    public void acknowledgeMessage(IncomingMessage toAcknowledge) throws CommunicationException {
        try {
            ServerResponse response = ServerApi.updateMessage(
                    this.token,
                    toAcknowledge.getIdentifier(),
                    TimestampUtility.now());
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
    }


    /**
     * Get all messages from server.
     * @return A list of messages.
     * @throws CommunicationException
     */
    @Override
    public List<IncomingMessage> retrieveMessages() throws CommunicationException {
        try {
            List<IncomingMessage> output = new ArrayList<>();
            ServerResponse response = ServerApi.getMessages(this.token);
            JSONObject responseObject = response.getBody();
            JSONArray resultsArray = responseObject.getJSONArray("result");
            // Iterate the JSON array and write all the elements into a list.
            for (int resultsIndex = 0; resultsIndex < resultsArray.length(); resultsIndex++) {
                JSONObject resultObject = resultsArray.getJSONObject(resultsIndex);
                String identifier = resultObject.getString("identifier");
                String sender = resultObject.getString("sender");
                String recipient = resultObject.getString("recipient");
                String content = resultObject.getString("content");
                int state = resultObject.getInt("state");
                int created = resultObject.getInt("created");
                int received = resultObject.opt("received") == null ? 0 : resultObject.getInt("received");
                Object recipientReceivedValue = resultObject.opt("recipient_received");
                int recipientReceived = 0;
                if (recipientReceivedValue instanceof Integer) {
                    recipientReceived = resultObject.getInt("recipient_received");
                }
                output.add(new IncomingMessage(
                        identifier,
                        sender,
                        recipient,
                        content,
                        state,
                        (long)created,
                        (long)received,
                        (long)recipientReceived
                ));
            }
            return output;
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
    }


    /**
     * Add a telemetry record to server database.
     * @param toSend
     * @throws CommunicationException
     */
    @Override
    public void sendTelemetry(Telemetry toSend) throws CommunicationException {
        try {
            ServerResponse response = ServerApi.createTelemetry(
                    this.token,
                    toSend.getDataType(),
                    this.gson.toJson(toSend.getProperties()),
                    toSend.getCreated());
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
    }


    /**
     * Get classifier parameters from server.
     * @return
     * @throws CommunicationException
     */
    @Override
    public ClassifierParameters retrieveClassifierParameters() throws CommunicationException {
        try {
            ServerResponse response = ServerApi.getClassifierParameters(this.token);
            JSONObject responseObject = response.getBody();
            JSONObject resultObject = responseObject.getJSONObject("result");
            String content = resultObject.getString("content");
            return new ClassifierParameters(content);
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
    }


    /**
     * Check the state of initialization.
     * @return
     */
    public boolean isInitialized() {
        return initialized;
    }
}
