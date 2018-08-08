package edu.cmu.eps.scams.logic.model;

/*
* Currently only implement notification messages so one device telling another device something.
* */
public enum MessageType {
    NOTIFY,
    BLOCK,
    KNOWN, REVIEW
}
