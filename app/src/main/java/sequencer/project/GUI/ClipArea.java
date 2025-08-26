package sequencer.project.GUI;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import sequencer.project.model.Block;
import sequencer.project.model.Track;
import java.util.ArrayList;
import java.util.List;

public class ClipArea extends Pane {
    private TrackRow trackRow;
    private Track track;
    private List<BlockNode> blockNodes;//BLOCKNODES
    private List<Block> blocks=new ArrayList<Block>(); //BLOCKS
    private BlockNode selectedBlock;
    private TrackContainer trackContainer;
    private Background background;
    private GUIController controller;

    private Rectangle newBlockPreview;
    private List<Rectangle> previewRectangles=new ArrayList<>(); 

    private Color colour;
    
    // match current settings exactly
    private double barWidthInPixels=200.0;
    private double totalWidth;
    private int visibleBars=4;
    private int interval=128;
    private double gridLineSpacing=25.0;
    private static final double CLIP_HEIGHT=100; 
    private static final int STEPS_PER_BLOCK=256;
    
    // visual elements
    private List<Line> lines;
    

    private int extraSpace=4000;
    
    
    public ClipArea(TrackRow trackRow){
        this.trackRow=trackRow;
        this.track=trackRow.getTrack();
        this.blockNodes=new ArrayList<>();
        this.lines=new ArrayList<>();
        this.trackContainer=trackRow.getContainer();
        this.colour=trackRow.getColour();
        this.background=trackRow.getContainer().getController().getBackground();
        this.controller=trackContainer.getController();
        
        initializeCanvas(); 
        setupMouseHandlers();
        refreshBlocks();

        
        
    }
    
    private void initializeCanvas(){
        
        setPrefWidth(barWidthInPixels*blocks.size()+extraSpace);
        
        setHeight(CLIP_HEIGHT);
        
        setPrefHeight(CLIP_HEIGHT);
        setMinHeight(CLIP_HEIGHT);
        setMaxHeight(CLIP_HEIGHT);
        
        
        setStyle("-fx-background-color: transparent;");
        drawLines();
    }
    
    private void setupMouseHandlers(){
        setOnMouseClicked(this::onAreaClicked);
    }
    
    private void onAreaClicked(MouseEvent e){
        // clicking on empty area deselects blocks and later, other things
        if(e.getTarget()==this){
            trackRow.getContainer().deselectAll();
        }
        if(!controller.getAudioPlayer().isPlaying() || controller.getAudioPlayer().isPaused()){
            // convert click X position to step
            double scrollOffset = trackContainer.getHvalue() * (trackContainer.getContent().getBoundsInLocal().getWidth() - trackContainer.getViewportBounds().getWidth());
            double clickX=e.getX()+trackContainer.getMasterHScroll().get(); // FAILED TO FIX THE INCREASING INNACCURACY!
            int step=(int)(clickX/(50.0/64.0)); // reverse your cursor position calculation
            controller.getAudioPlayer().play();controller.getAudioPlayer().pause(); //clumsy way to do that
            // set playback position
            controller.getAudioPlayer().setPlaybackPoint(step); // or whatever method sets position
            
            
            // update cursor visual
            trackContainer.getPlaybackCursor().updatePosition(step);
            
            
        }
        e.consume();
    }

    public void setUniformWidth(int totalBlocks){//FIX THE WEIRD SNAPPING DUE TO WIDTH BEING SHRUNKEN WHEN BLOCKS ARE REMOVED
        
        double newWidth=((barWidthInPixels*totalBlocks)+extraSpace);
        //System.out.println("Setting width to: "+newWidth+" (totalBlocks: "+totalBlocks+")");
        this.totalWidth=newWidth;
        setWidth(newWidth);
        setPrefWidth(newWidth);
        setMinWidth(newWidth);
        setMaxWidth(newWidth);
        //System.out.println("New width is: "+getWidth());
        
        refreshBlocks();
        drawLines();
    }
    
    public void drawLines(){
        getChildren().removeAll(lines);
        lines.clear();
        System.out.println("total width:" + totalWidth);
        for(int i=50;i<totalWidth;i+=50){                                  
            if(!(i%200==0)){
                Line newLine=new Line(i,0,i,CLIP_HEIGHT);
                newLine.setStroke(Color.web("#b2b2b2ff"));
                newLine.setStrokeWidth(1.0);
                lines.add(newLine);
                getChildren().add(newLine);
                continue;
            }
            Line newBlockLine=new Line(i,0,i,CLIP_HEIGHT);
            newBlockLine.setStroke(Color.web("#868686ff"));
            newBlockLine.setStrokeWidth(1.0);
            lines.add(newBlockLine);
            getChildren().add(newBlockLine);
        }
    }
    
    public void refreshBlocks(){          //forgot what is used for
        // remove all existing block nodes
        for(BlockNode node:blockNodes){
            node.refresh();
        }
    }

    public void moveBlockToPosition(BlockNode draggedNode, int fromIndex, int toIndex) {
        if(fromIndex == toIndex) return;
        
        System.out.println("Moving block from " + fromIndex + " to " + toIndex);
        
        // move in both lists 
        Block draggedBlock = blocks.get(fromIndex);
        BlockNode draggedBlockNode = blockNodes.get(fromIndex);

        Block swappedBlock=blocks.get(toIndex); BlockNode swappedBlockNode=blockNodes.get(toIndex);
        
        blocks.set(toIndex, draggedBlock); 
        blockNodes.set(toIndex, draggedBlockNode);

        blocks.set(fromIndex,swappedBlock);
        blockNodes.set(fromIndex,swappedBlockNode);
        
        draggedBlockNode.setIndex(toIndex);
        swappedBlockNode.setIndex(fromIndex);

        draggedBlock.setStartStep(toIndex*256); //i think 256 is correct, but it could be 255
        swappedBlock.setStartStep(fromIndex*256); //i think 256 is correct, but it could be 255

        draggedBlockNode.updatePosition();swappedBlockNode.updatePosition();
        // updatblock indices and positions
        
        
        trackRow.getContainer().updateAllTrackWidths();
    }

    
    
    public void blockMoved(BlockNode blockNode,int newStep){    //not currently used
        // this is called when a block finishes being dragged
        // the block model has already been updated by the blocknode
        
        // could add collision detection here
        // could add undo/redo support here
        // could trigger audio feedback here
        
        System.out.println("block moved to step: "+newStep);
    }
    
    public void editBlock(BlockNode blockNode){
        // called on double-click
        // could open a note editor dialog here
        System.out.println("editing block: "+blockNode.getBlock());
    }
    
    public void createBlock(){ //important
        
        int startStep=blocks.size()*STEPS_PER_BLOCK;
        Block newBlock=new Block(startStep,trackRow);
        blocks.add(newBlock);
        BlockNode newBlockNode=new BlockNode(this, blockNodes.size(), newBlock);
        blockNodes.add(newBlockNode);
        
        getChildren().add(newBlockNode);

        trackRow.getContainer().updateAllTrackWidths();
        setWidth(barWidthInPixels*blocks.size()+extraSpace);
        refreshBlocks();
        drawLines();
    }
    
    public void removeBlock(int blockIndex){
        
        if(blockIndex>=0&&blockIndex<blocks.size()){
            BlockNode nodeToRemove=blockNodes.get(blockIndex);
            getChildren().remove(nodeToRemove);
            blocks.remove(blockIndex);
            blockNodes.remove(blockIndex); //here

            
            trackRow.getContainer().updateAllTrackWidths();
            refreshBlocks();
            drawLines();
        }
    }
    
    
    
    public void deleteSelectedBlock(){ //actually just empties it! no worries
        if(selectedBlock!=null){
            // remove from model
            //track.removeBlock(selectedBlock.getBlock()); LATER
            
            // remove from ui
            getChildren().remove(selectedBlock);
            blockNodes.remove(selectedBlock);
            
            selectedBlock=null;
            //should do more than this liike update the model
        }
    }

    public void showNewBlockPreview(int blockCount){
        hideNewBlockPreview(); // clear existing previews
        
        System.out.println("showing preview for "+blockCount+" blocks"); 
        
        // create preview rectangles for each new block
        for(int i=0;i<blockCount;i++){
            Rectangle preview=new Rectangle();
            preview.setWidth(200); // BLOCK_WIDTH
            preview.setHeight(100); // BLOCK_HEIGHT  
            preview.setFill(Color.LIGHTGRAY);
            preview.setStroke(Color.GRAY);
            preview.setStrokeWidth(2);
            preview.setOpacity(0.5);
            
            // position at the end of existing blocks + i
            int position=getBlockNodes().size()+i;
            preview.setLayoutX(position*200);
            preview.setLayoutY(0);
            
            getChildren().add(preview);
            previewRectangles.add(preview); // store all previews
        }
    }

    public void showRemovalPreview(int numBlocksToRemove){
        hideRemovalPreview(); // clear any existing preview
        
        int totalBlocks=blockNodes.size();
        int startRemovalIndex=totalBlocks-numBlocksToRemove;
        
        System.out.println("showing removal preview for "+numBlocksToRemove+" blocks starting at index "+startRemovalIndex);
        
        // make the blocks that will be removed semi-transparent
        for(int i=startRemovalIndex;i<totalBlocks&&i>=0;i++){
            if(i<blockNodes.size()){
                BlockNode node=blockNodes.get(i);
                node.setOpacity(0.3); // eventually SHOULD BE COMPLETELY INVISIBLE MAYBE

            }
        }
    }

    public void hideRemovalPreview(){
        // restore opacity for all blocks
        for(BlockNode node:blockNodes){
            node.setOpacity(1.0);
            node.getStyleClass().remove("removal-preview");
        }
        System.out.println("hiding removal preview");
    }

    public void hideNewBlockPreview(){
        // removeprevie ws
        for(Rectangle preview:previewRectangles){
            if(getChildren().contains(preview)){
                getChildren().remove(preview);
            }
        }
        previewRectangles.clear();
        System.out.println("hiding preview"); 
    }

    // getters 
    public List<Block> getBlocks(){return blocks;}
    
    public double getBarWidthInPixels(){return barWidthInPixels;}
    
    public int getStepsPerBlock(){return STEPS_PER_BLOCK;}

    public List<BlockNode> getBlockNodes(){return blockNodes;}

    public TrackRow getTrackRow(){return trackRow;}
    public Block getSpecificBlock(int blockIndex){return blocks.get(blockIndex);}
    public BlockNode getSpecificBlockNode(int blockIndex){return blockNodes.get(blockIndex);}
}