package edu.cmu.eps.scams.logic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy on 4/13/2018.
 * Represent a past action by the software such as classifying a call
 */

public class History {

    private int id;
    public long Time;
    public String PhoneNumber;
    private String description;

    public History(String description, String PhoneNumber, long Time) {
        this.description = description;
        this.Time = Time;
        this.PhoneNumber = PhoneNumber;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeOfCall() {
        return this.Time;
    }

    public String getPhoneNumber() {
        return this.PhoneNumber;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}



