package edu.cmu.eps.scams.communication;

import org.json.*;
import edu.cmu.eps.scams.utilities.HTTPAction;


public class Register{
    private String identifer_;
    private String secret_;
    private String profile_;
    private String recovery_;
    private String url;


    /**
     * The constructor. Initialize the parameters and set the url.
     * @param identifer
     * @param secret
     * @param profile
     * @param recovery
     */
    public Register(String identifer, String secret, String profile, String recovery) {
        identifer_ = identifer;
        secret_ = secret;
        profile_ = profile;
        recovery_ = recovery;
        url = "https://eps-scams.appspot.com/api/authentication/register";
    }


    /**
     * This func makes a JSON object and send it to the url.
     * @return JSONObject. This func will return a JSON object, which is the response from server.
     * @throws Exception
     */
    public JSONObject send() throws Exception{
        HTTPAction action = new HTTPAction(url, "application/json");
        JSONObject data = new JSONObject();
        data.put("identifier", identifer_);
        data.put("secret", secret_);
        data.put("profile", profile_);
        data.put("recovery", recovery_);
        JSONObject response = action.postRequest(data);
        return response;
    }
}