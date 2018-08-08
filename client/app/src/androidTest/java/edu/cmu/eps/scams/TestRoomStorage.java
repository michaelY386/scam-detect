package edu.cmu.eps.scams;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.logic.model.Association;
import edu.cmu.eps.scams.logic.model.History;
import edu.cmu.eps.scams.storage.AppDatabase;
import edu.cmu.eps.scams.storage.RoomStorage;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/*
* Test the local storage implementation
* */
@RunWith(AndroidJUnit4.class)
public class TestRoomStorage {

    private RoomStorage storage;
    private AppDatabase database;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        this.database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        this.storage = new RoomStorage(database);
    }

    @After
    public void closeDb() throws IOException {
        this.database.close();
    }

    @Test
    public void insertAssociation() throws Exception {
        Association toCreate = new Association("jeremy","test");
        this.storage.insert(toCreate);
        List<Association> results = this.storage.retrieveAssociations();
        assertThat(results.size(), greaterThan(0));
    }

    @Test
    public void deleteAssociation() throws Exception {
        Association toCreate = new Association("jeremy","test");
        this.storage.insert(toCreate);
        Association toCreate2 = new Association("jeremy","test2");
        this.storage.insert(toCreate2);
        List<Association> results = this.storage.retrieveAssociations();
        assertThat(results.size(), greaterThan(0));
        this.storage.deleteAssociation(toCreate2);
        List<Association> updateResults = this.storage.retrieveAssociations();
        assertThat(results.size(), greaterThan(updateResults.size()));
    }

    @Test
    public void insertHistory() throws Exception {
        this.storage.insertHistory("Test", 100, "123-456-7890");
        List<History> results = this.storage.retrieveHistory();
        assertThat(results.size(), greaterThan(0));
    }

    @Test
    public void deleteHistory() throws Exception {
        this.storage.insertHistory("Test", 100, "123-456-7890");
        this.storage.insertHistory("Test2", 100, "123-456-7890");
        List<History> results = this.storage.retrieveHistory();
        assertThat(results.size(), greaterThan(0));
        this.storage.deleteHistory(results.get(0));
        List<History> updateResults = this.storage.retrieveHistory();
        assertThat(results.size(), greaterThan(updateResults.size()));
    }

    @Test
    public void insertSettings() throws Exception {
        this.storage.insert(new AppSettings("Testing", true, "secret", "profile", "recovery"));
        AppSettings results = this.storage.retrieveSettings();
        assertThat(results.getIdentifier(), equalTo("Testing"));
        this.storage.insert(new AppSettings("Testing", true, "secret", "profile", "recovery"));
        AppSettings updateResults = this.storage.retrieveSettings();
        assertThat(updateResults.getIdentifier(), equalTo("Testing2"));
    }
}