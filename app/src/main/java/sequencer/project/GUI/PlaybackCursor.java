package sequencer.project.GUI;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import sequencer.project.audio.AudioPlayer;

public class PlaybackCursor extends Line {
    private int pixelsPerBar=50;
    private int pixelsPerBlock=pixelsPerBar*4;
    private int stepsPerBar=64;

    private double containerHeight;

    private AudioPlayer audioPlayer;
    private TrackContainer trackContainer;
    private VBox trackArea;
    
    public PlaybackCursor(AudioPlayer audioPlayer, TrackContainer trackContainer){
        super();
        
        this.audioPlayer=audioPlayer;
        this.trackContainer=trackContainer;
        this.trackArea=trackContainer.getVBox();
        //set visual properties
        setStroke(Color.RED);
        setStrokeWidth(2.0);
        
        //set initial position
        
        updateHeight();
        updatePosition(0);
    }

    public void updatePosition(int step){
    
        double xPosition=(step*(50.0/64.0))+100; //calculATE steps to pixels
        setStartX(xPosition);
        setStartY(0);
        setEndX(xPosition);
        setEndY(containerHeight);
    }

    public void newPosition(double xPosition){
        setStartX(xPosition);
        setStartY(0);
        setEndX(xPosition);
        setEndY(containerHeight);
    }
    
    public void updateHeight(){
        this.containerHeight=trackContainer.getNumOfTracks()*100; //make sure the trackcontainer height is actually resized by adding and emoving tracks, the +100 is not great
        System.out.println("container height is" + containerHeight);
        setEndY(containerHeight);
    }

    public double getPosition(){return getStartX();}
}
