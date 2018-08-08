package edu.cmu.eps.scams.logic.model;

import java.util.Map;
import java.util.TreeMap;

/*
* Telemetry model for sending to server.
* */
public class Telemetry {
    private final TreeMap<String, Object> properties;
    private final String dataType;
    private final long created;

    public Telemetry(String dataType, long created) {
        this.dataType = dataType;
        this.created = created;
        this.properties = new TreeMap<String, Object>();
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getDataType() {
        return dataType;
    }

    public long getCreated() {
        return created;
    }
}
