package edu.cmu.eps.scams.communication;

public class CommunicationException extends Exception {

    public CommunicationException(Exception e) {
        super(e);
    }

    public CommunicationException(String message) {
        super(message);
    }
}
