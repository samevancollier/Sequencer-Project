package sequencer.project.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.Clip;

import sequencer.project.model.*;

public class AudioPlayer {
    private Sequence sequence;
    private int currentStep = 0;
    private boolean isPlaying = false;
    private boolean isPaused = true;
    private AudioEngine audioEngine = new ClipEngine();
    private Thread playbackThread;
    private List<ActiveNote> activeNotes = new ArrayList<>();

    public AudioPlayer(Sequence sequence){
        this.sequence = sequence;
    }

    private static class ActiveNote {
        Clip clip;
        long stopStep;
        int pitch;
        ActiveNote(Clip clip, int stopStep, int pitch) {   //helper class to track active notes
            this.clip = clip;
            this.stopStep = stopStep;
            this.pitch = pitch;
        }
    }

    //uhhhhh
    public void play(){
        if(isPlaying)return;
        isPlaying=true; isPaused = false;
        playbackThread = new Thread(this::playbackLoop);
        playbackThread.start();
    }
    //uhhhhhhhhh
    private void playbackLoop(){
        System.out.println("Entered playbackLoop");
        while (isPlaying && !Thread.currentThread().isInterrupted()){
            if(!isPaused && currentStep <= sequence.getLength()){
                //stopNotesAtStep(currentStep);
                
                playNotesAtStep();
                currentStep++;
                try {
                    Thread.sleep((60000/(sequence.getBPM()))/16);
                } catch (InterruptedException e) {
                    break; 
                }
            }  
        }
        isPlaying=false; isPaused=true;
    }
    //PLAY ALL NOTES ON A PARTICULAR STEP
    private void playNotesAtStep(){
        for(Track track : sequence.getTracks()){
            Instrument instrument = track.getInstrument();
            InstrumentType instrumentType = instrument.getinstrumentType(); //for some reason, apparently it would be good to use an enum?
            List<Note> notes = track.getNotes(currentStep);
            if(notes!=null){
                for (Note note : notes) {

                    
                    audioEngine.playNote(instrument, note);
                    if(instrumentType!=InstrumentType.DRUMS){
                        ActiveNote activeNote = new ActiveNote(instrument.getSample(note.getPitch()), currentStep+note.getLength(), note.getPitch());
                        activeNotes.add(activeNote); 
                    } 
                }
            }
        }
    }

    private void stopNotesAtStep(int step) {
        System.out.println("stopping note");
        Iterator<ActiveNote> iterator = activeNotes.iterator();
        while (iterator.hasNext()) {
            ActiveNote activeNote = iterator.next();
            if (activeNote.stopStep == step) {
                audioEngine.stopNote(activeNote.clip);
                iterator.remove();
            }
        }
    }

    public void stop(){ //stop/pause
        Iterator<ActiveNote> iterator = activeNotes.iterator();
        while(iterator.hasNext()){
            ActiveNote activeNote = iterator.next();
            audioEngine.stopNote(activeNote.clip);
        }
    }

    public boolean playing(){
        return isPlaying;
    }
}
