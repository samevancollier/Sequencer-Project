package sequencer.project.GUI;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Track;
import sequencer.project.audio.MusicRoom;
import sequencer.project.model.Block;
import sequencer.project.model.InstrumentType;
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

    public void tests(){
        //tests

        trackContainer.addTrack("Teenage Drums", InstrumentType.DRUMS);
        trackContainer.addTrack("Teenage Drums", InstrumentType.DRUMS);
        trackContainer.addTrack("Teenage Drums", InstrumentType.DRUMS);
        trackContainer.addTrack("Square", InstrumentType.SYNTH);

        trackContainer.removeTrack(2);

        TrackRow track0=trackContainer.getTrackRow(0);
        track0.getClipArea().createBlock();track0.getClipArea().createBlock();track0.getClipArea().createBlock();
        Block myBlock=track0.getClipArea().getSpecificBlock(0);
        myBlock.addNote(7, 60, 127, 40);

        Block myBlock2=track0.getClipArea().getSpecificBlock(1);Block myBlock3=track0.getClipArea().getSpecificBlock(2);Block myBlock4=track0.getClipArea().getSpecificBlock(3);
        myBlock2.addNote(15,80,127,40); myBlock3.addNote(15,40,127,40);myBlock4.addNote(15,20,127,40); //pitch is INVERSE...
        BlockNode myBlockNode=track0.getClipArea().getSpecificBlockNode(0);
        BlockNode myBlockNode2=track0.getClipArea().getSpecificBlockNode(1);BlockNode myBlockNode3=track0.getClipArea().getSpecificBlockNode(2);BlockNode myBlockNode4=track0.getClipArea().getSpecificBlockNode(3);
        myBlockNode.refresh(); myBlockNode2.refresh();myBlockNode3.refresh();myBlockNode4.refresh();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }
        trackContainer.addTrack("Square", InstrumentType.SYNTH);
        trackContainer.addTrack("Square", InstrumentType.SYNTH);
        trackContainer.addTrack("Square", InstrumentType.SYNTH);
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

        VBox.setVgrow(trackContainer, Priority.ALWAYS);
        
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
    public BorderPane getRoot(){return root;}

    public TrackContainer getContainer(){return trackContainer;}
}
