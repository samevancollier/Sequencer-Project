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

    private int visibleBars=4;//divytatedc by zoom

    private int interval=16; //dictated by zoom

    private double gridLineSpacing=25.0;

    
   

    private static final double CLIP_HEIGHT=100;
    private static final double MIN_NOTE_HEIGHT=2.0;
    private static final double MAX_NOTE_HEIGHT=15.0;

    private List<Bar> bars = new ArrayList<Bar>(); //LATER

    public ClipArea(TrackRow track){
        this.track=track;
        bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());bars.add(new Bar());
        initializeCanvas();
        
        redraw();
    }

    private void initializeCanvas(){
        setWidth(barWidthInPixels * bars.size());
        setHeight(CLIP_HEIGHT);
        
   
    }

    public void redraw(){
        GraphicsContext gc=getGraphicsContext2D(); //should this be here
        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, getWidth(), getHeight());
        drawLines(gc);
    }

    private void drawLines(GraphicsContext gc){

        for(int i=0;i<visibleBars*interval;i++){ //dubious math
            if(i%interval==0){
                gc.strokeLine(i, 0, i, CLIP_HEIGHT); //fuuuucl
            }
        }
    }

   

    
    //innner class fo rbars 
    public static class Bar {
        private List<Note> notes = new ArrayList<>();
        
        public void addNote(Note note){
            notes.add(note);
        }
        
        public List<Note> getNotes(){
            return notes;
        }
    }
}
