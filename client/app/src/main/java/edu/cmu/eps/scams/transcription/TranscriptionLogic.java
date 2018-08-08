package edu.cmu.eps.scams.transcription;

import android.content.Context;
import android.util.Log;

import java.io.File;

import edu.cmu.eps.scams.classify.ClassifyFacade;
import edu.cmu.eps.scams.logic.ApplicationLogicFactory;
import edu.cmu.eps.scams.logic.IApplicationLogic;
import edu.cmu.eps.scams.logic.model.ClassifierParameters;
import edu.cmu.eps.scams.logic.model.History;
import edu.cmu.eps.scams.logic.model.MessageType;
import edu.cmu.eps.scams.logic.model.OutgoingMessage;
import edu.cmu.eps.scams.logic.model.Telemetry;
import edu.cmu.eps.scams.notifications.NotificationFacade;
import edu.cmu.eps.scams.recordings.AudioRecording;
import edu.cmu.eps.scams.utilities.TimestampUtility;

public class TranscriptionLogic {

    private static final String TAG = "TranscriptionLogic";
    private static final double KNOWN_SCAM_THRESHOLD = 0.40;
    private static final double REVIEWER_THRESHOLD = 0.25;

    public static void handle(Context context, File file, NotificationFacade notifications, long ringTimestamp, String incomingNumber) {
        try {
            IApplicationLogic logic = ApplicationLogicFactory.build(context);
            if (logic.getAppSettings().isRegistered() == true) {
                ClassifierParameters classifierParameters = logic.getClassifierParameters();
                TranscriptionResult result = TranscriptionUtility.transcribe(AudioRecording.ENCODING_NAME, AudioRecording.SAMPLE_RATE, file);
                double scamLikelihood = ClassifyFacade.isScam(result.getText(), result.getConfidence(), ringTimestamp, incomingNumber, classifierParameters);
                if (scamLikelihood > KNOWN_SCAM_THRESHOLD) {
                    Telemetry telemetry = new Telemetry("call", TimestampUtility.now());
                    telemetry.getProperties().put("call.transcript", result.getText());
                    telemetry.getProperties().put("call.transcript.confidence", result.getConfidence());
                    telemetry.getProperties().put("call.timestamp", ringTimestamp);
                    telemetry.getProperties().put("call.number", incomingNumber);
                    telemetry.getProperties().put("call.likelihood", scamLikelihood);
                    logic.sendTelemetry(telemetry);
                    OutgoingMessage message = new OutgoingMessage();
                    message.getProperties().put("call.transcript.confidence", result.getConfidence());
                    message.getProperties().put("call.transcript", result.getText());
                    message.getProperties().put("call.timestamp", ringTimestamp);
                    message.getProperties().put("call.number", incomingNumber);
                    message.getProperties().put("call.likelihood", scamLikelihood);
                    message.getProperties().put("type", MessageType.KNOWN);
                    logic.sendMessage(message);
                    notifications.create(context, "Scam Call Detected!", String.format("Call from %s is likely a scam", incomingNumber));
                    logic.createHistory(new History("Scam call detected", incomingNumber, ringTimestamp));
                } else if (scamLikelihood > REVIEWER_THRESHOLD) {
                    Telemetry telemetry = new Telemetry("call", TimestampUtility.now());
                    telemetry.getProperties().put("call.transcript.confidence", result.getConfidence());
                    telemetry.getProperties().put("call.timestamp", ringTimestamp);
                    telemetry.getProperties().put("call.number", incomingNumber);
                    telemetry.getProperties().put("call.likelihood", scamLikelihood);
                    logic.sendTelemetry(telemetry);
                    OutgoingMessage message = new OutgoingMessage();
                    message.getProperties().put("call.transcript.confidence", result.getConfidence());
                    message.getProperties().put("call.transcript", result.getText());
                    message.getProperties().put("call.timestamp", ringTimestamp);
                    message.getProperties().put("call.number", incomingNumber);
                    message.getProperties().put("call.likelihood", scamLikelihood);
                    message.getProperties().put("type", MessageType.REVIEW);
                    logic.sendMessage(message);
                    logic.createHistory(new History("Suspicious call", incomingNumber, ringTimestamp));
                } else {
                    Telemetry telemetry = new Telemetry("call", TimestampUtility.now());
                    telemetry.getProperties().put("call.transcript.confidence", result.getConfidence());
                    telemetry.getProperties().put("call.timestamp", ringTimestamp);
                    telemetry.getProperties().put("call.number", incomingNumber);
                    telemetry.getProperties().put("call.likelihood", scamLikelihood);
                    logic.sendTelemetry(telemetry);
                }
                Log.d(TAG, String.format("Transcription: %s with %f likelihood = %f", result.getText(), result.getConfidence(), scamLikelihood));
            }
        } catch (Exception exception) {
            Log.w(TAG, String.format("Transcription encountered error: %s", exception.getMessage()));
        }
    }
}
