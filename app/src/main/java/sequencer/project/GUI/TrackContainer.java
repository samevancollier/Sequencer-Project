package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.List;



import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import sequencer.project.model.InstrumentType;
import sequencer.project.model.Project;

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

    private double barWidthInPixels=200;//should be set by zoom
    private int extraSpace=4000;

    private ThemeManager themeManager;
    private double imageWidth = 0;
    //playbackCursor STUFF HERE

    
    private ScrollPane playbackCursorLayer;
    private Pane cursorPane;
    private PlaybackCursor playbackCursor;

    private int zoom;

    private Project project;
    private PianoRoll pianoRoll;

    // shared scroll position property that all tracks bind to
    private DoubleProperty masterHScroll=new SimpleDoubleProperty(0.0);
    
    public TrackContainer(GUIController controller, Project project, PianoRoll pianoRoll ){
        super();
        zoom=1;
        this.controller=controller;
        this.project=project;
        this.pianoRoll=pianoRoll;
        this.tracks=new ArrayList<>();
        
        
        initialiseContainer();
        
        this.setFocusTraversable(true);

        this.setOnKeyPressed(event->{
            if(event.getCode()==KeyCode.DELETE){
                deleteSelectedBlocks();
                deleteSelectedTracks();
                deselectAll();
                event.consume();
            }
        });
        /* 
        Platform.runLater(() -> {
            System.out.println("=== TrackContainer Layout Debug ===");
            System.out.println("TrackContainer width: " + getWidth());
            System.out.println("cliparea width:" + tracks.get(1).getClipArea().getWidth());
            System.out.println("cursorlayer width:" + playbackCursorLayer.getWidth());
            System.out.println("TrackContainer height: " + getHeight());
            System.out.println("Content width: " + getContent().getBoundsInLocal().getWidth());
            System.out.println("Viewport width: " + getViewportBounds().getWidth());
            System.out.println("Can scroll horizontally: " + (getContent().getBoundsInLocal().getWidth() > getViewportBounds().getWidth()));
        });
        */
    } 
    private void initialiseplaybackCursor(){
        playbackCursorLayer=new ScrollPane();
        cursorPane=new Pane();
        playbackCursorLayer.setMouseTransparent(true);
        playbackCursorLayer.setFitToWidth(false);
        playbackCursorLayer.setFitToHeight(false);
        
      
        cursorPane.setPrefWidth(4800);
        //cursorPane.setMinWidth(4800);
        //cursorPane.setMaxWidth(4800);
        playbackCursor=new PlaybackCursor(controller.getAudioPlayer(), this);
        cursorPane.getChildren().add(playbackCursor);
        playbackCursorLayer.setContent(cursorPane);
        playbackCursorLayer.setStyle("-fx-background-color: transparent;;-fx-padding: 0 100 0 100;");
        playbackCursorLayer.setPannable(false);
       
        /* 
        //update playbackCursor height when container is resized
        heightProperty().addListener((obs,oldVal,newVal)->{
            playbackCursor.updateHeight();
        });
        */
    }

    private void initialiseContainer(){
        
        setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        // create the main vbox that will hold all tracks
        trackVBox=new VBox();
        trackVBox.setSpacing(0); // minimal spacing between tracks
        trackVBox.setPadding(Insets.EMPTY);
        trackVBox.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        initialiseplaybackCursor();

        StackPane mainStackPane=new StackPane();
        playbackCursorLayer.hvalueProperty().bindBidirectional(masterHScroll);//bind scroll position of playbackcursor laya
        mainStackPane.getChildren().addAll(trackVBox,playbackCursorLayer);
        
        // configure scrollpane
        
        setPadding(Insets.EMPTY);
        
        setFitToWidth(true);
        setFitToHeight(false);
        
        // scroll eiyhrt way in theory
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        playbackCursorLayer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playbackCursorLayer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // smooth scrolling
        setPannable(false);
        setContent(mainStackPane);
        //setSeamlessBackground("/backgrounds/kitty.jpg"); //filler
                                                                    
        
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
        double totalWidth=(barWidthInPixels*maxBlocks)+extraSpace;
        // update cursor layer widths
        playbackCursorLayer.setPrefWidth(getWidth() - 200); // subtract both 100px margins
        playbackCursorLayer.setLayoutX(100); // offset to start after left margin
        
        cursorPane.setPrefWidth(totalWidth); 
        cursorPane.setMinWidth(totalWidth);
        //cursorPane.setMaxWidth(totalWidth);

        Platform.runLater(() -> {
            cursorPane.requestLayout();
            playbackCursorLayer.requestLayout();
            System.out.println("totalwidth:" + totalWidth);
            System.out.println("cursorpanewidth:" + cursorPane.getWidth());
            System.out.println("cursorscrollpanewidth:" + playbackCursorLayer.getWidth());
        });

       

    }

    private void deleteSelectedTracks(){
        if(selectedTracks.isEmpty()){
            return;
        }
        
        // create copy
        List<TrackRow> tracksToDelete=new ArrayList<>(selectedTracks);
    
        selectedTracks.clear();
     
        for(TrackRow trackRow:tracksToDelete){
            removeTrack(trackRow.getIndex()); 
        }
    }
    private void deleteSelectedBlocks(){
        if(selectedBlocks.isEmpty()){
            return;
        }
        
        // create copy
        List<BlockNode> blocksToDelete=new ArrayList<>(selectedBlocks);
    
        selectedBlocks.clear();
     
        for(BlockNode emptying:blocksToDelete){
            emptying.setSelected(false);
            emptying.empty();
        }

        
    }

    public void addTrack(String instrumentName, InstrumentType instrumentType){ //inputs will come from dropdown menus ahh
        TrackRow newTrack=new TrackRow(tracks.size(),instrumentName,instrumentType,this);
        newTrack.getClipArea().createBlock();
        tracks.add(newTrack);
        trackVBox.getChildren().add(newTrack);
        project.addTrack(newTrack.getTrack());

        ScrollPane newScrollPane = newTrack.getClipScrollPane();
        //dont show bars
        newScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        newScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        newScrollPane.hvalueProperty().bindBidirectional(masterHScroll);//bind scroll positions
        updateAllTrackWidths();
        
        //newTrack.getClipArea().drawLines();  //why doesnt it doo that 
        trackScrollPanes.add(newScrollPane);  
        playbackCursor.updateHeight();
        

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
        //remove from project
        project.removeTrack(trackIndex);
        //update tha cursor
        playbackCursor.updateHeight();
        //update track indices for remaining tracks
        for(int i=trackIndex;i<tracks.size();i++){
            tracks.get(i).updateTrackIndex(i);
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
    
    public void loadCurrentThemeImageWidth() {
        if (themeManager != null) {
            try {
                String imagePath = themeManager.getCurrentBackgroundImagePath();
                if (imagePath != null) {
                    Image img = new Image(getClass().getResourceAsStream(imagePath));
                    imageWidth = img.getWidth();
                    System.out.println("Loaded image width: " + imageWidth);
                }
            } catch (Exception e) {
                System.err.println("Could not load image" + e.getMessage());
                imageWidth = 1920; // Fallback
            }
        }
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
    public DoubleProperty getMasterHScroll(){return masterHScroll;}
    public GUIController getController(){return controller;}
    public VBox getVBox(){return trackVBox;}
    public int getNumOfTracks(){return tracks.size();}
    public PlaybackCursor getPlaybackCursor(){return playbackCursor;}
    public double getBarWidthInPixels(){return barWidthInPixels;}
    public PianoRoll getPianoRoll(){return pianoRoll;}
}
