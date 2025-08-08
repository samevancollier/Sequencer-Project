package sequencer.project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sequencer.project.audio.MusicRoom;

public class Sequence {
    private List<Track> tracks;
    private int bPM;
    private int timeSignature; //probably i will just have 4/4 available, but could do 3/4 etc idk maybe later
    private String name; //i guess only ask for when saving?
    private MusicRoom musicRoom;
    private int currentStep;
    private int length;
    private Map<Track, Integer> volumes = new HashMap<>();//likely not neeeded
    
    public Sequence(){ //i really dont like passing this in all over the place, oh well
        this.tracks = new ArrayList<>();
        this.bPM = 200;
        this.timeSignature = 44;
        this.musicRoom = MusicRoom.getInstance();
        this.currentStep = 0;
        this.length=4000; //HERE
    }

    public void addTrack(String chosenInstrument){
        Track newTrack = new Track(chosenInstrument, tracks.size()+1);
        tracks.add(newTrack);
        volumes.put(newTrack, newTrack.getVolume());
        System.out.println("Track added!" + tracks.size());
    }

    public void removeTrack(int trackToRemove){
        tracks.remove(trackToRemove);
        volumes.remove(trackToRemove);
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
    public Map<Track, Integer> getVolumes(){return volumes;}
}
