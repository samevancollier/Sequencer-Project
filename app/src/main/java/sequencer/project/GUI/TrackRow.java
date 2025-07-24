package sequencer.project.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import sequencer.project.model.InstrumentType;
import sequencer.project.GUI.*;

public class TrackRow extends HBox {
    private static final double HEIGHT=100; //height for everything, basically

    private int trackIndex;
    private InstrumentType trackType;
    private String trackName;
    private boolean isSelected;
    private Color trackColor;
    private TrackContainer parentContainer;
    
    private TrackControl trackControl;
    private ClipArea clipArea=new ClipArea(this); //fuuuuuck this
    private ScrollPane clipAreaScrollPane=new ScrollPane(clipArea);
    //private FXArea fxArea;
    
    //private double currentZoomLevel=1.0; HANDLED GLOBALLY..by trackContainer

    private static final Color[] TRACK_COLORS={
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
        "-fx-background-color: #ac9090ff;" +
        "-fx-border-color: #333333;" +
        "-fx-border-width: 1px;";

    public TrackRow(int trackIndex, String trackName, InstrumentType trackType, TrackContainer parentContainer){
        this.trackIndex=trackIndex;
        this.trackName =trackName;
        this.trackType=trackType;
        this.parentContainer=parentContainer;
        this.trackColor=TRACK_COLORS[trackIndex % TRACK_COLORS.length];
        this.isSelected=false;
        
        initializeLayout();
        //setupMouseHandlers(); //do later
    } 

    private void initializeLayout(){
        //CREATE THREE MAIN SECTIONS
        setPrefHeight(HEIGHT);setMinHeight(HEIGHT);setMaxHeight(HEIGHT);
        trackControl=new TrackControl(trackName, trackType);
        trackControl.setPrefWidth(HEIGHT);trackControl.setPrefHeight(HEIGHT);trackControl.setMinWidth(HEIGHT);trackControl.setMaxWidth(HEIGHT);
        //CLIP AREAfyyyuccjk
        
        // wrap it in a scroll
        
        clipAreaScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        clipAreaScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        clipAreaScrollPane.setFitToHeight(true);
        clipAreaScrollPane.setFitToWidth(false); // important! let it scroll horizontally
        clipAreaScrollPane.setPannable(false);
        HBox.setHgrow(clipAreaScrollPane, Priority.ALWAYS); // expand to fill space
        //FX AREA
        //fxArea=new FXArea();
        //fxArea.setPrefWidth(HEIGHT);fxArea.setPrefHeight(HEIGHT);fxArea.setMinWidth(HEIGHT);fxArea.setMinheight(HEIGHT);

        //add all sectiond
        getChildren().addAll(trackControl, clipAreaScrollPane);//add fx area later

        setStyle(UNSELECTED_STYLE);
        setSpacing(0);
        setPadding(new Insets(0));
    }

    public ScrollPane getClipScrollPane(){return clipAreaScrollPane;}
    public double getTrackHeight(){return HEIGHT;}
    public Color getColor(){return trackColor;}
}
