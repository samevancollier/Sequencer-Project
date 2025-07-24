package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import sequencer.project.model.InstrumentType;

public class TrackContainer extends ScrollPane {
    private static final int MAX_TRACKS=8;
    private static final double TRACK_HEIGHT=80.0;
    private static final String TRACK_CONTAINER_STYLE=
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

    // shared scroll position property that all tracks bind to
    private DoubleProperty masterHScroll=new SimpleDoubleProperty(0.0);
    
    public TrackContainer(GUIController controller){
        this.controller=controller;
        this.tracks=new ArrayList<>();
        
        initializeContainer();
        
        //tests

        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
        addTrack("Teenage Drums", InstrumentType.DRUMS);
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
        tracks.add(newTrack);
        trackVBox.getChildren().add(newTrack);

        ScrollPane newScrollPane = newTrack.getClipScrollPane();
        //dont show bars
        newScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        newScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        newScrollPane.hvalueProperty().bindBidirectional(masterHScroll);

        trackScrollPanes.add(newScrollPane);    

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
}
