package edu.cmu.eps.scams.storage;

import java.util.List;

import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.logic.model.Association;
import edu.cmu.eps.scams.logic.model.History;

/*
* Interface for local storage access
* */
public interface ILocalStorage {

    void insert(Association toCreate) throws StorageException;

    List<Association> retrieveAssociations() throws StorageException;

    void deleteAssociation(Association association) throws StorageException;

    List<History> retrieveHistory() throws StorageException;

    void insertHistory(String description, long timestamp, String number) throws StorageException;

    void deleteHistory(History history) throws StorageException;

    AppSettings retrieveSettings() throws StorageException;

    void insert(AppSettings settings) throws StorageException;
}
