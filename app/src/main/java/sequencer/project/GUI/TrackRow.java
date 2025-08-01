package sequencer.project.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import sequencer.project.model.InstrumentType;
import sequencer.project.model.Track;
import sequencer.project.GUI.*;

public class TrackRow extends HBox {
    private static final double HEIGHT=100; //height for everything, basically

    private int trackIndex;
    private InstrumentType trackType;
    private String trackName;
    private boolean isSelected;
    private Color trackColour;
    private TrackContainer parentContainer;
    
    private TrackControl trackControl;
    private ClipArea clipArea=new ClipArea(this); //fuuuuuck this
    private ScrollPane clipAreaScrollPane=new ScrollPane(clipArea);
    private FXArea fxArea;

    //STUFF ASSOCIATED WITH BACKEND

    private Track track;
    
    //private double currentZoomLevel=1.0; HANDLED GLOBALLY..by trackContainer

    private static final Color[] TRACK_COLOURS={
        Color.web("#ff6b6b"), // red
        Color.web("#4ecdc4"), // teal  
        Color.web("#45b7d1"), // blue
        Color.web("#96ceb4"), // green
        Color.web("#feca57"), // yellow
        Color.web("#ff9ff3"), // pink
        Color.web("#54a0ff"), // light blue
        Color.web("#5f27cd")  // purple
    };
    //filller stylessss
    private static final String SELECTED_STYLE=
        "-fx-background-color: #2a2a2a;" +
        "-fx-border-color: #4a9eff;" +
        "-fx-border-width: 2px;";
        
    private static final String UNSELECTED_STYLE=
        "-fx-background-color: transparent;" +
        "-fx-border-color: #333333;" +
        "-fx-border-width: 1px;";

    public TrackRow(int trackIndex, String trackName, InstrumentType trackType, TrackContainer parentContainer){
        this.trackIndex=trackIndex;
        this.trackName =trackName;
        this.trackType=trackType;
        this.parentContainer=parentContainer;
        this.trackColour=TRACK_COLOURS[trackIndex];
        this.isSelected=false;
        this.track=new Track(trackName, trackIndex);
        
        initializeLayout();
        //setupMouseHandlers(); //do later
    } 

    private void initializeLayout(){
        setStyle("-fx-background-color: transparent;");
        //CREATE THREE MAIN SECTIONS
        setPrefHeight(HEIGHT);setMinHeight(HEIGHT);setMaxHeight(HEIGHT);
        trackControl=new TrackControl(trackName, trackType, this);
        trackControl.setPrefWidth(HEIGHT);trackControl.setPrefHeight(HEIGHT);trackControl.setMinWidth(HEIGHT);trackControl.setMaxWidth(HEIGHT);
        //CLIP AREAfyyyuccjk
        
        // wrap it in a scroll
        
        clipAreaScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        clipAreaScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        clipAreaScrollPane.setFitToHeight(true);
        clipAreaScrollPane.setFitToWidth(false); // important! let it scroll horizontally
        clipAreaScrollPane.setPannable(false);
        clipAreaScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(clipAreaScrollPane, Priority.ALWAYS); // expand to fill space
        
        //FX AREA
        fxArea=new FXArea();
        fxArea.setPrefWidth(HEIGHT);fxArea.setPrefHeight(HEIGHT);fxArea.setMinWidth(HEIGHT);fxArea.setMinHeight(HEIGHT);

        //add all sectiond
        getChildren().addAll(trackControl, clipAreaScrollPane, fxArea);//add fx area later

        setStyle(UNSELECTED_STYLE);
        setSpacing(0);
        setPadding(new Insets(0));
    }
    public void updateTrackIndex(int newIndex){
        //update any internal track index references
        this.trackIndex=newIndex;
    }

    public void setSelected(boolean selected) {
            this.isSelected = selected;
            if (selected) {
                setStyle(SELECTED_STYLE);
            } else {
                setStyle(UNSELECTED_STYLE);
            }
        }
        public boolean isSelected() {
            return isSelected;
    }

    public ScrollPane getClipScrollPane(){return clipAreaScrollPane;}
    public double getTrackHeight(){return HEIGHT;}
    public Color getColour(){return trackColour;}
    public TrackContainer getContainer(){return parentContainer;}
    public ClipArea getClipArea(){return clipArea;}
    public Track getTrack(){return track;}
    public int getIndex(){return trackIndex;}
}
