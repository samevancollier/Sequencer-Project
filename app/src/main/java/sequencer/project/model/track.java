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
    private Project sequence; //owning sequence
    private MusicRoom musicRoom; //seems weird to have it here, but ok
    private boolean muted = false;
    private boolean soloed = false;
    private ArrayList<Block> blocks;
    private static final int STEPS_PER_BLOCK=256;
    private int volume; //volume 0-12
    private Map<Integer, List<Integer>> blockedNotes = new HashMap<>();
    AudioEffect[] fX = new AudioEffect[4];

    private volatile float currentAmplitude = 0.0f;
    private static final float AMPLITUDE_DECAY = 0.4f;

    public Track(String chosenInstrument, int trackNumber){ //will have to pass in like clicks and stuff later i guess
        this.length = 256; //default values
        this.volume = 6;
        this.musicRoom = MusicRoom.getInstance();
        this.instrument = musicRoom.getInstrument(chosenInstrument);
        this.notes = new HashMap<>();
        this.trackNumber = trackNumber;
        this.blocks=new ArrayList<Block>();
        
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
            //System.out.println("note added.");
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

    public void swapBlocks(int index1,int index2){
        Collections.swap(blocks,index1,index2);
    }

    public List<Note> getNotesAtStep(int absoluteStep){         //HERE
        int blockIndex=absoluteStep/STEPS_PER_BLOCK;
        int relativeStep=absoluteStep%STEPS_PER_BLOCK;
        
        if(blockIndex>=0 && blockIndex<blocks.size()){
            return blocks.get(blockIndex).getNotesAtStep(relativeStep);
        }
        return null; 
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
        System.out.println("FX ADDED");
        fX[fXnum]=effect;
    }
    public void removeFX(int fXNum){
        System.out.println("FX REMOVED");

        fX[fXNum]=null;
    }

    public void updateAmplitude(float newAmplitude){
        // always apply some decay first
        this.currentAmplitude *= AMPLITUDE_DECAY;
        // then take the max of decayed value and new amplitude
        this.currentAmplitude = Math.max(newAmplitude, this.currentAmplitude);
    }

    // add a separate decay method for when no audio is playing
    public void decayAmplitude(){
        this.currentAmplitude *= AMPLITUDE_DECAY;
        if(this.currentAmplitude < 0.001f){
            this.currentAmplitude = 0.0f; // snap to zero when very small
        }
    }

    public float getCurrentAmplitude(){return currentAmplitude;}
        
    
    public void setVolume(int volume){
        System.out.println("VOLUME SET: "+volume);
        this.volume = volume;
    }
    public float getVolumeMultiplier(){
        if(muted){return 0.0f;}
    
        
        if(volume == 0) return 0.0f;
        float normalizedVolume = volume / 9.0f; // 0.11 to 1.0
        return normalizedVolume * normalizedVolume;
    }
    public AudioEffect[] getFX(){
        return fX;
    }
    public void setInstrument(Instrument newInstrument){instrument=newInstrument;}
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

    public ArrayList<Block> getBlocks(){return blocks;}

    //setter
    public void setTrackNumber(int newTrackNumber){
        this.trackNumber = newTrackNumber;
    }
    public void solo(){soloed=true;}
    public void unsolo(){soloed=false;}
    public void mute(){muted=true;}
    public void unmute(){muted=false;}

}
