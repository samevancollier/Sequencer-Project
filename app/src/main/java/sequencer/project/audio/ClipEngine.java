package sequencer.project.audio;

import javax.sound.sampled.Clip;

import sequencer.project.model.*;

public class ClipEngine implements AudioEngine {
    public void playNote(Instrument instrument, Note note){
        Clip clip = instrument.getSample(note.getPitch());
        if(instrument.getinstrumentType()==InstrumentType.DRUMS){
            if (clip == null) return;
            if(clip.isRunning()){
                        clip.stop();
                    }
            clip.setFramePosition(0);
            clip.start();
        } else {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    public void stopNote(Clip clip) {
    if (clip != null) {
        clip.stop();
    }
}
}
