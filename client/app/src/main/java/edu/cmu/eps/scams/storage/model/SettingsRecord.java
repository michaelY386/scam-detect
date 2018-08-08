package edu.cmu.eps.scams.storage.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
/*
 * Local storage model, persisted in a sqlite database on the device.
 * */
@Entity
public class SettingsRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "identifier")
    private String identifier;

    @ColumnInfo(name = "registered")
    private boolean registered;

    @ColumnInfo(name = "secret")
    private String secret;

    @ColumnInfo(name = "profile")
    private String profile;

    @ColumnInfo(name = "recovery")
    private String recovery;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRecovery() {
        return recovery;
    }

    public void setRecovery(String recovery) {
        this.recovery = recovery;
    }
}
