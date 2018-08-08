package edu.cmu.eps.scams.logic;

import android.content.Context;

import edu.cmu.eps.scams.storage.LocalStorageFactory;

/**
 * Build an implementation of the application logic using the provided Context
 * */
public class ApplicationLogicFactory {

    public static IApplicationLogic build(Context context) {
        return new ApplicationLogic(LocalStorageFactory.build(context));
    }
}
