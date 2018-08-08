package edu.cmu.eps.scams.recordings;

import java.io.File;
import java.util.Collection;

/**
 * Created by jeremy on 3/17/2018.
 * An interface for abstracting recording events
 */

public interface IRecorder {

    boolean isRecording();

    void start(File target);

    Collection<PhoneCallResult> loopEvent();

    PhoneCallResult stop();
}
