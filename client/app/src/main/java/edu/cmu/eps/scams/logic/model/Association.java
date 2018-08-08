package edu.cmu.eps.scams.logic.model;

/**
 * Created by jeremy on 4/13/2018.
 * Reviewer and Primary users are associations for each other
 */
public class Association {

    private final String identifier;
    private final String name;

    public Association(String name, String identifier) {
        this.identifier = identifier;
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }
}
