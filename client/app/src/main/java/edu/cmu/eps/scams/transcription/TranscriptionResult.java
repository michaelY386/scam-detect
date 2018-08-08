package edu.cmu.eps.scams.transcription;
/*
* Results of a tracsription.
* */
public class TranscriptionResult {

    private final String text;

    private final double confidence;


    public TranscriptionResult(String text, double confidence) {
        this.text = text;
        this.confidence = confidence;
    }

    public String getText() {
        return text;
    }

    public double getConfidence() {
        return confidence;
    }
}
