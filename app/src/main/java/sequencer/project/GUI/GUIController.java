package sequencer.project.GUI;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sequencer.project.audio.MusicRoom;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
//TO DO
//create an EmptyTrackRow class
//get rid of various unused things

public class GUIController {
    private BorderPane root;
    private TopControlBar topControlBar;
    private TrackContainer trackContainer;        
      
    //private PianoRoll pianoRoll

    // selected track state
    private int selectedTrackIndex=-1; //no track selected

    public GUIController(){
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents(){
        // create main layout
        
        root=new BorderPane();
        root.setPadding(Insets.EMPTY);
        
        // create top control bar
        topControlBar=new TopControlBar(this);
        
        // create track container
        trackContainer=new TrackContainer(this);

        VBox top=new VBox();
        top.getChildren().add(topControlBar);
        top.getChildren().add(trackContainer);

        
        
        // placeholder for piano roll area
        VBox pianoRollPlaceholder=new VBox();
        pianoRollPlaceholder.getChildren().add(new Label("piano roll area (coming soon)"));
        pianoRollPlaceholder.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 10;");
        pianoRollPlaceholder.setPrefHeight(200);
        
        SplitPane mainSplitPane = new SplitPane();
        mainSplitPane.setOrientation(Orientation.VERTICAL);
        mainSplitPane.getItems().addAll(top, pianoRollPlaceholder);
        mainSplitPane.setDividerPositions(0.8);
        mainSplitPane.setStyle(
            "-fx-background-color: #1a1a1a;" +
            "-fx-box-border: transparent;"
        );
        SplitPane.setResizableWithParent(pianoRollPlaceholder, true);
        SplitPane.setResizableWithParent(top, true);

        root.setCenter(mainSplitPane);

        //tests
    }

    private void setupLayout(){
        // add components to main layout
        //root.setTop(topControlBar.getNode());
        //root.setCenter(trackContainer.getNode());
        
        // piano roll will go in bottom later
        
        // set overall styling
        root.setStyle("-fx-background-color: #1e1e1e;");
    
    }

    public void selectTrack(int selectedTrack){
        selectedTrackIndex=selectedTrack;
        //expand later
    }

    // getters
    public BorderPane getRoot(){
        return root;
    }
}
