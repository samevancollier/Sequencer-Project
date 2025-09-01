package sequencer.project.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private int colourPallete;
    private InstrumentType trackType;
    private String trackName;
    private boolean isSelected;
    private Color trackColour;
    private TrackContainer parentContainer;
    
    private TrackControl trackControl;
    private ClipArea clipArea; //fuuuuuck this
    private ScrollPane clipAreaScrollPane;
    private FXArea fxArea;

    //STUFF ASSOCIATED WITH BACKEND

    private Track track;

    private String unselectedStyle;
    private String selectedStyle;
    
    //private double currentZoomLevel=1.0; HANDLED GLOBALLY..by trackContainer

   
    public TrackRow(int trackIndex, String trackName, InstrumentType trackType, TrackContainer parentContainer){
        this.parentContainer=parentContainer;
        this.trackIndex=trackIndex;
        this.trackName =trackName;
        this.trackType=trackType;
        ThemeManager tm=ThemeManager.getInstance();
        this.colourPallete = trackIndex % tm.getTrackColourCount();

        
        
        
        
        this.trackColour=tm.getTrackColourBase(colourPallete);
        this.isSelected=false;
        this.track=new Track(trackName, trackIndex);//HERE
        this.clipArea=new ClipArea(this, track); 
        this.clipAreaScrollPane=new ScrollPane(clipArea);
        initializeLayout();
        //setupMouseHandlers(); //do later
    } 

    public void style(){
        ThemeManager tm=ThemeManager.getInstance();
        unselectedStyle="-fx-background-color: transparent; -fx-border-color: "+tm.getLineColour().toString().replace("0x", "#")+"; -fx-border-width: 1px";//SHOULD NOT BE HARDCODED IN THIS WAY!
        selectedStyle="-fx-background-color: transparent; -fx-border-color: "+tm.getLineColour().toString().replace("0x", "#")+"; -fx-border-width: 7px";
        this.setStyle(unselectedStyle);//THIS WILL CAUSE A BUG IF CHANGING THEME WHILE HAVING TRACKS SELECTED
    }
    

    private void initializeLayout(){
        setAlignment(Pos.CENTER_LEFT);
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
        //clipArea.drawLines();
        HBox.setHgrow(clipAreaScrollPane, Priority.ALWAYS); // expand to fill space
        
        //FX AREA
        fxArea=new FXArea(this);
        fxArea.setPrefWidth(HEIGHT);fxArea.setPrefHeight(HEIGHT);fxArea.setMinWidth(HEIGHT);fxArea.setMinHeight(HEIGHT);

        //add all sectiond
        getChildren().addAll(trackControl, clipAreaScrollPane, fxArea);//add fx area later

        style();
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
                setStyle(selectedStyle);
            } else {
                setStyle(unselectedStyle);
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
    public int getPallete(){return colourPallete;}
}
