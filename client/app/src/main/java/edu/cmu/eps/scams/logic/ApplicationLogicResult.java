package edu.cmu.eps.scams.logic;

import java.util.List;

import edu.cmu.eps.scams.logic.model.AppSettings;
import edu.cmu.eps.scams.logic.model.Association;
import edu.cmu.eps.scams.logic.model.History;


/**
 *
 * Wrapper around background task results that are executed for application logic
 */
public class ApplicationLogicResult {
    private final Object result;

    public ApplicationLogicResult(Object result) {
        this.result = result;
    }

    public AppSettings getAppSettings() {
        return (AppSettings) this.result;
    }

    public List<History> getHistories() {
        return (List<History>) this.result;
    }

    public List<Association> getAssociations() {
        return (List<Association>) this.result;
    }
}
