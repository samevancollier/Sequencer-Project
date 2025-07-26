package sequencer.project.GUI;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sequencer.project.model.Track;
import javafx.scene.paint.Color;
import sequencer.project.model.Note;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class ClipArea extends Canvas {
    private TrackRow trackRow;
    private Track track;
    // these will be set by TrackContainer, not hardcoded
    private double barWidthInPixels=200.0; //probably also dictated by zoom actully means BLOCKWIDTHINPIXELS
    private int visibleBars=4;//divytatedc by zoom dk what is yjr [point of this]
    private int interval=128; //dictated by zoom 32 IS ABOUT RIGHT
    private double gridLineSpacing=25.0; //bruh
   
    private static final double CLIP_HEIGHT=100;
    private static final double MIN_NOTE_HEIGHT=2.0;
    private static final double MAX_NOTE_HEIGHT=15.0;
    private static final int STEPS_PER_BLOCK=256; //4 bars * 64 steps per bar
    //MOUSE STUFF
    private static final double RESIZE_EDGE_WIDTH=10.0;
    private Block draggedBlock=null;
    private boolean isDraggingLeftEdge=false;
    private boolean isDraggingRightEdge=false;
    private double dragStartX=0;
    
    private List<Block> blocks=new ArrayList<Block>();
    
    public ClipArea(TrackRow trackRow){
        this.trackRow=trackRow;
        this.track=trackRow.getTrack();
        initializeCanvas();
        setupMouseHandlers();
        redraw();
    }
    
    private void initializeCanvas(){
        setWidth(barWidthInPixels * Math.max(1,blocks.size()));//somehow fine
        setHeight(CLIP_HEIGHT);
    }

    private void setupMouseHandlers(){
        setOnMouseMoved(this::handleMouseMove);
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
    }
    
    public void redraw(){
        GraphicsContext gc=getGraphicsContext2D(); //should this be here
        gc.setFill(Color.web("#ffffffff"));
        gc.fillRect(0, 0, getWidth(), getHeight());
        drawBlocks(gc);
        drawGrid(gc);
        
    }
    
    private void drawGrid(GraphicsContext gc){ //BROKEN AF
        //draw grid lines for each block
        for(int blockIndex=0;blockIndex<Math.max(1,blocks.size());blockIndex++){
            double blockStartX=blockIndex*barWidthInPixels;
            
            //vertical lines within each block
            for(int i=0;i<=barWidthInPixels;i++){
                double x=blockStartX+i;
                if(i%32==0){
                    gc.setStroke(Color.web("#ffffffff"));
                    gc.strokeLine(x, 0, x, CLIP_HEIGHT);
                }
                if(i%interval==0){
                    gc.setStroke(Color.web("#5e5e5eff"));
                    gc.strokeLine(x, 0, x, CLIP_HEIGHT);
                }
            }
            
            //block boundary lines
            if(blockIndex>0){
                gc.setStroke(Color.web("#ff0000ff")); //red for block boundaries
                gc.setLineWidth(2);
                gc.strokeLine(blockStartX, 0, blockStartX, CLIP_HEIGHT);
                gc.setLineWidth(1);
            }
        }
    }
    
    private void drawBlocks(GraphicsContext gc){
        for(int i=0;i<blocks.size();i++){
            Block block=blocks.get(i);
            double blockStartX=i*barWidthInPixels;
            
            //draw block background
            gc.setFill(Color.web("#863e3eff"));
            gc.fillRect(blockStartX, 0, barWidthInPixels, CLIP_HEIGHT);

            gc.setFill(Color.web("#c25a5aff"));
            gc.fillRect(blockStartX,0,barWidthInPixels,CLIP_HEIGHT/5);
            //draw block boundaries

            gc.setStroke(Color.web("#ff0000ff"));
            //draw notes in block (simplified for now)
            gc.setFill(Color.web("#4a9eff"));
            for(var stepEntry : block.getNotes().entrySet()){
                int step=stepEntry.getKey();
                double noteX=blockStartX+(step*barWidthInPixels/STEPS_PER_BLOCK);
                //draw simple note representation
                gc.fillRect(noteX, 20, 4, 60);
            }
        }
    }
    
    public void createBlock(){
        int startStep=blocks.size()*STEPS_PER_BLOCK;
        Block newBlock=new Block(startStep, trackRow);
        blocks.add(newBlock);
        
        //update canvas width
        trackRow.getContainer().updateAllTrackWidths();
        setWidth(barWidthInPixels*blocks.size()+500);
        redraw();
    }
    
    public void removeBlock(int blockIndex){//problematic....actually fried 
        if(blockIndex>=0&&blockIndex<blocks.size()){
            blocks.remove(blockIndex);
            trackRow.getContainer().updateAllTrackWidths();
            redraw();
        }
    }

    

    public void setUniformWidth(int totalBlocks){
        double newWidth=((barWidthInPixels*totalBlocks)+500);
        System.out.println("Setting width to: " + newWidth + " (totalBlocks: " + totalBlocks + ")");
        setWidth(newWidth);
        System.out.println("New width is: " + getWidth());
        
        redraw();
    }
    
    private void handleMouseMove(MouseEvent e){
        double mouseX=e.getX();
        
        //check if mouse is near block edges
        for(int i=0;i<blocks.size();i++){
            double blockStartX=i*barWidthInPixels;
            double blockEndX=blockStartX+barWidthInPixels;
            
            //left edge resize zone
            if(mouseX>=blockStartX&&mouseX<=blockStartX+RESIZE_EDGE_WIDTH){
                setCursor(Cursor.W_RESIZE);
                return;
            }
            //right edge resize zone  
            if(mouseX>=blockEndX-RESIZE_EDGE_WIDTH&&mouseX<=blockEndX){
                return;
            }
        }
        setCursor(Cursor.DEFAULT);
    }
    private void handleMousePressed(MouseEvent e){
        double mouseX=e.getX();
        dragStartX=mouseX;
        
        //determine which block and which edge
        for(int i=0;i<blocks.size();i++){
            double blockStartX=i*barWidthInPixels;
            double blockEndX=blockStartX+barWidthInPixels;
            
            //left edge
            if(mouseX>=blockStartX&&mouseX<=blockStartX+RESIZE_EDGE_WIDTH){
                draggedBlock=blocks.get(i);
                isDraggingLeftEdge=true;//left edge probably not neccesary
                return;
            }
            //right edge
            if(mouseX>=blockEndX-RESIZE_EDGE_WIDTH&&mouseX<=blockEndX){
                draggedBlock=blocks.get(i);
                isDraggingRightEdge=true;
                return;
            }
        }
    }

    private void handleMouseDragged(MouseEvent e){
        if(draggedBlock==null) return;
        
        double mouseX=e.getX();
        double deltaX=mouseX-dragStartX;
        
        //snap to block boundaries
        int blockDelta=(int)Math.round(deltaX/barWidthInPixels);
        
        if(isDraggingRightEdge&&blockDelta>0){
            //expanding right - try to create new blocks
            tryExpandRight(draggedBlock, blockDelta);
        }
        if(isDraggingLeftEdge&&blockDelta<0){
            //expanding left - try to create new blocks
            //expanind left should maybe just replace the left block with an empty block? idk
        }
    }
    private void handleMouseReleased(MouseEvent e){
        draggedBlock=null;
        isDraggingLeftEdge=false;
        isDraggingRightEdge=false;
        setCursor(Cursor.DEFAULT);
    }

    private void tryExpandRight(Block block, int blockCount){
        int blockIndex=blocks.indexOf(block);
        
        //check if space is available
        for(int i=1;i<=blockCount;i++){
            if(blockIndex+i<blocks.size()){
                System.out.println("space occupied, cannot expand right");
                return;
            }
        }
        
        //create new blocks
        for(int i=1;i<=blockCount;i++){
            createBlock();
        }
        System.out.println("expanded right by "+blockCount+" blocks");
    }

    //getters
    public List<Block> getBlocks(){return blocks;}
    
    
    
}