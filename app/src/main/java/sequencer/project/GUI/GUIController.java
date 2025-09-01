package sequencer.project.GUI;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Track;
import sequencer.project.audio.AudioPlayer;
import sequencer.project.audio.MusicRoom;
import sequencer.project.model.Block;

import sequencer.project.model.InstrumentType;



import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;

import sequencer.project.model.Project;
//TO DO
//create an EmptyTrackRow class
//get rid of various unused things

public class GUIController {
    
    private TopControlBar topControlBar;
    private TrackContainer trackContainer;    
    private Background background;    
    
    //private PianoRoll pianoRoll

    //back end things here

    private AudioPlayer audioPlayer;
    private Project project;

    // selected track state
    private int selectedTrackIndex=-1; //no track selected

    public GUIController(AudioPlayer audioPlayer, Project project){
        this.audioPlayer=audioPlayer;
        this.project=project;
        initializeComponents();
        setupLayout();
    }

    public void tests(){
        //tests
        /* 
        trackContainer.addTrack("Teenage Drums", InstrumentType.DRUMS);
        trackContainer.addTrack("Teenage Drums", InstrumentType.DRUMS);
        trackContainer.addTrack("Teenage Drums", InstrumentType.DRUMS);
        trackContainer.addTrack("Square", InstrumentType.SYNTH);
        trackContainer.addTrack("Square", InstrumentType.SYNTH);
        
        //trackContainer.removeTrack(2);

        TrackRow track0=trackContainer.getTrackRow(0);
        track0.getClipArea().createBlock();track0.getClipArea().createBlock();track0.getClipArea().createBlock();
        Block myBlock=track0.getClipArea().getSpecificBlock(0);
        myBlock.addNote(7, 60, 127, 40);

        Block myBlock2=track0.getClipArea().getSpecificBlock(1);Block myBlock3=track0.getClipArea().getSpecificBlock(2);Block myBlock4=track0.getClipArea().getSpecificBlock(3);
        myBlock2.addNote(15,80,127,40); myBlock3.addNote(15,40,127,40);myBlock4.addNote(15,20,127,40); //pitch is INVERSE...
        BlockNode myBlockNode=track0.getClipArea().getSpecificBlockNode(0);
        BlockNode myBlockNode2=track0.getClipArea().getSpecificBlockNode(1);BlockNode myBlockNode3=track0.getClipArea().getSpecificBlockNode(2);BlockNode myBlockNode4=track0.getClipArea().getSpecificBlockNode(3);
        myBlockNode.refresh(); myBlockNode2.refresh();myBlockNode3.refresh();myBlockNode4.refresh();

        
       
        ThemeManager.getInstance().setTheme("terraria");
        */
    }

    private void initializeComponents(){
        Background background=new Background(800, 600);
        this.background=background;
        //initalize theme manager

        // create main layout
        
        StackPane root=new StackPane();
        root.setPadding(Insets.EMPTY);
        
        // create top control bar
        topControlBar=new TopControlBar(this, project);
        //pianoroll
        PianoRoll pianoRoll=new PianoRoll();
        
        // create track container
        trackContainer=new TrackContainer(this, project, pianoRoll);
        audioPlayer.linkPlaybackCursor(trackContainer.getPlaybackCursor());     //HERE
        bindScrollPositions(trackContainer);

        VBox top=new VBox();
        top.getChildren().add(topControlBar);
        top.getChildren().add(trackContainer);

        VBox.setVgrow(trackContainer, Priority.ALWAYS);
        
        // placeholder for piano roll area
        

        Pane testPane = new Pane();
        testPane.setPrefHeight(200);

        pianoRoll.setPrefHeight(200);
        
        SplitPane mainSplitPane = new SplitPane();
        mainSplitPane.setOrientation(Orientation.VERTICAL);
        mainSplitPane.getItems().addAll(top, pianoRoll);
        mainSplitPane.setDividerPositions(0.8);
        
        mainSplitPane.setStyle(
        "-fx-background-color: transparent; " +
        "-fx-background: transparent; " +
        "-fx-control-inner-background: transparent; " +
        "-fx-accent: transparent;"
        );
        top.setStyle("-fx-background-color: transparent; -fx-background: transparent");
        
        SplitPane.setResizableWithParent(pianoRoll, true);
        SplitPane.setResizableWithParent(top, true);
        
        root.setStyle("-fx-background-color: transparent;");
        //root.setCenter(mainSplitPane);
        root.getChildren().addAll(background,mainSplitPane);

        //init ttack SHOULD BE AN EMPTY TRACK doesnt really matter 

        trackContainer.addTrack("Square", InstrumentType.SYNTH);
        TrackRow track0=trackContainer.getTrackRow(0);
        track0.getClipArea().createBlock();
        BlockNode firstBlock=track0.getClipArea().getSpecificBlockNode(0);
        pianoRoll.setOpenBlock(firstBlock);

    }

    private void bindScrollPositions(TrackContainer container){
        DoubleProperty masterHScroll=trackContainer.getMasterHScroll();
        // bind parallax position to master scroll with optional scaling
        masterHScroll.addListener((obs,oldVal,newVal)->{
            
            //System.out.println("TrackContainer scroll changed, background exists: " + (background != null));
            double scrollValue=newVal.doubleValue();
            //System.out.println("Scroll changed: "+oldVal+" -> "+newVal);
            
            // scale the scroll for parallax effect (adjust multiplier as needed)
            double parallaxScale=0.8; // background scrolls slightly slower than tracks
            double parallaxPosition=scrollValue*parallaxScale;
        
            //System.out.println("Setting parallax position to: "+parallaxPosition);
            background.setScrollPosition(scrollValue*parallaxScale);
        });
    }

    private void setupLayout(){
        // add components to main layout
        //root.setTop(topControlBar.getNode());
        //root.setCenter(trackContainer.getNode());
        
        // piano roll will go in bottom later
        
        // set overall styling
        
    
    }

    public void loadStylesheets(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/themes/sonic.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load CSS file: " + e.getMessage());
        }
    }

    public void selectTrack(int selectedTrack){
        selectedTrackIndex=selectedTrack;
        //expand later
    }

    // getters
    public StackPane getRoot(){return (StackPane)background.getParent();}

    public TrackContainer getContainer(){return trackContainer;}

    public Background getBackground(){return background;}

    public AudioPlayer getAudioPlayer(){return audioPlayer;}
}
