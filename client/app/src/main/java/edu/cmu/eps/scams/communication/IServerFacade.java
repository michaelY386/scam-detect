package edu.cmu.eps.scams.communication;

import java.util.List;

import edu.cmu.eps.scams.logic.model.OutgoingMessage;
import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.logic.model.IncomingMessage;
import edu.cmu.eps.scams.logic.model.Telemetry;
/*
 * Interface for server functions
 */
public interface IServerFacade {

    void sendMessage(OutgoingMessage toSend) throws CommunicationException;

    void acknowledgeMessage(IncomingMessage toAcknowledge) throws CommunicationException;

    List<IncomingMessage> retrieveMessages() throws CommunicationException;

    void sendTelemetry(Telemetry toSend) throws CommunicationException;

    ClassifierParameters retrieveClassifierParameters() throws CommunicationException;
}
