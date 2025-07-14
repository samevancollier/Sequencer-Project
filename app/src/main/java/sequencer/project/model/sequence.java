package sequencer.project.model;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Sequence;

import sequencer.project.audio.musicroom;

public class sequence {
    private List<track> tracks;
    private int bPM;
    private int timeSignature; //probably i will just have 4/4 available, but could do 3/4 etc idk maybe later
    private String name; //i guess only ask for when saving?
    private musicroom musicRoom;
    
    public sequence(musicroom mR){ //i really dont like passing this in all over the place, oh well
        this.tracks = new ArrayList<>();
        this.bPM = 10;
        this.timeSignature = 44;
        this.musicRoom = mR;
    }

    public void addTrack(String chosenInstrument){
        tracks.add(new track(chosenInstrument, musicRoom));
        System.out.println("Track added!");
    }

    public track getTrack(int desiredTrackNumber){
        return tracks.get(desiredTrackNumber);
    }
    //PLAY!
    public void play(){
        for(int i=0;i<20;i++){ //stupid
            for(track track : tracks){
                List<note> notesToBePlayed = track.getNotes(i);
                for(note noteToPlay : notesToBePlayed){
                    track.getInstrument().playNote(noteToPlay);
                }
            }
            try {
                Thread.sleep((60000/bPM)/16); // You'll need this method
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    


}
