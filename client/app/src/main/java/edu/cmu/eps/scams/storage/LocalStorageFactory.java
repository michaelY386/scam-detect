package edu.cmu.eps.scams.storage;

import android.arch.persistence.room.Room;
import android.content.Context;

/*
* Factory for local storage structure, its expensive, so only create a few...
* */
public class LocalStorageFactory {

    private static AppDatabase appDatabase;

    public static ILocalStorage build(Context applicationContext) {
        if (appDatabase == null) {
            AppDatabase database = Room.databaseBuilder(applicationContext, AppDatabase.class, "scams-app-db").build();
            LocalStorageFactory.appDatabase = database;
        }
        return new RoomStorage(LocalStorageFactory.appDatabase);
    }
}
