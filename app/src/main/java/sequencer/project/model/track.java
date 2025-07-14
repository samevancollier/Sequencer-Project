package sequencer.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sequencer.project.audio.musicroom;

public class track {
    private int trackNumber;
    private int length;
    private instrument instrument;
    private Map<Integer, List<note>> notes; //grid, basically
    private sequence sequence; //owning sequence
    private musicroom musicRoom; //seems weird to have it here, but ok
    private boolean muted = false;
    private boolean soloed = false;
    private int volume; //volume 0-12

    public track(String chosenInstrument, musicroom mR){ //will have to pass in like clicks and stuff later i guess
        this.length = 256; //default values
        this.volume = 6;
        this.musicRoom = mR;
        this.instrument = musicRoom.getInstrument(chosenInstrument);
        this.notes = new HashMap<>();
        
    }
    //add notes
    public void addNote(int step, int pitch, int velocity, int length) {
        note newNote = new note(pitch, step, length);
        notes.computeIfAbsent(step, k -> new ArrayList<>()).add(newNote); //if there is no list for this step, create one, otherwise add to existing list
        System.out.println("note added.");
    }
    //remove notes
    public void removeNote(int step, note note) {
        List<note> stepNotes = notes.get(step);
        if (stepNotes != null) {
            stepNotes.remove(note);
            if (stepNotes.isEmpty()) {
                notes.remove(step); // clean up empty lists
            }
        }
    }
    public void playNote(note note){
        instrument.playNote(note);
    }
    //getters
    public List getNotes(int step){
        return notes.getOrDefault(step, Collections.emptyList());
    }

    public int getLength(){
        return length;
    }

    public instrument getInstrument(){
        return instrument;
    }


}
