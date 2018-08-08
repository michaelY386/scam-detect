package edu.cmu.eps.scams.logic;

import org.json.JSONException;

/**
 * Command for running application logic.
 */
public interface IApplicationLogicCommand {
    public ApplicationLogicResult execute(IApplicationLogic logic) throws JSONException;
}
