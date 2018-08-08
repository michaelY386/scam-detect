package edu.cmu.eps.scams.communication;

import java.sql.Timestamp;
import java.time.Instant;
import org.json.*;
import edu.cmu.eps.scams.utilities.HTTPAction;
import edu.cmu.eps.scams.utilities.TimestampUtility;


public class Messages{
    private String recipient_;
    private String content_;
    private String created;
    private String accessToken_;
    private String url;


    /**
     * The constructor. Initialize the parameters and set the url.
     * @param recipient
     * @param content
     * @param accessToken
     */
    public Messages(String recipient, String content, String accessToken){
        recipient_ = recipient;
        content_ = content;
        created = GetTime();
        accessToken_ = accessToken;
        url = "https://eps-scams.appspot.com/api/messages/";
    }


    /**
     * Create the new message on server database.
     * @return JSONObject. The response from server.
     * @throws Exception
     */
    public JSONObject Create() throws Exception{
        JSONObject data = MakeJson();
        HTTPAction action = new HTTPAction(url, "application/json");
        JSONObject response = action.postRequest(data, accessToken_);
        return response;
    }


    /**
     * Retrieve all messages from the server database.
     * @return JSONObject. The response from server.
     * @throws Exception
     */
    public JSONObject Retrieve() throws Exception{
        JSONObject data = MakeJson();
        HTTPAction action = new HTTPAction(url, "application/json");
        JSONObject response = action.getRequest(data, accessToken_);
        return response;
    }


    /**
     * Update the message in server database.
     * @param accessToken_
     * @return JSONObject. The response from server.
     * @throws Exception
     */
    public JSONObject Update(String accessToken_) throws Exception{
        JSONObject data = MakeJson();
        HTTPAction action = new HTTPAction(url, "application/json");
        JSONObject response = action.putRequest(data, accessToken_);
        return response;
    }


    /**
     * Get the current time.
     * @return JSONObject.
     */
    private String GetTime() {
        return String.valueOf(TimestampUtility.now());
    }


    /**
     * Pack data to JSON format.
     * @return JSONObject.
     * @throws JSONException
     */
    private JSONObject MakeJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("recipient", recipient_);
        obj.put("content", content_);
        obj.put("created", created);
        return obj;
    }
}
