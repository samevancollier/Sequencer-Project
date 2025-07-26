package sequencer.project.GUI;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import sequencer.project.model.Block;
import sequencer.project.model.Track;
import java.util.ArrayList;
import java.util.List;

public class ClipArea extends Pane {
    private TrackRow trackRow;
    private Track track;
    private List<BlockNode> blockNodes;
    private BlockNode selectedBlock;
    
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
    private List<Block> blocks=new ArrayList<Block>();

    private int extraSpace=500;
    
    
    public ClipArea(TrackRow trackRow){
        this.trackRow=trackRow;
        this.track=trackRow.getTrack();
        this.blockNodes=new ArrayList<>();
        this.lines=new ArrayList<>();
        
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
        
        
        setStyle("-fx-background-color: #ffffff;");
        drawLines();
    }
    
    private void setupMouseHandlers(){
        setOnMouseClicked(this::onAreaClicked);
    }
    
    private void onAreaClicked(MouseEvent e){
        // clicking on empty area deselects blocks
        if(e.getTarget()==this){
            selectBlock(null);
        }
    }
    private void drawLines(){
        getChildren().removeAll(lines);
        lines.clear();
        for(int i=50;i<totalWidth;i+=50){
            if(!(i%200==0)){
                Line newLine=new Line(i,0,i,CLIP_HEIGHT);
                newLine.setStroke(Color.web("#00ff08ff"));
                newLine.setStrokeWidth(1.0);
                lines.add(newLine);
                getChildren().add(newLine);
                continue;
            }
            Line newBlockLine=new Line(i,0,i,CLIP_HEIGHT);
            newBlockLine.setStroke(Color.web("#081309ff"));
            newBlockLine.setStrokeWidth(1.0);
            lines.add(newBlockLine);
            getChildren().add(newBlockLine);
        }
    }
    private void drawGrid(){ //old method IGNORE
        // clear existing grid lines
        //getChildren().removeAll(gridLines);
        //gridLines.clear();
        
        
        for(int blockIndex=0;blockIndex<Math.max(1,blocks.size());blockIndex++){
            double blockStartX=blockIndex*barWidthInPixels;
            
            // vertical lines within each block
            for(int i=0;i<=barWidthInPixels;i++){
                double x=blockStartX+i;
                
                if(i%32==0){
                    Line line=new Line(x,0,x,CLIP_HEIGHT);
                    line.setStroke(Color.web("#ffffffff"));
                    line.setStrokeWidth(1.0);
                    lines.add(line);
                    getChildren().add(line);
                }
                if(i%interval==0){
                    Line line=new Line(x,0,x,CLIP_HEIGHT);
                    line.setStroke(Color.web("#5e5e5eff"));
                    line.setStrokeWidth(1.0);
                    lines.add(line);
                    getChildren().add(line);
                }
            }
            
            // block boundary lines
            if(blockIndex>0){
                Line line=new Line(blockStartX,0,blockStartX,CLIP_HEIGHT);
                line.setStroke(Color.web("#ff0000ff"));
                line.setStrokeWidth(2);
                lines.add(line);
                getChildren().add(line);
            }
        }
    }
    
    public void refreshBlocks(){
        // remove all existing block nodes
        for(BlockNode node:blockNodes){
            node.refresh();
        }
    }
    
    public void selectBlock(BlockNode block){
        // deselect previous block
        if(selectedBlock!=null){
            selectedBlock.setSelected(false);
        }
        
        selectedBlock=block;
        
        // select new block
        if(selectedBlock!=null){
            selectedBlock.setSelected(true);
        }
    }
    
    public void blockMoved(BlockNode blockNode,int newStep){
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
    
    public void createBlock(){
        
        int startStep=blocks.size()*STEPS_PER_BLOCK;
        Block newBlock=new Block(startStep,trackRow);
        blocks.add(newBlock);
        BlockNode newBlockNode=new BlockNode(this, blockNodes.size(), newBlock);
        blockNodes.add(newBlockNode);
        
        getChildren().add(newBlockNode);

        trackRow.getContainer().updateAllTrackWidths();
        setWidth(barWidthInPixels*blocks.size()+500);
        refreshBlocks();
        drawLines();
    }
    
    public void removeBlock(int blockIndex){
        
        if(blockIndex>=0&&blockIndex<blocks.size()){
            blocks.remove(blockIndex);
            trackRow.getContainer().updateAllTrackWidths();
            refreshBlocks();
            drawLines();
        }
    }
    
    public void setUniformWidth(int totalBlocks){
        
        double newWidth=((barWidthInPixels*totalBlocks)+500);
        System.out.println("Setting width to: "+newWidth+" (totalBlocks: "+totalBlocks+")");
        this.totalWidth=newWidth;
        setWidth(newWidth);
        setPrefWidth(newWidth);
        setMinWidth(newWidth);
        setMaxWidth(newWidth);
        System.out.println("New width is: "+getWidth());
        
        refreshBlocks();
        drawLines();
    }
    
    public void deleteSelectedBlock(){
        if(selectedBlock!=null){
            // remove from model
            //track.removeBlock(selectedBlock.getBlock()); LATER
            
            // remove from ui
            getChildren().remove(selectedBlock);
            blockNodes.remove(selectedBlock);
            
            selectedBlock=null;
        }
    }
    /* 
    public void setStepWidth(double stepWidth){
        this.stepWidth=stepWidth;
        
        // update area width
        double totalWidth=totalSteps*stepWidth;
        setPrefWidth(totalWidth);
        setMinWidth(totalWidth);
        
        // refresh all blocks to use new step width
        for(BlockNode node:blockNodes){
            node.refresh();
        }
        
        // redraw grid
        drawGrid();
    }*/
    
    // getters 
    public List<Block> getBlocks(){
        return blocks;
    }
    
    public double getBarWidthInPixels(){
        return barWidthInPixels;
    }
    
    public int getStepsPerBlock(){
        return STEPS_PER_BLOCK;
    }
}