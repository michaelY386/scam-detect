package edu.cmu.eps.scams.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.notifications.NotificationFacade;
import edu.cmu.eps.scams.transcription.TranscriptionRunnable;

/**
 * Another background service. This one handles sending audio recordings to google for transcription.
 */
public class TranscriptionService extends Service {

    private static final String TAG = "TranscriptionService";
    private static final String HANDLER_THREAD_NAME = "TranscriptionServiceThread";
    private HandlerThread handlerThread;
    private Looper handlerLooper;
    private Handler handler;

    public TranscriptionService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Transcription service created");
        this.handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        this.handlerThread.start();
        this.handlerLooper = this.handlerThread.getLooper();
        this.handler = new Handler(this.handlerLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Transcription service started");
        int result = START_STICKY;
        if (intent == null || intent.getLongExtra("ring_timestamp", -1) < 0) {
            Log.i(TAG, "Transcription service no event");
            result = START_STICKY;
        } else {
            String audioRecordingPath = intent.getStringExtra("audio_recording");
            String incomingNumber = intent.getStringExtra("incoming_number");
            long ringTimestamp = intent.getLongExtra("ring_timestamp", -1);
            long audioLength = intent.getLongExtra("audio_length", -1);
            this.handler.post(new TranscriptionRunnable(
                    audioRecordingPath,
                    incomingNumber,
                    ringTimestamp,
                    audioLength,
                    new NotificationFacade(this),
                    this));
        }
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "transcription Service is being destroyed by system");
        this.handlerThread.quitSafely();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.w(TAG, "Transcription Service task removed");
        super.onTaskRemoved(rootIntent);
    }
}
