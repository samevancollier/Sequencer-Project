package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import sequencer.project.model.InstrumentType;

public class TrackPanel extends ScrollPane {
    private VBox trackContainer;
    private List<TrackControl> trackControls;
    
    public TrackPanel(){
        // Create container for tracks
        trackContainer=new VBox();
        trackContainer.setSpacing(2);
        trackContainer.setPadding(new Insets(5));
        
        // Initialize track controls list
        trackControls=new ArrayList<>();
        
        // Createsample tracks for testing
        addSampleTracks();
        
        // Set up scroll pane
        this.setContent(trackContainer);
        this.setFitToWidth(true);
        this.setFitToHeight(false);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setMinWidth(220);
        this.setMaxWidth(220);
    }
    
    private void addSampleTracks(){ //test
        addTrack("Kick", InstrumentType.DRUMS);
        addTrack("Kick", InstrumentType.DRUMS);
        addTrack("Kick", InstrumentType.DRUMS);
        addTrack("Kick", InstrumentType.DRUMS);
        addTrack("Kick", InstrumentType.DRUMS);
    }
    
    public void addTrack(String trackName, InstrumentType instrumentType){
        TrackControl trackControl=new TrackControl(trackName, instrumentType);
        trackControls.add(trackControl);
        trackContainer.getChildren().add(trackControl);
    }
    
    public void removeTrack(int index){
        if(index>=0 && index<trackControls.size()){
            TrackControl trackToRemove=trackControls.get(index);
            trackControls.remove(index);
            trackContainer.getChildren().remove(trackToRemove);
        }
    }
    
    public TrackControl getTrackControl(int index){
        if(index>=0 && index<trackControls.size()){
            return trackControls.get(index);
        }
        return null;
    }
    
    public int getTrackCount(){
        return trackControls.size();
    }
}
