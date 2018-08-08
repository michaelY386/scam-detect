package edu.cmu.eps.scams.storage.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import edu.cmu.eps.scams.storage.model.HistoryRecord;
import edu.cmu.eps.scams.storage.model.SettingsRecord;
/**
 *
 * Access for settings records on the devices storage.
 */
@Dao
public interface SettingsRecordAccess {
    @Query("SELECT * FROM settingsrecord ORDER BY id DESC")
    List<SettingsRecord> getAll();

    @Query("SELECT * FROM settingsrecord WHERE id IN (:settingsRecordIds)")
    List<SettingsRecord> loadAllByIds(int[] settingsRecordIds);

    @Insert
    void insertAll(SettingsRecord... settingsRecords);

    @Delete
    void delete(SettingsRecord settingsRecord);
}