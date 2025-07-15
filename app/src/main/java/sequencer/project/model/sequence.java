package sequencer.project.model;

import java.util.ArrayList;
import java.util.List;



import sequencer.project.audio.MusicRoom;

public class Sequence {
    private List<Track> tracks;
    private int bPM;
    private int timeSignature; //probably i will just have 4/4 available, but could do 3/4 etc idk maybe later
    private String name; //i guess only ask for when saving?
    private MusicRoom musicRoom;
    private int currentStep;
    private int length;
    
    public Sequence(MusicRoom mR){ //i really dont like passing this in all over the place, oh well
        this.tracks = new ArrayList<>();
        this.bPM = 10;
        this.timeSignature = 44;
        this.musicRoom = mR;
        this.currentStep = 0;
        this.length=100;
    }

    public void addTrack(String chosenInstrument){
        tracks.add(new Track(chosenInstrument, musicRoom, tracks.size()));
        System.out.println("Track added!" + tracks.size());
    }

    public void removeTrack(int trackToRemove){
        tracks.remove(trackToRemove-1);
        for(Track track : tracks){
            track.setTrackNumber(track.getTrackNumber()-1);
            System.out.println(track.getTrackNumber());
        }
    }
    //need method for removing tracks - must reassign track numbers

    public Track getTrack(int desiredTrackNumber){
        return tracks.get(desiredTrackNumber);
    }
    public List<Track> getTracks(){
        return tracks;
    }

    public int getBPM(){
        return bPM;
    }
    public int getLength(){
        return length;
    }
    //PLAY! MOVE THIS TO AUDIOPLAYER.JAVA!
    public void play(){
        for(int i=0;i<20;i++){ //stupid
            for(Track track : tracks){
                List<Note> notesToBePlayed = track.getNotes(i);
                for(Note noteToPlay : notesToBePlayed){
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
