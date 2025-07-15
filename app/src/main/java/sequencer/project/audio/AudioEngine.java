package sequencer.project.audio;

import javax.sound.sampled.Clip;

import sequencer.project.model.*;

public interface AudioEngine {
    void playNote(Instrument instrument, Note note);
    void stopNote(Clip clip);
}
