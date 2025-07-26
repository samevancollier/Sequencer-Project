package sequencer.project.GUI;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sequencer.project.model.Block;

public class BlockNode extends Pane {
    private int blockIndex;
    private double width;
    private Block block;
    private Rectangle rect;
    private ClipArea clipArea;
    private double dragStartX;
    private double dragStartY;
    private boolean isDragging=false;
    private double BLOCK_HEIGHT=100;
    private double BLOCK_WIDTH=200;
    private Color BLOCK_COLOUR=Color.BLUE;
    private Color BLOCK_SELECTED_COLOUR=Color.DARKBLUE;
    private Color BLOCK_BORDER_COLOUR=Color.RED;

    public BlockNode(ClipArea clipArea, int blockIndex, Block block){
        this.clipArea=clipArea;
        this.blockIndex=blockIndex;
        this.block=block;
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
        
        getChildren().add(rect);
        
        // make the pane match the rectangle size
        setPrefHeight(BLOCK_HEIGHT);
        setPrefWidth(BLOCK_WIDTH);
    }

    private void setupMouseHandlers(){
        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        setOnMouseClicked(this::onMouseClicked);
        
        // visual feedback on hover
        setOnMouseEntered(e->rect.setOpacity(0.8));
        setOnMouseExited(e->rect.setOpacity(1.0));
    }
    
    private void onMousePressed(MouseEvent e){
        dragStartX=e.getSceneX();
        dragStartY=e.getSceneY();
        isDragging=false;
        
        // bring to front for dragging
        toFront();
        
        // select this block
        clipArea.selectBlock(this);
        
        e.consume();
    }
    
    private void onMouseDragged(MouseEvent e){
        if(!isDragging){
            isDragging=true;
            // visual feedback for drag start
            rect.setOpacity(0.7);
        }
        
        double deltaX=e.getSceneX()-dragStartX;
        double deltaY=e.getSceneY()-dragStartY;
        
        // snap to grid during drag
        int nearestStep=snapToStep(getLayoutX()+deltaX);//WR OOOONG
        
        // update visual position immediately for smooth dragging
        setLayoutX(nearestStep*width); //ehhhhh
        
        dragStartX=e.getSceneX();
        dragStartY=e.getSceneY();
        
        e.consume();
    }
    
    private void onMouseReleased(MouseEvent e){
        if(isDragging){
            // finalize the drag - update the model
            int newStep=snapToStep(getLayoutX());
            block.setStartStep(newStep);
            
            // notify cliparea of the change
            clipArea.blockMoved(this,newStep);
            
            rect.setOpacity(1.0);
            isDragging=false;
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
    
    public Block getBlock(){
        return block;
    }
    
    public void refresh(){
        updatePosition();
        
    }
}
