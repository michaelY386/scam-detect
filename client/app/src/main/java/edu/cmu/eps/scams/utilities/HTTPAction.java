package edu.cmu.eps.scams.utilities;
/**
 * Created by Ao Chen on 4/3/18.
 * <p>
 * This is a utility class that can be used to send http requests to server.
 * There are three methods: GET, POST and PUT.
 * <p>
 * To use this class, you need add extra lib "org.json", which is available at
 * "https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.json%22%20AND%20a%3A%22json%22"
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.*;

import javax.net.ssl.HttpsURLConnection;


public class HTTPAction {

    private URL urlObj;
    private HttpsURLConnection httpURLConnection;
    private String contentType_;
    private String token;


    public HTTPAction(String url, String contentType) throws Exception {
        urlObj = new URL(url);
        httpURLConnection = (HttpsURLConnection) urlObj.openConnection();
        contentType_ = contentType;

    }

    public void setToken(String token) {
        this.token = token;
        this.httpURLConnection.setRequestProperty("Authorization", String.format("Bearer %s", this.token));
    }

    /**
     * GET method
     *
     * @return
     * @throws Exception
     */
    public JSONObject getRequest() throws Exception {

        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("content_type", contentType_);
        return ReadResponse(httpURLConnection);
    }

    public JSONObject getRequest(JSONObject jsonObject, String accessToken_) throws Exception {

        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("content_type", contentType_);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken_);
        return ReadResponse(httpURLConnection);
    }


    /**
     * POST method
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject postRequest(JSONObject jsonObject) throws Exception {

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("content_type", contentType_);

        WriteInput(httpURLConnection, jsonObject);
        return ReadResponse(httpURLConnection);
    }

    public JSONObject postRequest(JSONObject jsonObject, String accessToken_) throws Exception {

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("content_type", contentType_);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken_);

        WriteInput(httpURLConnection, jsonObject);
        return ReadResponse(httpURLConnection);
    }

    /**
     * PUT method
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject putRequest(JSONObject jsonObject) throws Exception {

        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setRequestProperty("content_type", contentType_);

        WriteInput(httpURLConnection, jsonObject);
        return ReadResponse(httpURLConnection);
    }


    public JSONObject putRequest(JSONObject jsonObject, String accessToken_) throws Exception {

        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setRequestProperty("content_type", contentType_);
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken_);

        WriteInput(httpURLConnection, jsonObject);
        return ReadResponse(httpURLConnection);
    }


    /**
     * Read the response from the server.
     *
     * @param conn
     * @return
     * @throws Exception
     */
    private JSONObject ReadResponse(HttpURLConnection conn) throws Exception {

        int status = conn.getResponseCode();
        String Authorization = "";

        if (status == HttpURLConnection.HTTP_OK) {
            Authorization = conn.getHeaderField("Authorization");
            if (Authorization == null) {
                Authorization = "";
            } else {
                Authorization = Authorization.split(" ")[1]; // get the token
            }
            System.out.println(Authorization);
        } else {
            BufferedReader err = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream())
            );
            String line;
            while ((line = err.readLine()) != null) {
                System.out.println(line);
            }
        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        JSONObject res = new JSONObject(stringBuffer.toString());
        res.put("token", Authorization);
        return res;
    }


    /**
     * Send input data to server.
     *
     * @param connection
     * @param jsonObject
     * @throws Exception
     */
    private void WriteInput(HttpURLConnection connection, JSONObject jsonObject) throws Exception {
        String json = jsonObject.toString();
        byte[] postData = json.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(postData);
        outputStream.close();
    }
}
