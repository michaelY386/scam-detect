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

import edu.cmu.eps.scams.logic.ApplicationLogic;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.model.OutgoingMessage;
import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.logic.model.Association;
import edu.cmu.eps.scams.storage.AppDatabase;
import edu.cmu.eps.scams.storage.RoomStorage;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
/*
* Test the application logic, running on an android device.
* */
@RunWith(AndroidJUnit4.class)
public class TestApplicationLogic {

    private IApplicationLogic logic;
    private AppDatabase database;
    private RoomStorage storage;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        this.database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        this.storage = new RoomStorage(database);
        this.logic = new ApplicationLogic(this.storage);
    }

    @After
    public void closeDb() throws IOException {
        this.database.close();
    }

    @Test
    public void getAppSettings() throws Exception {
        AppSettings settings = this.logic.getAppSettings();
        assertThat(settings.getIdentifier().length(), greaterThan(0));
    }

    @Test
    public void updateAppSettings() throws Exception {
        AppSettings settings = this.logic.getAppSettings();
        assertThat(settings.getIdentifier().length(), greaterThan(0));
        boolean updated = this.logic.updateAppSettings(
                new AppSettings(settings.getIdentifier(),
                        true,
                        settings.getSecret(),
                        "Test 1",
                        settings.getRecovery()));
        assertThat(updated, equalTo(true));
        AppSettings updatedSettings = this.logic.getAppSettings();
        assertThat(updatedSettings.getIdentifier().length(), greaterThan(0));
        assertThat(updatedSettings.getProfile(), equalTo("Test 1"));
    }

    @Test
    public void createAssociation() throws Exception {
        Association created = this.logic.createAssociation("Test", "Test Association 1");
        assertThat(created.getIdentifier().length(), greaterThan(0));
        assertThat(created.getIdentifier(), equalTo("Test Association 1"));
        List<Association> associations = this.logic.getAssociations();
        assertThat(associations.size(), greaterThan(0));
    }

    @Test
    public void getAssociations() throws Exception {
        this.logic.createAssociation("Test", "Test Association 1");
        this.logic.createAssociation("Test", "Test Association 2");
        this.logic.createAssociation("Test", "Test Association 3");
        List<Association> associations = this.logic.getAssociations();
        assertThat(associations.size(), greaterThanOrEqualTo(3));
    }

    @Test
    public void sendMessage() throws Exception {
        AppSettings settings = this.logic.getAppSettings();
        this.logic.createAssociation("Test", settings.getIdentifier());
        OutgoingMessage message = new OutgoingMessage();
        try {
            message.getProperties().put("message", "content");
            this.logic.sendMessage(message);
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }
}