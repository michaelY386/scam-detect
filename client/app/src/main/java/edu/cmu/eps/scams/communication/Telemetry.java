package edu.cmu.eps.scams.communication;


import java.time.Instant;
import org.json.*;
import edu.cmu.eps.scams.utilities.HTTPAction;
import edu.cmu.eps.scams.utilities.TimestampUtility;


public class Telemetry {
    private String dataType_;
    private String content_;
    private String created;
    private String accessToken_;
    private String url;

    /**
     * The Constructor. Initialize the parameters and set the url.
     * @param dataType
     * @param content
     * @param accessToken
     */
    public Telemetry(String dataType, String content, String accessToken) {
        dataType_ = dataType;
        content_ = content;
        accessToken_ = accessToken;
        created = GetTime();
        url = "https://eps-scams.appspot.com/api/telemetry/";
    }

    /**
     * Create a new telemetry record in server database.
     * @return JSONObject. The resopnse from server.
     * @throws Exception
     */
    public JSONObject create() throws Exception{
        JSONObject obj = MakeJson();
        HTTPAction action = new HTTPAction(url, "application/json");
        JSONObject response = action.postRequest(obj, accessToken_);
        return response;
    }

    /**
     * Get the current time.
     * @return
     */
    private String GetTime() {
        return String.valueOf(TimestampUtility.now());
    }

    /**
     * Pack data to JSON format.
     * @return
     * @throws JSONException
     */
    private JSONObject MakeJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("data_type", dataType_);
        obj.put("content", content_);
        obj.put("created", created);
        return obj;
    }
}