package sequencer.project.GUI;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Background extends Pane{
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer timer;
    
    // parallax layers for depth
    private ParallaxLayer[] layers;
    
    // scroll position
    private double scrollX=0;
    private double scrollSpeed=0.5; // pixels per frame
    private boolean isAutoScrolling=false; // automatic playback scrolling
    private boolean enableUserScroll=true; // user scroll input
    
    // big canvas approach - make it really fucking big
    private static final double BIG_CANVAS_WIDTH=8000; // should cover most tracks

    private String backgroundImagePath;
    private ThemeManager tm = ThemeManager.getInstance();
    
    public Background(double width,double height){
        this.canvas=new Canvas(width,height);
        this.gc=canvas.getGraphicsContext2D();
        backgroundImagePath=tm.getCurrentBackgroundImagePath();
        // initialize parallax layers
        initialiseLayers();
        
        // add canvas to pane
        getChildren().add(canvas);
        
        // bind canvas size to pane size
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        
        // redraw when size changes
        widthProperty().addListener((obs,oldVal,newVal)->draw());
        heightProperty().addListener((obs,oldVal,newVal)->draw());
        
        // start animation loop
        startAnimation();
        
        // add user scroll listeners
        setupUserScrolling();
    }
    
    private void setupUserScrolling(){
        // mouse scroll wheel
        setOnScroll(event->{
            if(enableUserScroll){
                double deltaX=event.getDeltaX();
                double deltaY=event.getDeltaY();
                
                // use horizontal scroll if available, otherwise vertical
                double scrollDelta=Math.abs(deltaX)>Math.abs(deltaY)?deltaX:deltaY;
                
                // invert scroll direction and scale
                scrollX-=scrollDelta*0.5;
                
                // prevent scrolling beyond the big canvas
                scrollX=Math.max(0,Math.min(scrollX,BIG_CANVAS_WIDTH));
            }
        });
        
        // mouse drag scrolling
        final double[] lastMouseX={0};
        final boolean[] isDragging={false};
        
        setOnMousePressed(event->{
            if(enableUserScroll){
                lastMouseX[0]=event.getX();
                isDragging[0]=true;
            }
        });
        
        setOnMouseDragged(event->{
            if(enableUserScroll&&isDragging[0]){
                double deltaX=lastMouseX[0]-event.getX();
                scrollX+=deltaX*0.8; // drag sensitivity
                scrollX=Math.max(0,Math.min(scrollX,BIG_CANVAS_WIDTH)); // clamp
                lastMouseX[0]=event.getX();
            }
        });
        
        setOnMouseReleased(event->{
            isDragging[0]=false;
        });
    }
    
    private void initialiseLayers(){
        layers=new ParallaxLayer[1];
        
        // create big background layer
        Image backgroundImage=new Image(backgroundImagePath);
        Image bigCanvas=createBigCanvas(backgroundImage,BIG_CANVAS_WIDTH,800); // use fixed height for now
        layers[0]=new ParallaxLayer(bigCanvas,1.0);
    }
    
    private Image createBigCanvas(Image sourceImage,double bigWidth,double bigHeight){
        Canvas bigCanvas=new Canvas(bigWidth,bigHeight);
        GraphicsContext bigGc=bigCanvas.getGraphicsContext2D();
        
        double sourceWidth=sourceImage.getWidth();
        double sourceHeight=sourceImage.getHeight();
        
        // scale image to fit height
        double scaleY=bigHeight/sourceHeight;
        double scaledWidth=sourceWidth*scaleY;
        
        // calculate how many times to repeat
        int numRepeats=(int)(bigWidth/scaledWidth)+1; // +1 for safety
        
        // draw repeated images across the big canvas
        for(int i=0;i<numRepeats;i++){
            bigGc.drawImage(sourceImage,i*scaledWidth,0,scaledWidth,bigHeight);
        }
        System.out.println("Source image size: " + sourceWidth + "x" + sourceHeight);
        System.out.println("Big canvas size: " + bigWidth + "x" + bigHeight);
        System.out.println("Scale: " + scaleY + ", Scaled width: " + scaledWidth);
        System.out.println("Num repeats: " + numRepeats);

        Image result = bigCanvas.snapshot(null,null);
        System.out.println("Snapshot result: " + (result != null));
        
        return bigCanvas.snapshot(null,null);
    }
    
    // method to load actual images
    public void setLayerImage(int layerIndex,String imagePath,double speed){
        if(layerIndex>=0&&layerIndex<layers.length){
            try{
                Image sourceImage=new Image(imagePath);
                Image bigCanvas=createBigCanvas(sourceImage,BIG_CANVAS_WIDTH,800);
                layers[layerIndex]=new ParallaxLayer(bigCanvas,speed);
            }catch(Exception e){
                System.err.println("failed to load image: "+imagePath);
            }
        }
    }

    public void changeBackground(String theme){         //here
       
        this.backgroundImagePath=tm.getCurrentBackgroundImagePath();
        //add handling for layered backgrounds
        setLayerImage(0, backgroundImagePath, 1);

    }
    
    private void startAnimation(){
        timer=new AnimationTimer(){
            @Override
            public void handle(long now){
                if(isAutoScrolling){
                    scrollX+=scrollSpeed;
                    scrollX=Math.max(0,Math.min(scrollX,BIG_CANVAS_WIDTH)); // clamp
                }
                draw();
            }
        };
        timer.start();
    }
    
    private void draw(){
        double width=canvas.getWidth();
        double height=canvas.getHeight();
        
        if(width<=0||height<=0)return;
        
        // clear canvas
        gc.clearRect(0,0,width,height);
        
        // draw each parallax layer
        for(ParallaxLayer layer:layers){
            drawLayer(layer,width,height);
        }
    }
    
    private void drawLayer(ParallaxLayer layer,double canvasWidth,double canvasHeight){
        if(layer.image==null)return;
        
        // calculate scroll offset for this layer
        double scrollScale = 5000; //speed of scorll here
        double layerScrollX=(scrollX*layer.speed)*scrollScale;
        
        // clamp to prevent drawing beyond the big canvas
        layerScrollX=Math.max(0,Math.min(layerScrollX,BIG_CANVAS_WIDTH-canvasWidth));
        
        // simple slice drawing - no loops, no modulo, no jumps
        gc.drawImage(layer.image,
            layerScrollX,0,canvasWidth,canvasHeight, // source rectangle
            0,0,canvasWidth,canvasHeight); // destination rectangle
    }
    
    // control methods
    public void startAutoScrolling(){
        isAutoScrolling=true;
    }
    
    public void stopAutoScrolling(){
        isAutoScrolling=false;
    }
    
    public void setUserScrollEnabled(boolean enabled){
        this.enableUserScroll=enabled;
    }
    
    public boolean isUserScrollEnabled(){
        return enableUserScroll;
    }
    
    public void setScrollSpeed(double speed){
        this.scrollSpeed=speed;
    }
    
    public void setScrollPosition(double position){
        //System.out.println("Setting scroll to: " + position);
        this.scrollX=Math.max(0,Math.min(position,BIG_CANVAS_WIDTH));
        draw();
    }
    
    public double getScrollPosition(){
        return scrollX;
    }
    
    // manual scroll methods for external control
    public void scrollBy(double deltaX){
        this.scrollX+=deltaX;
        this.scrollX=Math.max(0,Math.min(scrollX,BIG_CANVAS_WIDTH));
    }
    
    public void scrollTo(double position){
        this.scrollX=Math.max(0,Math.min(position,BIG_CANVAS_WIDTH));
    }
    
    // cleanup
    public void dispose(){
        if(timer!=null){
            timer.stop();
        }
    }
    
    // inner class for parallax layers
    private static class ParallaxLayer{
        Image image; // now holds the big pre-rendered canvas
        double speed; // multiplier for scroll speed
        
        ParallaxLayer(Image image,double speed){
            this.image=image;
            this.speed=speed;
        }
    }
}