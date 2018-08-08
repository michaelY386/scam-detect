package edu.cmu.eps.scams.storage.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import edu.cmu.eps.scams.storage.model.AssociationRecord;
import edu.cmu.eps.scams.storage.model.HistoryRecord;

/**
 *
 * Access for association records on the devices storage.
 */

@Dao
public interface AssociationRecordAccess {

    @Query("SELECT * FROM associationrecord")
    List<AssociationRecord> getAll();

    @Query("SELECT * FROM associationrecord WHERE id IN (:associationRecordIds)")
    List<AssociationRecord> loadAllByIds(int[] associationRecordIds);


    @Query("SELECT * FROM associationrecord WHERE identifier = :identifier")
    AssociationRecord getByIdentifier(String identifier);

    @Insert
    void insertAll(AssociationRecord... associationRecords);

    @Delete
    void delete(AssociationRecord associationRecord);
}