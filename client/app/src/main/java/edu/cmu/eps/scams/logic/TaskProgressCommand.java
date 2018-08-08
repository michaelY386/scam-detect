package edu.cmu.eps.scams.logic;

/*
* Command to run when an application logic task is in progress (update any progress bars or wheels)
* */
public interface TaskProgressCommand {
    void execute(Integer progress);
}
