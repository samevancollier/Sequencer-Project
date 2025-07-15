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

    public Track(String chosenInstrument, MusicRoom mR, int trackNumber){ //will have to pass in like clicks and stuff later i guess
        this.length = 256; //default values
        this.volume = 6;
        this.musicRoom = mR;
        this.instrument = musicRoom.getInstrument(chosenInstrument);
        this.notes = new HashMap<>();
        this.trackNumber = trackNumber;
        
    }
    //add notes
    public void addNote(int step, int pitch, int velocity, int length) {
        Note newNote = new Note(pitch, step, length);
        notes.computeIfAbsent(step, k -> new ArrayList<>()).add(newNote); //if there is no list for this step, create one, otherwise add to existing list
        System.out.println("note added.");
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
    public void playNote(Note note){
        instrument.playNote(note);
    }
    //getters
    public List getNotes(int step){
        return notes.getOrDefault(step, Collections.emptyList());
    }

    public int getLength(){
        return length;
    }

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

}
