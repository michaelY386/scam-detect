package edu.cmu.eps.scams.storage.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/*
* Local storage model, persisted in a sqlite database on the device.
* */
@Entity
public class AssociationRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "identifier")
    private String identifier;

    @ColumnInfo(name = "name")
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
