package edu.cmu.eps.scams.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import edu.cmu.eps.scams.storage.access.AssociationRecordAccess;
import edu.cmu.eps.scams.storage.access.HistoryRecordAccess;
import edu.cmu.eps.scams.storage.access.SettingsRecordAccess;
import edu.cmu.eps.scams.storage.model.AssociationRecord;
import edu.cmu.eps.scams.storage.model.HistoryRecord;
import edu.cmu.eps.scams.storage.model.SettingsRecord;

/**
 *
 * Some android library stuff for using a SQLite database for local storage on the client.
 */
@Database(entities = {AssociationRecord.class, HistoryRecord.class, SettingsRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AssociationRecordAccess associationRecordAccess();
    public abstract HistoryRecordAccess historyRecordAccess();
    public abstract SettingsRecordAccess settingsRecordAccess();
}
