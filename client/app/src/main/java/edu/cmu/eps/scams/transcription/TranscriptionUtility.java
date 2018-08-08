package edu.cmu.eps.scams.transcription;

import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import static android.util.Base64.DEFAULT;

/**
 * Created by jeremy on 3/19/2018.
 * Ugly HTTP code to get google speech API to transcribe our audio recordings.
 */

public class TranscriptionUtility {

    private static final String TAG = "TranscriptionUtility";

    //gcloud auth print-access-token
    private static final String KEY = "AIzaSyDLSInp1mdGsuOgEzZ0LsI5Me5ZwGPlipQ";

    public static TranscriptionResult transcribe(String encoding, int sampleRate, File file) throws IOException, TranscriptionException {
        byte[] data = TranscriptionUtility.readFile(file);
        String dataString = android.util.Base64.encodeToString(data, Base64.NO_WRAP);
        return TranscriptionUtility.transcribe(encoding, sampleRate, dataString);
    }

    public static TranscriptionResult transcribe(String encoding, int sampleRate, String dataString) throws IOException, TranscriptionException {
        String postString = TranscriptionUtility.buildJsonRequest(encoding, sampleRate, dataString);
        String result = "{}";
        byte[] postData = postString.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = String.format("https://speech.googleapis.com/v1/speech:recognize?key=%s", KEY);
        URL url = new URL(request);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        try {
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            connection.setUseCaches(false);
            try (DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                dataOutputStream.write(postData);
            }
            result = TranscriptionUtility.readInputStreamToString(connection.getInputStream());
            return TranscriptionUtility.parseJsonResults(result);
        } catch (IOException exception) {
            String errorMessage = TranscriptionUtility.readInputStreamToString(connection.getErrorStream());
            Log.e(TAG, String.format("Encountered error on request to Google: %s", errorMessage));
            return new TranscriptionResult("", 0);
        } catch (JSONException exception) {
            Log.e(TAG, String.format("Encountered json error on request to Google: %s", exception.getMessage()));
            return new TranscriptionResult("", 0);
        }
    }

    public static TranscriptionResult parseJsonResults(String jsonString) throws JSONException {
        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray resultsArray = rootObject.getJSONArray("results");
        JSONObject resultItem = resultsArray.getJSONObject(0);
        JSONArray alternativesArray = resultItem.getJSONArray("alternatives");
        JSONObject alternativeItem = alternativesArray.getJSONObject(0);
        String text = alternativeItem.getString("transcript");
        double confidence = alternativeItem.getDouble("confidence");
        return new TranscriptionResult(text, confidence);
    }

    public static String readInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder results = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
        }
        return results.toString();
    }

    public static String buildJsonRequest(String encoding, int sampleRate, String content) {
        return "{\n" +
                //String.format("'key': '%s'\n", KEY) +
                "'config': {\n" +
                String.format("'encoding': '%s',\n", encoding) +
                String.format("'sampleRateHertz': %d,\n", sampleRate) +
                "'languageCode': 'en-US',\n" +
                "'enableWordTimeOffsets': false\n" +
                "},\n" +
                "'audio': {\n" +
                String.format("'content': '%s'\n", content) +
                "}" +
                "}";
    }

    public static byte[] readFile(File file) throws IOException {
        int size = (int) file.length();
        byte[] buffer = new byte[size];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(buffer, 0, size);
        }
        return buffer;

    }
}
