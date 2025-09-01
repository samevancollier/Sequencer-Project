package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sequencer.project.model.Block;
import sequencer.project.model.Note;

public class GraphicNote extends Rectangle{

    private Block owningBlock;
    private BlockNode owningBlockNode;
    private int keyHeight;
    private PianoRoll pianoRoll;
    private boolean isDraggingLeft=false;
    private boolean isDraggingRight=false;
    private boolean isDraggingBody=false;
    private double startX,startWidth,startMouseX;
    private static final double DRAG_ZONE=5.0;
    private Rectangle previewRect;
    private List<Rectangle> previewRects=new ArrayList<>();     //weird
    private boolean selected;
    private Note note;
    private Pane noteLayer;
    private Boolean isShiftDown=false;

    GraphicNote(double x,double y,double width,int height,PianoRoll pianoRoll){
        super(x,y,width,height);
        setFocusTraversable(false);
        
        this.pianoRoll=pianoRoll;
        this.owningBlockNode=pianoRoll.getBlockNode();
        this.keyHeight=pianoRoll.getKeyHeight();
        this.noteLayer=pianoRoll.getNoteLayer();
        setupEventHandlers();
    }
    
    private void setupEventHandlers(){
        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        
        
        // change cursor based on position need to fix it 
        setOnMouseMoved(e->{
            double mouseX=e.getX();
            if(mouseX<=DRAG_ZONE){
                setCursor(Cursor.W_RESIZE);
            }else if(mouseX>=getWidth()-DRAG_ZONE){
                setCursor(Cursor.E_RESIZE);
            }else{
                setCursor(Cursor.MOVE);
            }
        });
    }
    
    private void onMousePressed(MouseEvent e){      //add stuff to drag notes without holding shift 
        isShiftDown=e.isShiftDown();
        select();
        double mouseX=e.getX()-getX();
        startX=getX();
        startWidth=getWidth();
        startMouseX=e.getX();
    
        isDraggingLeft=(mouseX<=DRAG_ZONE);
        isDraggingRight=(mouseX>=getWidth()-DRAG_ZONE);
        isDraggingBody=!isDraggingLeft&&!isDraggingRight;
    
        // create preview rectangles for all selected notes
        if(isDraggingLeft||isDraggingRight||isDraggingBody){
            List<GraphicNote> selectedNotes=pianoRoll.getSelectedNotes();
            for(GraphicNote note : selectedNotes){
                Rectangle previewRect=new Rectangle(note.getX(),note.getY(),note.getWidth(),note.getHeight());
                previewRect.setFill(Color.TRANSPARENT);
                previewRect.setStroke(Color.YELLOW);
                previewRect.setStrokeWidth(2);
                previewRect.getStrokeDashArray().addAll(5.0,5.0);
                ((Pane)getParent()).getChildren().add(previewRect);
                previewRects.add(previewRect);
            }
        }
    }

    private void onMouseDragged(MouseEvent e){
        double deltaX=e.getX()-startMouseX;
        double subdivisionWidth=pianoRoll.getSubdivisionWidth();
        List<GraphicNote> selectedNotes=pianoRoll.getSelectedNotes();
        
    
        if(isDraggingLeft){
            // LEFT EDGE DRAG: resize all selected notes
            for(int i=0;i<selectedNotes.size();i++){
                GraphicNote note=selectedNotes.get(i);
                Rectangle preview=previewRects.get(i);
                double newX=note.getX()+deltaX;
                double newWidth=note.getWidth()-deltaX;
            
                if(newWidth>=subdivisionWidth){
                    preview.setX(newX);
                    preview.setWidth(newWidth);
                }
            }
        }else if(isDraggingRight){
            // RIGHT EDGE DRAG: resize all selected notes
            for(int i=0;i<selectedNotes.size();i++){
                GraphicNote note=selectedNotes.get(i);
                Rectangle preview=previewRects.get(i);
                double newWidth=note.getWidth()+deltaX;
            
                if(newWidth>=subdivisionWidth){
                    preview.setWidth(newWidth);
                }
            }
        }else if(isDraggingBody){
            // BODY DRAG: move all selected notes
            double newY=e.getY();
            double keyHeight=pianoRoll.getKeyHeight();
            double quantizedY=newY-(newY%keyHeight);
            double deltaY=quantizedY-getY();
        
            for(int i=0;i<selectedNotes.size();i++){
                GraphicNote note=selectedNotes.get(i);
                Rectangle preview=previewRects.get(i);
                double newX=note.getX()+deltaX;
                double newNoteY=note.getY()+deltaY;
            
                preview.setX(newX);
                preview.setY(newNoteY);
            }
        }
        e.consume();
    }

    public void select(){
        selected=true;
        System.out.println("Before focus request - current focus: " + pianoRoll.getScene().getFocusOwner());
        noteLayer.requestFocus();
        System.out.println("After focus request - current focus: " + pianoRoll.getScene().getFocusOwner());
        System.out.println("noteLayer.isFocused(): " + noteLayer.isFocused());
        setFill(Color.rgb(0, 108, 255, 0.5));                                                               //HARDCODED COLOUR HERE
        if(isShiftDown || pianoRoll.getIsDragging()){
            pianoRoll.getSelectedNotes().add(this);
           // System.out.println("NEW NOTE SELECTED: " + note.getStep()+":"+note.getPitch());
            System.out.println("Added to selection (shift). List size: " + pianoRoll.getSelectedNotes().size());

        }else{
            for(GraphicNote g : new ArrayList<>(pianoRoll.getSelectedNotes())){
                System.out.println("deselecting...,");
                g.deselect();
            }
            pianoRoll.getSelectedNotes().clear();
            pianoRoll.getSelectedNotes().add(this);
            System.out.println("Added to selection solo. List size: " + pianoRoll.getSelectedNotes().size());
        }
    }
    public void deselect(){
        selected=false;
        setFill(Color.rgb(255, 0, 0, 1));
    }
    private void onMouseReleased(MouseEvent e){
        e.consume();
        List<GraphicNote> selectedNotes = new ArrayList<>(pianoRoll.getSelectedNotes());
        if(isDraggingLeft||isDraggingRight||isDraggingBody){
           // List<GraphicNote> selectedNotes=pianoRoll.getSelectedNotes();
            double subdivisionWidth=pianoRoll.getSubdivisionWidth();
        
            // collect target positions for all notes
            List<Double> targetXs=new ArrayList<>();
            List<Double> targetYs=new ArrayList<>();
            List<Double> targetWidths=new ArrayList<>();
        
            for(int i=0;i<selectedNotes.size();i++){
                Rectangle preview=previewRects.get(i);
                double targetX=preview.getX();
                double targetY=preview.getY();
                double targetWidth=preview.getWidth();
            
                // quantize positions
                double quantizedX=Math.round(targetX/subdivisionWidth)*subdivisionWidth;
                double quantizedY=Math.round(targetY/keyHeight)*keyHeight;
                double quantizedWidth=Math.round(targetWidth/subdivisionWidth)*subdivisionWidth;
            
                if(quantizedWidth<subdivisionWidth){
                    quantizedWidth=subdivisionWidth;
                }
            
                targetXs.add(quantizedX);
                targetYs.add(quantizedY);
                targetWidths.add(quantizedWidth);
            }
        
            // remove preview rectangles
            for(Rectangle preview : previewRects){
                ((Pane)getParent()).getChildren().remove(preview);
            }
            previewRects.clear();
        
            // update all notes and handle collisions/bounds
            for(int i=selectedNotes.size()-1;i>=0;i--){
                GraphicNote note=selectedNotes.get(i);
                double quantizedX=targetXs.get(i);
                double quantizedY=targetYs.get(i);
                double quantizedWidth=targetWidths.get(i);
            
                // check bounds
                int step=(int)(quantizedX/subdivisionWidth) * (int)Math.pow(2, (5-pianoRoll.getResolution()));
                int pitch=119-((int)quantizedY/keyHeight);
                int length=(int)(quantizedWidth/subdivisionWidth) * (int)Math.pow(2, (5-pianoRoll.getResolution()));
            
                // delete if out of bounds
                if(step<0||step>255||pitch<0||pitch>119){
                    pianoRoll.deleteNote(note); //maybe here
                    continue;
                }
            
                // delete overlapping notes (not selected)
                for(int j=pianoRoll.getAllNotes().size()-1;j>=0;j--){
                    GraphicNote g=pianoRoll.getAllNotes().get(j);
                    if(!selectedNotes.contains(g) && g.getY()==quantizedY){
                        if(g.getX()<quantizedX+quantizedWidth && g.getX()+g.getWidth()>quantizedX){
                            pianoRoll.deleteNote(g);
                        }
                    }
                }
            
                // update note position and model
                note.setX(quantizedX);
                note.setY(quantizedY);
                note.setWidth(quantizedWidth);
                note.setFill(Color.rgb(0, 108, 255, 0.5));          //HARDCODED COLOUR HEHRE
            
                note.getNote().setPosition(step);
                note.getNote().setPitch(pitch);
                System.out.println("NEW LENGTH:" + length);
                note.getNote().setLength(length);
            }
        
            Platform.runLater(() -> {
                noteLayer.requestFocus();
            });
        }
        isDraggingLeft=false;
        isDraggingRight=false;
        isDraggingBody=false;
    }
    public void setNote(Note note){this.note=note;}

    public void move(KeyEvent e){           //ADD BOUNDS CHECKS
        KeyCode k=e.getCode();
        double subdivisionWidth=pianoRoll.getSubdivisionWidth();
        if(k==KeyCode.DOWN && note.getPitch()>0){
            
            double y=getY();
            System.out.println("y= "+y);
            setY(y+keyHeight);
            System.out.println("y= "+getY());
            note.setPitch(note.getPitch()-1);
            System.out.println("NEWpitch: " + note.getPitch());

        } else if (k==KeyCode.UP && note.getPitch()<119){
            double y=getY();
            setY(y-keyHeight);
            note.setPitch(note.getPitch()+1);
            System.out.println("NEWpitch: " + note.getPitch());
        } else if (k==KeyCode.LEFT && note.getStep()>0){
            double x=getX();
            setX(x-subdivisionWidth);
            note.setPosition(note.getStep()-pianoRoll.subdivisionToStep());                 //WRONG, NEEDs TO BE BASED ON RESOLUTION!
            System.out.println("NEWSTEP: " + note.getStep());
        } else if (k==KeyCode.RIGHT && note.getStep()<255){
            double x=getX();
            setX(x+subdivisionWidth);
            note.setPosition(note.getStep()+pianoRoll.subdivisionToStep());
            System.out.println("NEWSTEP: " + note.getStep());
        }
        
    }

    public Note getNote(){return note;}
}