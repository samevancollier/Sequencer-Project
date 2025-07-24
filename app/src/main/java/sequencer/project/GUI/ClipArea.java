package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sequencer.project.model.Note;

public class ClipArea extends Canvas {
    private TrackRow track;
    // these will be set by TrackContainer, not hardcoded
    private double barWidthInPixels=200.0; //probably also dictated by zoom

    private int visibleBars=4;//divytatedc by zoom dk what is yjr [point of this]

    private int interval=128; //dictated by zoom 32 IS ABOUT RIGHT

    private double gridLineSpacing=25.0; //bruh

    
   

    private static final double CLIP_HEIGHT=100;
    private static final double MIN_NOTE_HEIGHT=2.0;
    private static final double MAX_NOTE_HEIGHT=15.0;

    private List<FourBar> fourBars = new ArrayList<FourBar>(); //LATER

    public ClipArea(TrackRow track){
        this.track=track;
        fourBars.add(new FourBar());fourBars.add(new FourBar());fourBars.add(new FourBar());fourBars.add(new FourBar());fourBars.add(new FourBar());fourBars.add(new FourBar());fourBars.add(new FourBar());fourBars.add(new FourBar());
        initializeCanvas();
        
        redraw();
    }

    private void initializeCanvas(){
        setWidth(barWidthInPixels * fourBars.size());
        setHeight(CLIP_HEIGHT);
        
   
    }

    public void redraw(){
        GraphicsContext gc=getGraphicsContext2D(); //should this be here
        gc.setFill(Color.web("#ffffffff"));
        gc.fillRect(0, 0, getWidth(), getHeight());
        drawLines(gc);
    }

    private void drawLines(GraphicsContext gc){

        for(int i=0;i<this.getWidth();i++){ //dubious 
            if(i%32==0){
                gc.setStroke(Color.web("#8a8a8aff"));
                gc.strokeLine(i, 0, i, CLIP_HEIGHT); //fuuuucl
            }
            if(i%interval==0){
                gc.setStroke(Color.web("#000000ff"));
                gc.strokeLine(i, 0, i, CLIP_HEIGHT); //fuuuucl
            }
        }
    }

   

    
    //innner class fo rbars maybe it should be an actual classs thoughhhhhh
    public static class FourBar {
        private List<Note> notes = new ArrayList<>(); //bruh
        
        public void addNote(Note note){
            notes.add(note);
        }
        
        public List<Note> getNotes(){
            return notes;
        }
    }
}
