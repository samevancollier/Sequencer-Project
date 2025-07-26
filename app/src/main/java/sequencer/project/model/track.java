package sequencer.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sequencer.project.audio.MusicRoom;

public class Track {
    private int trackNumber;
    private int length;
    private Instrument instrument;
    private Map<Integer, List<Note>> notes; //grid, basically
    private Sequence sequence; //owning sequence
    private MusicRoom musicRoom; //seems weird to have it here, but ok
    private boolean muted = false;
    private boolean soloed = false;
    private int volume; //volume 0-12
    private Map<Integer, List<Integer>> blockedNotes = new HashMap<>();
    AudioEffect[] fX = new AudioEffect[4];

    public Track(String chosenInstrument, int trackNumber){ //will have to pass in like clicks and stuff later i guess
        this.length = 256; //default values
        this.volume = 6;
        this.musicRoom = MusicRoom.getInstance();
        this.instrument = musicRoom.getInstrument(chosenInstrument);
        this.notes = new HashMap<>();
        this.trackNumber = trackNumber;
        
    }
    //add notes
    public void addNote(int step, int pitch, int velocity, int length) {
        if(blockedNotes.containsKey(pitch) && blockedNotes.get(pitch).contains(step)){ //it lets me put a note on the endstep of another note, but i cant visualise if thats fine or not, but should just be a +1 somewhere so no problem
            System.out.println("no sir");
        } else {
            Note newNote = new Note(pitch, step, length, this);
            notes.computeIfAbsent(step, k -> new ArrayList<>()).add(newNote); //if there is no list for this step, create one, otherwise add to existing list
            for(int i=step;i<step+length;i++){
                blockedNotes.computeIfAbsent(pitch, k -> new ArrayList<>()).add(i);
            }
            System.out.println("note added.");
        }
    }
    //remove notes
    public void removeNote(int step, Note note) {
        List<Note> stepNotes = notes.get(step);
        if (stepNotes != null) {
            stepNotes.remove(note);
            if (stepNotes.isEmpty()) {
                notes.remove(step); // clean up empty lists
            }
        }
    }
    private Boolean hasOverlappingNote(int startStep, int pitch, int length){ //turned out to be unneccesary 
        int stopStep=startStep+length;
        for(int i=startStep;i<stopStep;i++){
            if(!notes.containsKey(i)) continue;
            for (Note existingNote : notes.get(i)){
                if(existingNote.getPitch()!=pitch) continue;
                if(!(startStep>existingNote.getStep()) && !(startStep<existingNote.getStep()+existingNote.getLength())) continue;
                if(!(stopStep>existingNote.getStep()) && !(stopStep<existingNote.getStep()+existingNote.getLength())) continue;
                System.out.println("Could not place note here!");
                return true;
                };
            }
            return false;
        }
    
    public short processEffects(short sample) {
        for (AudioEffect effect : fX) {
            if (effect==null){continue;}
            sample = effect.process(sample);
        }
        return sample;
    }
    public void addFX(AudioEffect effect, int fXnum){
        fX[fXnum]=effect;
    }
    public void removeFX(int fXNum){
        fX[fXNum]=null;
    }
        
    
    public void setVolume(int volume){
        this.volume = volume;
    }
    public float getVolumeMultiplier(){
        if(muted){return 0.0f;}
        return volume/12.0f;
    }
    public AudioEffect[] getFX(){
        return fX;
    }
    
    //getters
    public List<Note> getNotes(int step){
        return notes.getOrDefault(step, Collections.emptyList());
    }

    public int getLength(){
        return length;
    }
    public int getVolume(){return volume;}

    public Instrument getInstrument(){
        return instrument;
    }

    public int getTrackNumber(){
        return trackNumber;
    }

    //setter
    public void setTrackNumber(int newTrackNumber){
        this.trackNumber = newTrackNumber;
    }
    public void solo(){soloed=true;}
    public void unsolo(){soloed=false;}
    public void mute(){muted=true;}
    public void unmute(){muted=false;}

}
