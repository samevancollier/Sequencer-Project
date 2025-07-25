package sequencer.project.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sequencer.project.GUI.ClipArea;
import sequencer.project.GUI.TrackRow;
//SHOULD BE IN MODEL
public class Block {
    private int startStep;
    private Map<Integer, List<Note>> notes;
    private TrackRow trackRow;
    private Track track;
    private ClipArea clipArea;
    private Map<Integer, List<Integer>> blockedNotes=new HashMap<>();
    
    public Block(int startStep, TrackRow trackRow){
        this.startStep=startStep;
        this.trackRow=trackRow;
        this.track=this.trackRow.getTrack();
        this.clipArea=this.trackRow.getClipArea();
        this.notes=new HashMap<>(); //initialize the map
    }
    
    public void addNote(int step, int pitch, int velocity, int length){
        if(blockedNotes.containsKey(pitch)&&blockedNotes.get(pitch).contains(step)){
            System.out.println("no");
        } else {
            Note newNote=new Note(pitch, step, length, track);
            notes.computeIfAbsent(step, k->new ArrayList<>()).add(newNote);
            for(int i=step;i<step+length;i++){
                blockedNotes.computeIfAbsent(pitch, k->new ArrayList<>()).add(i);
            }
            System.out.println("note added.");
            
        }
    }
    
    public void removeNote(int step, Note note){
        List<Note> stepNotes=notes.get(step);
        if(stepNotes!=null){
            stepNotes.remove(note);
            if(stepNotes.isEmpty()){
                notes.remove(step);
            }
            //remove from blocked notes
            int pitch=note.getPitch();
            int length=note.getLength();
            if(blockedNotes.containsKey(pitch)){
                for(int i=step;i<step+length;i++){
                    blockedNotes.get(pitch).remove(Integer.valueOf(i));
                }
                if(blockedNotes.get(pitch).isEmpty()){
                    blockedNotes.remove(pitch);
                }
            }
            
        }
    }
    
    public Map<Integer, List<Note>> getNotes(){
        return notes;
    }
    
    public int getStartStep(){
        return startStep;
    }
    
    public void setStartStep(int startStep){
        this.startStep=startStep;
    }
    
    //check if block has any notes (for visual feedback)
    public boolean isEmpty(){
        return notes.isEmpty();
    }
    
    //get all notes as a flat list for easier processing
    public List<Note> getAllNotes(){
        List<Note> allNotes=new ArrayList<>();
        for(List<Note> stepNotes : notes.values()){
            allNotes.addAll(stepNotes);
        }
        return allNotes;
    }
}