package edu.cmu.eps.scams.logic;

import android.os.AsyncTask;
import android.util.Log;


/**
 *
 * Android requires file and network functions to run on background threads, so we use this AsyncTask
 * class to accomplish this. Built specifically for wrapping application logic functions.
 */
public class ApplicationLogicTask extends AsyncTask<IApplicationLogicCommand, Integer, ApplicationLogicResult> {

    private static final String TAG = "ApplicationLogicTask";
    private final IApplicationLogic logic;
    private final TaskProgressCommand progressCommand;
    private final TaskCompletionCommand completionCommand;

    public ApplicationLogicTask(IApplicationLogic logic, TaskProgressCommand progressCommand, TaskCompletionCommand completionCommand) {
        this.logic = logic;
        this.progressCommand = progressCommand;
        this.completionCommand = completionCommand;
    }

    @Override
    protected ApplicationLogicResult doInBackground(IApplicationLogicCommand... command) {
        try {
            return command[0].execute(this.logic);
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed on execution due to json format error"));
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        this.progressCommand.execute(progress[0]);
    }

    @Override
    protected void onPostExecute(ApplicationLogicResult result) {
        this.completionCommand.execute(result);

    }
}
