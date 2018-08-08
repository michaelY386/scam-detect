package edu.cmu.eps.scams.logic;

/*
* Command to run when an application logic task is complete (e.g. update the UI in here)
* */
public interface TaskCompletionCommand {
    void execute(ApplicationLogicResult result);
}
