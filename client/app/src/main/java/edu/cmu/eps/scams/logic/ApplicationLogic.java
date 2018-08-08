package edu.cmu.eps.scams.logic;

import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.eps.scams.communication.CommunicationException;
import edu.cmu.eps.scams.communication.ServerFacade;
import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.logic.model.Association;
import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.logic.model.History;
import edu.cmu.eps.scams.logic.model.IncomingMessage;
import edu.cmu.eps.scams.logic.model.OutgoingMessage;
import edu.cmu.eps.scams.logic.model.Telemetry;
import edu.cmu.eps.scams.storage.ILocalStorage;
import edu.cmu.eps.scams.storage.StorageException;

/**
 * Implementation of applciation logic for interfacing with UI.
 */
public class ApplicationLogic implements IApplicationLogic {

    private static final String TAG = "ApplicationLogic";

    private final ILocalStorage storage;
    private final ServerFacade serverFacade;

    public ApplicationLogic(ILocalStorage storage) {
        this.storage = storage;
        this.serverFacade = new ServerFacade();
    }

    @Override
    public Association createAssociation(String name, String qrValue) {
        Association toCreate = new Association(name, qrValue);
        try {
            storage.insert(toCreate);
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to create Association due to %s", e.getMessage()));
        }
        return toCreate;
    }

    @Override
    public List<Association> getAssociations() {
        List<Association> results = new ArrayList<>();
        try {
            results = storage.retrieveAssociations();
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to retrieve Associations due to %s", e.getMessage()));
        }
        return results;
    }

    @Override
    public boolean removeAssociation(Association association) {
        boolean result = true;
        try {
            storage.deleteAssociation(association);
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to delete Association due to %s", e.getMessage()));
            result = false;
        }
        return result;
    }

    @Override
    public List<History> getHistory() {
        List<History> results = new ArrayList<>();
        try {
            results = storage.retrieveHistory();
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to retrieve History due to %s", e.getMessage()));
        }
        return results;
    }

    @Override
    public boolean removeHistory(History history) {
        boolean result = true;
        try {
            storage.deleteHistory(history);
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to delete History due to %s", e.getMessage()));
            result = false;
        }
        return result;
    }

    @Override
    public AppSettings getAppSettings() {
        try {
            AppSettings result = storage.retrieveSettings();
            Log.d(TAG, String.format("Retrieved settings = %s", result.toString()));
            return result;
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to retrieve Settings due to %s", e.getMessage()));
            try {
                AppSettings settings = AppSettings.defaults();
                storage.insert(settings);
                return settings;
            } catch (StorageException e1) {
                Log.d(TAG, String.format("Failed to insert default Settings due to %s", e1.getMessage()));
            } catch (JSONException e1) {
                Log.d(TAG, String.format("Failed to build default Settings due to %s", e1.getMessage()));
            }
            return null;
        }
    }

    @Override
    public boolean updateAppSettings(AppSettings appSettings) {
        boolean result = true;
        try {
            storage.insert(appSettings);
            this.serverFacade.updateIdentity(appSettings.getProfile(), appSettings.getRecovery());
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to insert Settings due to %s", e.getMessage()));
            result = false;
        } catch (JSONException e) {
            result = false;
            e.printStackTrace();
        } catch (CommunicationException e) {
            Log.d(TAG, String.format("Failed send settings to server due to %s", e.getMessage()));
            result = false;
        }
        return result;
    }

    @Override
    public void sendTelemetry(Telemetry telemetry) {
        try {
            this.initializeServer();
            this.serverFacade.sendTelemetry(telemetry);
        } catch (StorageException | JSONException | CommunicationException e) {
            Log.d(TAG, String.format("Failed to send telemetry due to %s", e.getMessage()));
        }
    }

    private void initializeServer() throws StorageException, CommunicationException, JSONException {
        if (this.serverFacade.isInitialized() == false) {
            AppSettings settings = this.storage.retrieveSettings();
            this.serverFacade.init(
                    settings.isRegistered(),
                    settings.getIdentifier(),
                    settings.getSecret(),
                    settings.getProfile(),
                    settings.getRecovery());
        }
    }

    @Override
    public OutgoingMessage sendMessage(OutgoingMessage outgoingMessage) {
        try {
            this.initializeServer();
            if (outgoingMessage.getRecipient() == null || outgoingMessage.getRecipient().isEmpty()) {
                List<Association> associations = this.getAssociations();
                for (Association association : associations) {
                    outgoingMessage.setRecipient(association.getIdentifier());
                    this.serverFacade.sendMessage(outgoingMessage);
                }
            } else {
                this.serverFacade.sendMessage(outgoingMessage);
            }
            return outgoingMessage;
        } catch (StorageException | JSONException | CommunicationException e) {
            Log.d(TAG, String.format("Failed to send message due to %s", e.getMessage()));
            return outgoingMessage;
        }
    }

    @Override
    public void createHistory(History item) {
        try {
            this.storage.insertHistory(item.getDescription(), item.getTimeOfCall(), item.getPhoneNumber());
        } catch (StorageException e) {
            Log.d(TAG, String.format("Failed to create history due to %s", e.getMessage()));
        }
    }

    @Override
    public List<IncomingMessage> receiveMessages() {
        try {
            this.initializeServer();
            return this.serverFacade.retrieveMessages();
        } catch (StorageException | JSONException | CommunicationException e) {
            Log.d(TAG, String.format("Failed to retrieve messages due to %s", e.getMessage()));
            return new ArrayList<>();
        }
    }

    @Override
    public IncomingMessage acknowledgeMessage(IncomingMessage message) {
        try {
            this.initializeServer();
            this.serverFacade.acknowledgeMessage(message);
            return message;
        } catch (StorageException | JSONException | CommunicationException e) {
            Log.d(TAG, String.format("Failed to acknowledge message due to %s", e.getMessage()));
            return message;
        }
    }

    @Override
    public ClassifierParameters getClassifierParameters() {
        try {
            this.initializeServer();
            return this.serverFacade.retrieveClassifierParameters();
        } catch (StorageException | JSONException | CommunicationException e) {
            Log.d(TAG, String.format("Failed to retrieve parameters due to %s", e.getMessage()));
            return ClassifierParameters.defaults();
        }
    }
}
