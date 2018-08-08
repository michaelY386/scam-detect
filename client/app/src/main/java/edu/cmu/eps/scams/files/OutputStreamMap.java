package edu.cmu.eps.scams.files;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jeremy on 3/17/2018.
 * Build a map of output streams. Used to write audio recording to multiple streams for different
 * recording lengths: 0-10 seconds, 0-30 seconds, 0-60 seconds
 */
public class OutputStreamMap {

    private static final String TAG = "OutputStreamMap";

    private final TreeMap<String, OutputStream> map;

    public OutputStreamMap() {
        this.map = new TreeMap<>();
    }

    public void add(String key, OutputStream value) {
        this.map.put(key, value);
    }

    public OutputStream remove(String name) {
        return this.map.remove(name);
    }

    public Map<String, Boolean> write(byte[] data, int offset, int length) {
        Map<String, Boolean> results = new TreeMap<>();
        for (Map.Entry<String, OutputStream> item : this.map.entrySet()) {
            try {
                item.getValue().write(data, offset, length);
                results.put(item.getKey(), true);
            } catch (IOException exception) {
                Log.d(TAG, String.format("Failed to write to output stream: %s", exception.getMessage()));
                results.put(item.getKey(), false);
            }
        }
        return results;
    }
}
