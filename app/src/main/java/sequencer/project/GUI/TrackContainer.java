package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import sequencer.project.audio.MusicRoom;
import sequencer.project.model.InstrumentType;

public class TrackContainer extends ScrollPane {
    private static final int MAX_TRACKS=8;
    private static final double TRACK_HEIGHT=80.0;//idk
    private static final String TRACK_CONTAINER_STYLE= //all this colour stuff do later
        "-fx-background-color: #1a1a1a;" +
        "-fx-border-color: #333333;" +
        "-fx-border-width: 0px;" +
        "-fx-focus-color: transparent;" +
        "-fx-faint-focus-color: transparent;";
    
    private VBox trackVBox;
    private List<TrackRow> tracks;
    private List<ScrollPane> trackScrollPanes = new ArrayList<>();
    private ScrollPane masterScrollPane = null; // reference to the first one
    private GUIController controller;
    private int selectedTrackIndex=-1;

    

    private int zoom;

    // shared scroll position property that all tracks bind to
    private DoubleProperty masterHScroll=new SimpleDoubleProperty(0.0);
    
    public TrackContainer(GUIController controller){
        zoom=1;
        this.controller=controller;
        this.tracks=new ArrayList<>();
        
        
        initializeContainer();
        
        //tests

        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Square", InstrumentType.SYNTH);

        removeTrack(1);

        TrackRow track0=getTrackRow(0);
        track0.getClipArea().createBlock();

        
        try {
            Thread.sleep(1000);
            TrackRow testTrack=tracks.get(0);
            testTrack.getClipArea().createBlock();testTrack.getClipArea().createBlock();testTrack.getClipArea().createBlock();testTrack.getClipArea().createBlock();testTrack.getClipArea().createBlock();
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    } 

    private void initializeContainer(){
        // create the main vbox that will hold all tracks
        trackVBox=new VBox();
        trackVBox.setSpacing(0); // minimal spacing between tracks
        trackVBox.setPadding(Insets.EMPTY);
        trackVBox.setStyle("-fx-background-color: #ffffffff;");
        
        // configure scrollpane
        setContent(trackVBox);
        setPadding(Insets.EMPTY);
        setStyle(TRACK_CONTAINER_STYLE);
        setFitToWidth(true);
        setFitToHeight(false);
        
        // scroll eiyhrt way in theory
        setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // smooth scrolling
        setPannable(true);
    }

    public void addTrack(String instrumentName, InstrumentType instrumentType){ //inputs will come from dropdown menus ahh
        TrackRow newTrack=new TrackRow(tracks.size(),instrumentName,instrumentType,this);
        newTrack.getClipArea().createBlock();
        tracks.add(newTrack);
        trackVBox.getChildren().add(newTrack);

        ScrollPane newScrollPane = newTrack.getClipScrollPane();
        //dont show bars
        newScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        newScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        newScrollPane.hvalueProperty().bindBidirectional(masterHScroll);//bind scroll positions

        trackScrollPanes.add(newScrollPane);    

    }

    public void removeTrack(int trackIndex){
        if(trackIndex<0 || trackIndex>=tracks.size()){
            return; //invalid index
        }
        
        //get the track to remove
        TrackRow trackToRemove=tracks.get(trackIndex);
        
        //unbind scroll property to prevent memory leaks
        ScrollPane scrollPaneToRemove=trackScrollPanes.get(trackIndex);
        scrollPaneToRemove.hvalueProperty().unbindBidirectional(masterHScroll);
        
        //remove from collections
        tracks.remove(trackIndex);
        trackScrollPanes.remove(trackIndex);
        
        //remove from gui
        trackVBox.getChildren().remove(trackToRemove);
        
        //update track indices for remaining tracks
        for(int i=trackIndex;i<tracks.size();i++){
            tracks.get(i).updateTrackIndex(i);
        }
    }

    public void updateAllTrackWidths(){
    // find the maximum number of blocks across all tracks
        int maxBlocks=0;
        for(TrackRow track : tracks){
            int blockCount=track.getClipArea().getBlocks().size();
            maxBlocks=Math.max(maxBlocks, blockCount);
        }
        
        // ensure minimum width (at least 4 blocks worth)
        maxBlocks=Math.max(maxBlocks, 4);
        
        // update all tracks to have the same width
        for(TrackRow track : tracks){
            track.getClipArea().setUniformWidth(maxBlocks);
        }
    }
    
    private void synchronizeScrollPositions(){ //REDUNDANT..
        if(tracks.size() <= 1) return; // nothing to sync
        
        // Get the scroll panes from all tracks
        
        for(TrackRow track : tracks){
            ScrollPane clipScrollPane = track.getClipScrollPane(); // you'll need to add this getter
            trackScrollPanes.add(clipScrollPane);
        }
        
        
        // Bind all scroll positions to the first one
        ScrollPane masterScrollPane = trackScrollPanes.get(0);
        for(int i = 1; i < trackScrollPanes.size(); i++){
            ScrollPane slaveScrollPane = trackScrollPanes.get(i);
            
            // Bind horizontal scroll positions
            slaveScrollPane.hvalueProperty().bind(masterScrollPane.hvalueProperty());
        }
    }

    public int getZoom(){return zoom;}//bruh idk what am i gonna do with zoom
    public TrackRow getTrackRow(int trackIndex){return tracks.get(trackIndex);}
   
}
