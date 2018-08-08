package edu.cmu.eps.scams.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import edu.cmu.eps.scams.MainActivity;
import edu.cmu.eps.scams.files.DirectoryOutputFileFactory;
import edu.cmu.eps.scams.notifications.NotificationFacade;

/*
* This class provides an Android "Service" that instructs a background thread to start and stop
* recording audioRecording from the microphone.
* */
public class RecordingService extends Service {

    private static final String TAG = "RecordingService";
    private static final String HANDLER_THREAD_NAME = "RECORDING_HANDLER_THREAD";
    private static final String RECORDING_DIRECTORY = "recordings";

    private HandlerThread handlerThread;
    private Looper handlerLooper;
    private RecordingServiceHandler serviceHandler;

    public RecordingService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Create recording service handler thread");
        this.handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        this.handlerThread.start();
        this.handlerLooper = this.handlerThread.getLooper();
        this.serviceHandler = new RecordingServiceHandler(
                this.handlerLooper,
                new DirectoryOutputFileFactory(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), RECORDING_DIRECTORY),
                this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Recording service onStartCommand received");
        if (intent != null) {
            Message message = this.serviceHandler.obtainMessage();
            message.arg1 = startId;
            String incomingNumber = intent.getStringExtra("incomingNumber");
            message.arg2 = intent.getIntExtra("operation", RecordingEvents.NONE.ordinal());
            message.getData().putString("incomingNumber", incomingNumber);
            Log.i(TAG, String.format("Recording service received: call from %s", incomingNumber));
            this.serviceHandler.sendMessage(message);
        } else {
            Message message = this.serviceHandler.obtainMessage();
            message.arg1 = startId;
            message.arg2 = RecordingEvents.NONE.ordinal();
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationFacade facade = new NotificationFacade(this);
        Notification notification = facade.buildService(this, pendingIntent,"Scam Detector", "Protection Enabled!");
        startForeground(999888, notification);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "RecordingService is being destroyed by system");
        this.handlerThread.quitSafely();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.w(TAG, "RecordingService task removed");
        super.onTaskRemoved(rootIntent);
    }
}
