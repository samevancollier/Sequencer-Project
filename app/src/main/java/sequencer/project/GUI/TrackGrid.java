package sequencer.project.GUI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TrackGrid extends Canvas { //trash...
    private GraphicsContext graphicsContext;

    //dimensions

    private double cellWidth=1.0;
    private double trackHeight=60.0;
    private int numOfBeats=16;
    private int cellsPerBeat=16; //change all this later
    private int width;
    private int pitchRange; //hmmm

    private int resolution=64; //move this upward
    private int range=50; //range will be used to calc size of displayed notes, arbitrary number

    public TrackGrid(){
        this.setWidth(800.0);
        this.setHeight(70.0);
        this.graphicsContext=getGraphicsContext2D();
        
        drawGrid();
    }

    private void drawGrid(){
        // Clear and set background
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, getWidth(), getHeight());
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(1);
        //graphicsContext.strokeLine(0, 0, getWidth(), 0);         // Top edge
        //graphicsContext.strokeLine(0, getHeight(), getWidth(), getHeight()); // Bottom edge
        
        drawLines();
    }

    private void drawLines(){
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(1);
        //graphicsContext.strokeLine(0,0,getWidth(),0);
        for(int i=0;i<this.getWidth();i++){
            if(i%resolution==0){
                graphicsContext.strokeLine(i, 0, i, this.getHeight());
            }
            
        }
    }

    public void refresh(){
        //later...
    }

}
