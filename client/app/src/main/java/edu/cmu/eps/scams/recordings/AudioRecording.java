package edu.cmu.eps.scams.recordings;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import java.io.File;

/**
 * Created by jeremy on 3/18/2018.
 * A class to represent details of an audio recording.
 */
public class AudioRecording {


    public static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    public static final int SAMPLE_RATE = 44100;
    public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public static final String ENCODING_NAME = "LINEAR16";
    public static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
    public static final int BUFFER_SIZE = 500 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);
    public static final int READ_BUFFER_SIZE = BUFFER_SIZE / 2;

    public final File file;
    public final long startTime;
    public final long endTime;
    public final String identifier;

    public AudioRecording(File file, long startTime, long endTime, String identifier) {
        this.file = file;
        this.startTime = startTime;
        this.endTime = endTime;
        this.identifier = identifier;
    }

    public boolean shouldStop(long time) {
        boolean result = false;
        if (time >= endTime) {
            result = true;
        }
        return result;
    }
}
