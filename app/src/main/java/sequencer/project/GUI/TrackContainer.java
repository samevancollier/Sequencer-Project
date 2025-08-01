package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
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

    private  List<BlockNode> selectedBlocks=new ArrayList<>();
    private  List<TrackRow> selectedTracks=new ArrayList<>();

    

    private int zoom;

    // shared scroll position property that all tracks bind to
    private DoubleProperty masterHScroll=new SimpleDoubleProperty(0.0);
    
    public TrackContainer(GUIController controller){
        zoom=1;
        this.controller=controller;
        this.tracks=new ArrayList<>();
        
        
        initializeContainer();
        
        

        
    } 

    
    private void initializeContainer(){
        // create the main vbox that will hold all tracks
        trackVBox=new VBox();
        trackVBox.setSpacing(0); // minimal spacing between tracks
        trackVBox.setPadding(Insets.EMPTY);
        trackVBox.setStyle("-fx-background-color: transparent;");

        StackPane backgroundPane = new StackPane();
        backgroundPane.setMinHeight(800);backgroundPane.setMinWidth(800);
        backgroundPane.setStyle("-fx-background-color: #ffffffff;");                        //BACKGROUND HERE
        
        backgroundPane.getChildren().addAll(trackVBox);
        
        // configure scrollpane
        setContent(backgroundPane);
        setPadding(Insets.EMPTY);
        //setStyle(TRACK_CONTAINER_STYLE);
        setFitToWidth(true);
        setFitToHeight(false);
        
        // scroll eiyhrt way in theory
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // smooth scrolling
        setPannable(false);
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
        newTrack.getClipArea().drawLines();  
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

    public void selectBlock(BlockNode selected,boolean isShiftHeld){ //consider making a list of lists Selected and then when keys like delete are pressed iterate through selected blocks, tracks and perform operations
        if(!isShiftHeld){
            deselectAll();
            selectedBlocks.clear();
            selected.setSelected(true);
            selectedBlocks.add(selected);
        } else {
            selectedBlocks.add(selected);
            selected.setSelected(true);  
        }
    }
    public void selectTrack(TrackRow selectedTrack, boolean isShiftHeld){
        if(!isShiftHeld){
            deselectAll();
            selectedTrack.setSelected(true);
            selectedTracks.add(selectedTrack);
            for(TrackRow trackRow : selectedTracks){
                System.out.println("ttrack" + trackRow.getIndex());
            }
        } else {
            selectedTracks.add(selectedTrack);
            selectedTrack.setSelected(true);
            System.out.println("PRINTING");
            for(TrackRow trackRow : selectedTracks){
                System.out.println("ttrack" + trackRow.getIndex());
            }
        }
    }
    public void deselectAll(){
        for(BlockNode block : selectedBlocks) {
            block.setSelected(false); 
        }
        for(TrackRow trackRow : selectedTracks){
            trackRow.setSelected(false);
        }
        selectedTracks.clear();
        selectedBlocks.clear();
    }

    public void moveTracksUp() { //MOVING MULTIPLE TRACKS UP OR DOWN AT THE SAME TIME DOES NOT WORK dont know why
        // sort selected tracks by their current index (highest first)
        List<TrackRow> sortedTracks = new ArrayList<>(selectedTracks);
        sortedTracks.sort((a, b) -> Integer.compare(tracks.indexOf(b), tracks.indexOf(a)));
        
        for(TrackRow trackToMoveUp : sortedTracks) {
            int index = tracks.indexOf(trackToMoveUp);
            if(index > 0) {
                // move in data
                tracks.remove(index);
                tracks.add(index - 1, trackToMoveUp);
                
                // move in UI
                trackVBox.getChildren().remove(index);
                trackVBox.getChildren().add(index - 1, trackToMoveUp);
            }
        }
        updateTrackIndices(); // only call once at the end
    }

    public void moveTracksDown() {
        // sort selected tracks by their current index
        List<TrackRow> sortedTracks = new ArrayList<>(selectedTracks);
        sortedTracks.sort((a, b) -> Integer.compare(tracks.indexOf(a), tracks.indexOf(b)));
        
        for(TrackRow trackToMoveDown : sortedTracks) {
            int index = tracks.indexOf(trackToMoveDown);
            if(index < tracks.size() - 1) {
                tracks.remove(index);
                tracks.add(index + 1, trackToMoveDown);
                
                trackVBox.getChildren().remove(index);
                trackVBox.getChildren().add(index + 1, trackToMoveDown);
            }
        }
        updateTrackIndices();
    }
    
    private void updateTrackIndices() {
        for (int i = 0; i < tracks.size(); i++) {
            tracks.get(i).updateTrackIndex(i);
        }
    }

    public int getZoom(){return zoom;}//bruh idk what am i gonna do with zoom
    public TrackRow getTrackRow(int trackIndex){return tracks.get(trackIndex);}
    public List<BlockNode> getSelectedBlocks(){return selectedBlocks;}
    public List<TrackRow> getSelectedTracks(){return selectedTracks;}
   public boolean isTrackSelected(TrackRow trackRow) {return selectedTracks.contains(trackRow);}
}
