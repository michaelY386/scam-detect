package edu.cmu.eps.scams.transcription;

import android.content.Context;
import android.util.Log;

import java.io.File;

import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.classify.ClassifyFacade;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.model.MessageType;
import edu.cmu.eps.scams.logic.model.OutgoingMessage;
import edu.cmu.eps.scams.logic.model.Telemetry;
import edu.cmu.eps.scams.notifications.NotificationFacade;
import edu.cmu.eps.scams.recordings.AudioRecording;
import edu.cmu.eps.scams.utilities.TimestampUtility;

/**
 * Created by jeremy on 3/19/2018.
 * Thread runnnable for getting a transcription of a call recording.
 */
public class TranscriptionRunnable implements Runnable {

    private static final String TAG = "TranscriptionRunnable";
    private static final double KNOWN_SCAM_THRESHOLD = 0.40;
    private static final double REVIEWER_THRESHOLD = 0.25;
    private final File file;
    private final String incomingNumber;
    private final long ringTimestamp;
    private final long audioLength;
    private ClassifierParameters classifierParameters;
    private IApplicationLogic logic;
    private final NotificationFacade notifications;
    private final Context context;

    public TranscriptionRunnable(String audioRecordingPath,
                                 String incomingNumber,
                                 long ringTimestamp,
                                 long audioLength,
                                 NotificationFacade notifications,
                                 Context context) {
        this.file = new File(audioRecordingPath);
        this.incomingNumber = incomingNumber;
        this.ringTimestamp = ringTimestamp;
        this.audioLength = audioLength;
        this.notifications = notifications;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            this.logic = ApplicationLogicFactory.build(this.context);
            if (this.logic.getAppSettings().isRegistered() == true) {
                this.classifierParameters = this.logic.getClassifierParameters();
                TranscriptionResult result = TranscriptionUtility.transcribe(AudioRecording.ENCODING_NAME, AudioRecording.SAMPLE_RATE, this.file);
                Log.d(TAG, String.format("Transcribed: %s", result.getText()));
                double scamLikelihood = ClassifyFacade.isScam(result.getText(), result.getConfidence(), ringTimestamp, incomingNumber, this.classifierParameters);
                if (scamLikelihood > KNOWN_SCAM_THRESHOLD) {
                    Telemetry telemetry = new Telemetry("call", TimestampUtility.now());
                    telemetry.getProperties().put("call.transcript", result.getText());
                    telemetry.getProperties().put("call.transcript.confidence", result.getConfidence());
                    telemetry.getProperties().put("call.timestamp", this.ringTimestamp);
                    telemetry.getProperties().put("call.number", this.incomingNumber);
                    telemetry.getProperties().put("call.likelihood", scamLikelihood);
                    this.logic.sendTelemetry(telemetry);
                    OutgoingMessage message = new OutgoingMessage();
                    message.getProperties().put("call.transcript.confidence", result.getConfidence());
                    message.getProperties().put("call.transcript", result.getText());
                    message.getProperties().put("call.timestamp", this.ringTimestamp);
                    message.getProperties().put("call.number", this.incomingNumber);
                    message.getProperties().put("call.likelihood", scamLikelihood);
                    message.getProperties().put("type", MessageType.KNOWN);
                    this.logic.sendMessage(message);
                    this.notifications.create(this.context, "Scam Call Detected!", String.format("Call from %s is likely a scam", this.incomingNumber));
                } else if (scamLikelihood > REVIEWER_THRESHOLD) {
                    Telemetry telemetry = new Telemetry("call", TimestampUtility.now());
                    telemetry.getProperties().put("call.transcript.confidence", result.getConfidence());
                    telemetry.getProperties().put("call.timestamp", this.ringTimestamp);
                    telemetry.getProperties().put("call.number", this.incomingNumber);
                    telemetry.getProperties().put("call.likelihood", scamLikelihood);
                    this.logic.sendTelemetry(telemetry);
                    OutgoingMessage message = new OutgoingMessage();
                    message.getProperties().put("call.transcript.confidence", result.getConfidence());
                    message.getProperties().put("call.transcript", result.getText());
                    message.getProperties().put("call.timestamp", this.ringTimestamp);
                    message.getProperties().put("call.number", this.incomingNumber);
                    message.getProperties().put("call.likelihood", scamLikelihood);
                    message.getProperties().put("type", MessageType.REVIEW);
                    this.logic.sendMessage(message);
                } else {
                    Telemetry telemetry = new Telemetry("call", TimestampUtility.now());
                    telemetry.getProperties().put("call.transcript.confidence", result.getConfidence());
                    telemetry.getProperties().put("call.timestamp", this.ringTimestamp);
                    telemetry.getProperties().put("call.number", this.incomingNumber);
                    telemetry.getProperties().put("call.likelihood", scamLikelihood);
                    this.logic.sendTelemetry(telemetry);
                }
                Log.d(TAG, String.format("Transcription: %s with %f likelihood = %f", result.getText(), result.getConfidence(), scamLikelihood));
            }
        } catch (Exception exception) {
            Log.w(TAG, String.format("Transcription encountered error: %s", exception.getMessage()));
        }
    }
}
