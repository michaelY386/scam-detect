package edu.cmu.eps.scams.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import edu.cmu.eps.scams.files.DirectoryOutputFileFactory;

/*
* Another background service in Android. This one handles polling the server for new messages.
* */
public class MessagingService extends Service {

    private static final String TAG = "MessagingService";
    private static final String HANDLER_THREAD_NAME = "MESSAGING_HANDLER_THREAD";

    private HandlerThread handlerThread;
    private Looper handlerLooper;
    private MessagingServiceHandler serviceHandler;

    public MessagingService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Create messaging service handler thread");
        this.handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        this.handlerThread.start();
        this.handlerLooper = this.handlerThread.getLooper();
        this.serviceHandler = new MessagingServiceHandler(
                this.handlerLooper,
                this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Messaging service onStartCommand received");
        if (intent != null) {
            Message message = this.serviceHandler.obtainMessage();
            message.arg1 = startId;
            this.serviceHandler.sendMessage(message);
        } else {
            Message message = this.serviceHandler.obtainMessage();
            message.arg1 = startId;
            this.serviceHandler.sendMessage(message);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed by system");
        this.handlerThread.quitSafely();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "task removed");
        super.onTaskRemoved(rootIntent);
    }
}
