package edu.cmu.eps.scams.recordings;

import android.media.AudioRecord;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static edu.cmu.eps.scams.recordings.AudioRecording.AUDIO_SOURCE;
import static edu.cmu.eps.scams.recordings.AudioRecording.BUFFER_SIZE;
import static edu.cmu.eps.scams.recordings.AudioRecording.CHANNEL_MASK;
import static edu.cmu.eps.scams.recordings.AudioRecording.ENCODING;
import static edu.cmu.eps.scams.recordings.AudioRecording.READ_BUFFER_SIZE;
import static edu.cmu.eps.scams.recordings.AudioRecording.SAMPLE_RATE;

/**
 * Created by jeremy on 3/11/2018.
 * A bunch of android audio recording garbage. Basically record the audio and write to file.
 */
public class AudioRecordFacade extends BaseRecorder {

    private static final String TAG = "AudioRecordFacade";


    private AudioRecord recorder;
    private AudioRecordOutputStrategy outputStrategy;
    private final byte[] buffer;
    private final int intervals;
    private final int intervalLength;

    public AudioRecordFacade() {
        this.buffer = new byte[READ_BUFFER_SIZE];
        this.intervals = 1;
        this.intervalLength = 10000;
    }

    @Override
    protected void startRecording(File target) throws RecordingException {
        Log.d(TAG, "Starting recording with AudioRecord");
        try {
            this.recorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);
            Log.d(TAG, "Built AudioRecord");
            this.outputStrategy = new AudioRecordOutputStrategy(target, this.intervals, this.intervalLength);
            Log.d(TAG, "Starting to record");
            this.recorder.startRecording();
            Log.d(TAG, "Initializing output");
            this.outputStrategy.init();
            Log.d(TAG, "Recording in progress");
        } catch (IOException exception) {
            Log.d(TAG, String.format("Exception during start of recording: %s", exception.getMessage()));
        }
    }

    @Override
    public Collection<PhoneCallResult> loopEvent() {
        Collection<PhoneCallResult> results = new ArrayList<>();
        if (this.isRecording()) {
            Log.d(TAG, "Recording loop event");
            Collection<AudioRecording> recordings = this.readFromAudioRecord(false);
            for (AudioRecording recording : recordings) {
                results.add(new PhoneCallResult(recording.startTime, recording.file, (recording.endTime - recording.startTime)));
            }
        }
        return results;
    }

    private Collection<AudioRecording> readFromAudioRecord(boolean completion) {
        Collection<AudioRecording> results = new ArrayList<>();
        int read = this.recorder.read(buffer, 0, buffer.length);
        if (read > 0) {
            Log.d(TAG, String.format("Read %d bytes", read));
            results = this.outputStrategy.write(buffer, 0, read);
        }
        if (completion) {
            while (read > 0) {
                read = this.recorder.read(buffer, 0, buffer.length);
                if (read > 0) {
                    Log.d(TAG, String.format("Read %d bytes", read));
                    results = this.outputStrategy.write(buffer, 0, read);
                }
            }
        }
        return results;
    }

    @Override
    public PhoneCallResult stopRecording() throws RecordingException {
        try {
            Log.d(TAG, "Stopping recording");
            if (this.recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                this.recorder.stop();
            }
        } catch (IllegalStateException exception) {
            Log.d(TAG, String.format("Exception during stopping of recording: %s", exception.getMessage()));
        }
        Collection<AudioRecording> recordings = this.readFromAudioRecord(true);
        recordings.addAll(this.outputStrategy.finish());
        if (this.recorder.getState() == AudioRecord.STATE_INITIALIZED) {
            Log.d(TAG, "Releasing AudioRecord");
            this.recorder.release();
        }
        return null;
    }
}
