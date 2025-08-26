package sequencer.project.GUI;

import java.util.List;

import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sequencer.project.model.Block;
import sequencer.project.model.Note;

public class BlockNode extends Pane {
    private int blockIndex;
    private double width;
    private Block block;
    private Rectangle rect;
    private Rectangle header;
    private ClipArea clipArea;
    private double dragStartX;
    private double dragStartY;
    private Color colour;
    private boolean isDragging=false;
    private Boolean isDraggingRight=false;
    private boolean isSelected=false;//idk
    private boolean isEmpty=true;
    private TrackContainer trackContainer;
    private Canvas noteCanvas;
    private double DRAG_ZONE=10; //area where dragging happens
    private double BLOCK_HEIGHT=100;
    private double BLOCK_WIDTH=200;
    private Color BLOCK_COLOUR;                     //make all these into some kind of enum for associated colours
    private Color HEADER_COLOUR=Color.AQUAMARINE;
    private Color BLOCK_SELECTED_COLOUR=Color.DARKBLUE;
    private Color BLOCK_BORDER_COLOUR=Color.RED;
    private Color NOTE_COLOUR=Color.BLACK;

    public BlockNode(ClipArea clipArea, int blockIndex, Block block){
        this.clipArea=clipArea;
        this.blockIndex=blockIndex;
        this.block=block;
        this.BLOCK_COLOUR=clipArea.getTrackRow().getColour();
        this.trackContainer=clipArea.getTrackRow().getContainer();
        width=clipArea.getBarWidthInPixels();

        initializeVisuals();
        setupMouseHandlers();
        updatePosition();
    }

    

    private void initializeVisuals(){
        rect=new Rectangle();
        rect.setWidth(BLOCK_WIDTH);
        rect.setHeight(BLOCK_HEIGHT);
        rect.setFill(BLOCK_COLOUR);
        rect.setStroke(BLOCK_BORDER_COLOUR);
        rect.setStrokeWidth(1);

        header=new Rectangle();
        header.setWidth(BLOCK_WIDTH);
        header.setHeight(BLOCK_HEIGHT/5);
        header.setFill(HEADER_COLOUR);

        noteCanvas=new Canvas(BLOCK_WIDTH,BLOCK_HEIGHT);


        
        getChildren().addAll(rect, header, noteCanvas);
        
        // make the pane match the rectangle size
        setPrefHeight(BLOCK_HEIGHT);
        setPrefWidth(BLOCK_WIDTH);
    }

    public void empty(){
        List<Note> notesToDelete=block.getAllNotes();
        for(Note deleting : notesToDelete){
            block.removeNote(deleting.getStep(), deleting); //delete from the block in model
        }
        drawNotes();
        isEmpty=true; //for appearance
    }
    private void drawNotes(){
        int range=block.getRange();
        int noteThicknessInPixels=5; //replace with algorithim for calculating thickness based on range later
        List<Note> notesToDraw=block.getAllNotes();

        
        GraphicsContext gc=noteCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);

        gc.setFill(NOTE_COLOUR);
        for(Note noteBeingDrawn : notesToDraw){
            int startX=noteBeingDrawn.getStep();
            int startY=noteBeingDrawn.getPitch()+(noteThicknessInPixels/2);
            gc.fillRect(startX,startY,noteBeingDrawn.getLength(),noteThicknessInPixels );
            //gc.strokeLine(noteBeingDrawn.getStep(), noteBeingDrawn.getPitch(), noteBeingDrawn.getStep()+noteBeingDrawn.getLength(), noteBeingDrawn.getPitch());
        }

    }

    private void setupMouseHandlers(){
        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        setOnMouseClicked(this::onMouseClicked);
        setOnMouseMoved(this::onMouseMoved);
        
        // visual feedback on hover
        setOnMouseEntered(e->rect.setOpacity(0.8));
        setOnMouseExited(e->rect.setOpacity(1.0));
    }
    
    private void onMousePressed(MouseEvent e){
        dragStartX=e.getSceneX();
        dragStartY=e.getSceneY();
        isDragging=false;

        //extending rightwardcheck

        double mouseX=e.getX();
        isDraggingRight=(mouseX>=BLOCK_WIDTH-DRAG_ZONE);
        
        
        if(!isDraggingRight){
            toFront();
            boolean isShiftHeld=e.isShiftDown();
            trackContainer.selectBlock(this,isShiftHeld);
        }
        
        
        
        
        
        e.consume();
    }

    private void onMouseMoved(MouseEvent e){
        double mouseX=e.getX();
        
        // check if near right edge
        if(mouseX>=BLOCK_WIDTH-DRAG_ZONE){
            setCursor(Cursor.E_RESIZE); // horizontal resize cursor should only display this for the last block
        }else{
            setCursor(Cursor.DEFAULT);
        }
    }
    
    private void onMouseDragged(MouseEvent e){
        if(!isDragging){
            isDragging=true;
            rect.setOpacity(0.7);
        }
        
        double deltaX = e.getSceneX() - dragStartX;

        if(isDraggingRight){ //this logic probably shouldnt be handled here, but in trackcontainer, but oh well, it is ok
            if(deltaX>0){
                double extendedPosition=(blockIndex*BLOCK_WIDTH)+deltaX;
                int totalBlocks=clipArea.getBlockNodes().size();
                double emptySpaceStart=totalBlocks*BLOCK_WIDTH;
                
                if(extendedPosition>emptySpaceStart){
                    int numOfBlocksToCreate=(int)((extendedPosition-emptySpaceStart)/BLOCK_WIDTH)+1;
                    clipArea.showNewBlockPreview(numOfBlocksToCreate);
                } else {
                    clipArea.hideNewBlockPreview(); //huh
                }
            } else if(deltaX<-BLOCK_WIDTH){ //dragging left
                int numOfBlocksToHide=Math.abs((int)(deltaX/BLOCK_WIDTH));
                clipArea.showRemovalPreview(numOfBlocksToHide);
            } else {
                clipArea.hideRemovalPreview();
                clipArea.hideNewBlockPreview();
            }
        } else {
            double newX = (blockIndex * width) + deltaX;
            newX = Math.max(0, Math.min(newX, (clipArea.getBlocks().size() - 1) * width));
            
            setLayoutX(newX);
        }
        
        //  Just move visually, calculate target on release
        
        e.consume();
    }
    
    private void onMouseReleased(MouseEvent e){
        if(isDragging){
            double deltaX=e.getSceneX()-dragStartX;
            
            if(isDraggingRight){
                // extending - create new blocks if dragged into empty space
                if(deltaX>0){
                    double extendedPosition=(blockIndex*BLOCK_WIDTH)+deltaX;
                    int totalBlocks=clipArea.getBlockNodes().size();
                    double emptySpaceStart=totalBlocks*BLOCK_WIDTH;
                    
                    // same calculation as preview
                    if(extendedPosition>emptySpaceStart){
                        int numOfBlocksToCreate=(int)((extendedPosition-emptySpaceStart)/BLOCK_WIDTH)+1;
                        System.out.println("creating "+numOfBlocksToCreate+" blocks");
                        for(int i=0;i<numOfBlocksToCreate;i++){
                            clipArea.createBlock();
                            System.out.println("new block created!");
                        }
                    }
                } else if(deltaX<-200) {

                    //double position=((blockIndex-1)*BLOCK_WIDTH)-deltaX;
                    int numOfBlocksToRemove=Math.abs((int)(deltaX/BLOCK_WIDTH));

              
                    System.out.println("deltaX: "+deltaX);
                    System.out.println("blockIndex: "+blockIndex);
                    //System.out.println("position: "+position);
                    System.out.println("numOfBlocksToRemove: "+numOfBlocksToRemove);
                    System.out.println("current block count: "+clipArea.getBlocks().size());

                    for(int i=0;i<numOfBlocksToRemove;i++){
                        int lastIndex=clipArea.getBlocks().size()-1;
                        System.out.println("removing block at index: "+lastIndex);
                        clipArea.removeBlock(lastIndex);
                    }
                }
                clipArea.hideNewBlockPreview();
                clipArea.hideRemovalPreview();
            } 
            
            else {
                // normal drag
                double currentX=getLayoutX();
                int targetBlockIndex=(int)Math.round(currentX/BLOCK_WIDTH);
                targetBlockIndex=Math.max(0,Math.min(targetBlockIndex,clipArea.getBlockNodes().size()-1));
                
                setLayoutX(targetBlockIndex*BLOCK_WIDTH);
                clipArea.moveBlockToPosition(this,blockIndex,targetBlockIndex);
            }
            
            rect.setOpacity(1.0);
            isDragging=false;
            isDraggingRight=false;
        }
        e.consume();
    }
    
    private void onMouseClicked(MouseEvent e){
        if(e.getClickCount()==2){
            // double clickWILLopen note editor
            clipArea.editBlock(this);
        }
        e.consume();
    }
    
    private int snapToStep(double x){
        return Math.max(0,(int)Math.round(x/width)); //snap to nearest block index
    }
    
    public void updatePosition(){
        setLayoutX(blockIndex*width);
        setLayoutY(0); // blocks align to top of cliparea
    }
    
    
    
    public void setSelected(boolean selected){
        if(selected){
            rect.setFill(BLOCK_SELECTED_COLOUR);
            rect.setStrokeWidth(2);
        }else{
            rect.setFill(BLOCK_COLOUR);
            rect.setStrokeWidth(1);
        }
    }
    public void addNote(int step, int pitch, int velocity, int length){ //stupid, dont use anymore
        block.addNote(step, pitch, velocity, length);
        drawNotes(); // UI handles its own refresh
    }

    public void addNote(Note newNote){ 
        block.addNote(newNote);
        drawNotes(); // UI handles its own refresh
    }


    public void removeNote(Note noteToRemove){
        block.removeNote(noteToRemove.getStep(),noteToRemove); //weird
        drawNotes(); // UI handles its own refresh
    }
    public void refresh(){
        updatePosition();
        drawNotes();
    }
    public void setIndex(int newIndex){
        blockIndex=newIndex;
        block.setStartStep((newIndex)*256); //uh
    }
    public int getIndex(){return blockIndex;}
    public Block getBlock(){return block;}
    
    
}
