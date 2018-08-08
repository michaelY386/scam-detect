package edu.cmu.eps.scams.recordings;

import java.io.IOException;

/**
 * Created by jeremy on 3/17/2018.
 */

class RecordingException extends Throwable {
    public RecordingException(IOException e) {
        super(e);
    }

    public RecordingException(String message, Exception cause) {
        super(message, cause);
    }
}
