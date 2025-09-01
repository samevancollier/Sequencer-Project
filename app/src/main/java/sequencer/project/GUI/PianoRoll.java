package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.g;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import sequencer.project.model.Block;
import sequencer.project.model.Note;

public class PianoRoll extends VBox {
    private HBox ruler;

    
    private HBox keysAndGrid;



    private Pane keyboard;
    private Pane gridPane;
    private Pane noteLayer;
    private Pane backgroundPane;

    private int keyboardWidth=30;
    private int keyHeight=12;

    private int resolution=2;                                       

    private int totalHeight = 120 * keyHeight;//HERE

    private BlockNode openBlockNode;

    private ThemeManager tm;
    private ArrayList<Rectangle> whiteNotes;
    private ArrayList<Rectangle> sharpNotes;

    private int octave=6; //playd aocateve
    ContextMenu contextMenu;
    //styling stuff: should be controlled by themeL add getters and setters

    private Color oneThreeBarBackground;
    private Color twoFourBarBackground;
    private Color heaviestLine;
    private Color sharpNoteFill;
    private Color whiteNoteFill;
    
    private Color heavyLine;
    private Color lightLine;
    private Color lightestLine;
    private Color faintWhiteNoteLine;

    private Color noteColour=Color.rgb(255, 108, 108, 1);

    private ArrayList<GraphicNote> notes=new ArrayList<GraphicNote>();
    private ArrayList<GraphicNote> selectedNotes=new ArrayList<GraphicNote>();

    private HashMap<GraphicNote, Note> graphicNotesAndNotes=new HashMap<GraphicNote, Note>();//broken i think
    //random
    private Rectangle selectionBox;
    private boolean isDragging=false;
    private boolean shiftPressed=false;
    private double startX, startY;
    private static final double DRAG_THRESHOLD = 5.0; // min amount to trigger dragging



    double width=1200-keyboardWidth;
    double barWidth=width/4;
    
        
    double quarterNoteWidth=width/16;
    double eighthNoteWidth=width/32;
    double sixteenthNoteWidth=width/64;
    double thirtytwothNoteWidth=width/128;
    double sixtyfourthNoteWidth=width/256;

    double subdivisionWidth=barWidth/Math.pow(2,resolution+1);//calc base to the power of exponent to to get subdivision width 

    

    public PianoRoll(){
        initaliseComponents();
        setupRightClickMenu();
        
 
        
        
    }
    /*RESOLUTIONS:
     * 0: BAR
     * 1:QUARTER
     * 2:EIGHTH
     * 3 SIXTEENTH
     * 4 1/32
     * and so on.
     */
    public void createNote(double x, double y, MouseEvent e){    
        double quantizedX=x;
        if(resolution==0){
            quantizedX=x-(x%barWidth);
        }else{
            quantizedX=x-(x%subdivisionWidth);
        }

        double quantizedY=y-(y%keyHeight);
        System.out.println("QUANTIZED y: " + quantizedY);
        GraphicNote newGraphicNote=new GraphicNote(quantizedX, quantizedY,subdivisionWidth,keyHeight, this);
        notes.add(newGraphicNote);
        newGraphicNote.setFill(noteColour);
        shiftPressed=e.isShiftDown();
        newGraphicNote.select();

        System.out.println("clicked at: "+x+", quantized to: "+quantizedX+", subdivisionWidth: "+subdivisionWidth);
        double testGridLine=Math.round(quantizedX/(barWidth/Math.pow(2,resolution+1)))*(barWidth/Math.pow(2,resolution+1));
        System.out.println("quantizedX: "+quantizedX+", nearest grid should be: "+testGridLine+", difference: "+(quantizedX-testGridLine));

        noteLayer.getChildren().add(newGraphicNote);

        //backend communication
        int step=(int)(quantizedX/subdivisionWidth) * (int)Math.pow(2, (5-resolution)); //seems fine
        int pitch=119-Math.abs((int)quantizedY/keyHeight);
        int length=0;
        if(resolution==5){  //could do this a nicer way with math.pow
            length=1;
        }else if(resolution==4){
            length=2;
        }else if(resolution==3){
            length=4;
        }else if(resolution==2){
            length=8;
        }else if(resolution==1){
            length=16;
        }else if(resolution==0){
            length=64;
        }

      
        Note newNote=new Note(pitch, step, length);
        openBlockNode.addNote(newNote);
        graphicNotesAndNotes.put(newGraphicNote, newNote);//useless now
        newGraphicNote.setNote(newNote);
        openBlockNode.getGraphicNotes().add(newGraphicNote);//HERE
        System.out.println(graphicNotesAndNotes.toString());
        System.out.println("note added att"+step + "pitch:" + pitch);
    }

    public void deleteNote(GraphicNote g){
        //System.out.print(graphicNotesAndNotes.toString());
        openBlockNode.removeNote(g.getNote());
        openBlockNode.getGraphicNotes().remove(g);//HERE
        notes.remove(g);
        //graphicNotesAndNotes.remove(g);
        noteLayer.getChildren().remove(g);
        selectedNotes.remove(g);  
        
    }

    private void initaliseComponents(){
        style();
        gridPane=new Pane();
        noteLayer=new Pane();
        backgroundPane=new Pane();
        ScrollPane keyboardScrollPane = new ScrollPane();
        keyboardScrollPane.setFocusTraversable(false);
        
        ScrollPane gridScrollPane = new ScrollPane();
        keyboardScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        keyboardScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        keyboardScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        
        gridScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gridScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        

        keyboardScrollPane.setPrefWidth(keyboardWidth);
        keyboardScrollPane.setMaxWidth(keyboardWidth);
        keyboardScrollPane.setMinWidth(keyboardWidth);
        keyboardScrollPane.vvalueProperty().bindBidirectional(gridScrollPane.vvalueProperty());
        

        keysAndGrid=new HBox();
        setUpGrid();
        setUpKeys();
        drawVerticalLines();
        

        keyboardScrollPane.setContent(keyboard);
        
        noteLayer.setFocusTraversable(true); // allows it to receive focus
        //noteLayer.setOnMouseClicked(this::onMouseClicked);
        noteLayer.setOnKeyPressed(this::handleKeyPress);
        noteLayer.setOnMousePressed(this::onMousePressed);
        noteLayer.setOnMouseDragged(this::onMouseDragged);  
        noteLayer.setOnMouseReleased(this::onMouseReleased);
        
        StackPane rightside=new StackPane(backgroundPane,gridPane,noteLayer);
        gridScrollPane.setContent(rightside);
        keysAndGrid.getChildren().addAll(keyboardScrollPane, gridScrollPane);

        keysAndGrid.setPrefHeight(1200); 
        keysAndGrid.setPrefWidth(800);   
        
        this.getChildren().add(keysAndGrid);
        
        VBox.setVgrow(keysAndGrid, Priority.ALWAYS);   
    }

    private void style(){
        tm=ThemeManager.getInstance();
        oneThreeBarBackground=tm.getOneAndThreeBG();
        twoFourBarBackground=tm.getTwoAndFourBG();
        heaviestLine=tm.getHeaviestPianoRollLine();heavyLine=tm.getHeavyPianoRollLine();lightLine=tm.getLightPianoRollLine();lightestLine=tm.getLightestPianoRollLine();
        sharpNoteFill=tm.getSharpNoteFill();
        faintWhiteNoteLine=tm.getWhiteNoteDivider();

    }

    private void handleKeyPress(KeyEvent e){
        if(e.getCode()==KeyCode.DELETE || e.getCode()==KeyCode.BACK_SPACE){
            System.out.println("Deleting selected notes");
            for(int i=selectedNotes.size()-1;i>=0;i--){
                deleteNote(selectedNotes.get(i));
            }
        }else if(e.getCode()==KeyCode.UP  || e.getCode()==KeyCode.DOWN  || e.getCode()==KeyCode.LEFT  || e.getCode()==KeyCode.RIGHT ){
            System.out.println("Key pressed. Selected notes count: " + selectedNotes.size());
            for(GraphicNote g : selectedNotes){
                g.move(e);
            }
            
        }
    }

    private void setupRightClickMenu(){
        contextMenu=new ContextMenu();
        MenuItem header=new MenuItem("View");
        header.setDisable(true);
        MenuItem res1=new MenuItem("1/4");
        MenuItem res2=new MenuItem("1/8");
        MenuItem res3=new MenuItem("1/16");
        MenuItem res4=new MenuItem("1/32");
        MenuItem res5=new MenuItem("1/64");
        
        res1.setOnAction(e->setResolution(1));
        res2.setOnAction(e->setResolution(2));
        res3.setOnAction(e->setResolution(3));
        res4.setOnAction(e->setResolution(4));
        res5.setOnAction(e->setResolution(5));
        
        contextMenu.getItems().addAll(header,res1,res2,res3,res4,res5);
        
    }
    /*private void onMousePressed(MouseEvent e){
        if(e.getButton()==MouseButton.PRIMARY && e.getTarget()==noteLayer){
            startX=e.getX();
            startY=e.getY();
            isDragging=true;
            shiftPressed=e.isShiftDown();
            
            // create selection box
            selectionBox=new Rectangle();
            selectionBox.setFill(Color.TRANSPARENT);                        //HARDCODED COLOURS HERE
            selectionBox.setStroke(Color.BLUE);
            selectionBox.setStrokeWidth(1);
            selectionBox.getStyleClass().add("selection-box");          //IDK
            selectionBox.setX(startX);
            selectionBox.setY(startY);
            noteLayer.getChildren().add(selectionBox);
        }
    }*/

    private void onMouseDragged(MouseEvent e){
        if(!isDragging && startX != 0 && startY != 0){
            double dx = e.getX() - startX;
            double dy = e.getY() - startY;
            if(Math.abs(dx) > DRAG_THRESHOLD || Math.abs(dy) > DRAG_THRESHOLD){
                isDragging=true;
                selectionBox=new Rectangle();
                selectionBox.setFill(Color.TRANSPARENT);
                selectionBox.setStroke(Color.BLUE);
                selectionBox.setStrokeWidth(1);
                selectionBox.setX(startX);
                selectionBox.setY(startY);
                noteLayer.getChildren().add(selectionBox);
            }
        }
        
        if(isDragging && selectionBox!=null){
            double currentX=e.getX();
            double currentY=e.getY();
            
            double minX=Math.min(startX,currentX);
            double minY=Math.min(startY,currentY);
            double width=Math.abs(currentX-startX);
            double height=Math.abs(currentY-startY);
            
            selectionBox.setX(minX);
            selectionBox.setY(minY);
            selectionBox.setWidth(width);
            selectionBox.setHeight(height);
            
            updateBoxSelection();
        }
    }

    private void onMouseReleased(MouseEvent e){
        if(isDragging){
            // finish box selection
            isDragging=false;
            if(selectionBox!=null){
                noteLayer.getChildren().remove(selectionBox);
                selectionBox=null;
            }
        } else if(e.getButton()==MouseButton.PRIMARY && e.getTarget()==noteLayer){
            // handle clicks (since we didn't drag)
            if(e.getClickCount()==2){
                createNote(startX, startY, e);
            } else if(e.getClickCount()==1){
                if(!shiftPressed){
                    for(GraphicNote g : selectedNotes){
                        g.deselect();
                    }
                    selectedNotes.clear();
                }
            }
        }
        
        startX=0;
        startY=0;
    }
    

    private void updateBoxSelection(){
        if(!isDragging || selectionBox==null) return;
        
        
        
        // get selection box bounds
        double boxMinX = selectionBox.getX();
        double boxMinY = selectionBox.getY();
        double boxMaxX = boxMinX + selectionBox.getWidth();
        double boxMaxY = boxMinY + selectionBox.getHeight();
        
        // check intersection with all notes using their actual x,y,width,height
        for(Node node : noteLayer.getChildren()){
            if(node instanceof GraphicNote){
                GraphicNote note = (GraphicNote)node;
                
                double noteMinX = note.getX();
                double noteMinY = note.getY();
                double noteMaxX = noteMinX + note.getWidth();
                double noteMaxY = noteMinY + note.getHeight();
                
                // check if rectangles overlap
                if(boxMaxX >= noteMinX && boxMinX <= noteMaxX && 
                boxMaxY >= noteMinY && boxMinY <= noteMaxY){
                    System.out.println("Note intersects, currently selected: " + selectedNotes.contains(note));
                    if(!selectedNotes.contains(note)){
                        selectedNotes.add(note);
                        note.select();
                        System.out.println("Added note, new size: " + selectedNotes.size());
                    }
                }
            }
        }
    }
    private void onMousePressed(MouseEvent e){
        if(e.getButton()==MouseButton.SECONDARY){
            contextMenu.show(gridPane,e.getScreenX(),e.getScreenY());
            return;
        }
        
        if(e.getButton()==MouseButton.PRIMARY && e.getTarget()==noteLayer){
            noteLayer.requestFocus();
            startX=e.getX();
            startY=e.getY();
            shiftPressed=e.isShiftDown();
        }
    }
    private void drawOctave(int octave){
        whiteNotes = new ArrayList<>();
        sharpNotes = new ArrayList<>();
        
        int baseY=(9-octave)*(keyHeight*12);
        Rectangle c=new Rectangle(0,baseY+keyHeight*11,keyboardWidth,keyHeight);    Rectangle cBackground=new Rectangle(0,baseY+keyHeight*11,1200-keyboardWidth,keyHeight);
        Rectangle d=new Rectangle(0,baseY+keyHeight*9,keyboardWidth,keyHeight);     Rectangle dBackground=new Rectangle(0,baseY+keyHeight*9,1200-keyboardWidth,keyHeight);
        Rectangle e=new Rectangle(0,baseY+keyHeight*7,keyboardWidth,keyHeight);     Rectangle eBackground=new Rectangle(0,baseY+keyHeight*7,1200-keyboardWidth,keyHeight);
        Rectangle f=new Rectangle(0,baseY+keyHeight*6,keyboardWidth,keyHeight);     Rectangle fBackground=new Rectangle(0,baseY+keyHeight*6,1200-keyboardWidth,keyHeight);
        Rectangle g=new Rectangle(0,baseY+keyHeight*4,keyboardWidth,keyHeight);     Rectangle gBackground=new Rectangle(0,baseY+keyHeight*4,1200-keyboardWidth,keyHeight);
        Rectangle a=new Rectangle(0,baseY+keyHeight*2,keyboardWidth,keyHeight);     Rectangle aBackground=new Rectangle(0,baseY+keyHeight*2,1200-keyboardWidth,keyHeight);
        Rectangle b=new Rectangle(0,baseY,keyboardWidth,keyHeight);                 Rectangle bBackground=new Rectangle(0,baseY,1200-keyboardWidth,keyHeight);
        c.setFill(Color.WHITE);
        d.setFill(Color.WHITE);
        e.setFill(Color.WHITE);
        f.setFill(Color.WHITE);
        g.setFill(Color.WHITE);
        a.setFill(Color.WHITE);
        b.setFill(Color.WHITE);
        c.setStroke(Color.BLACK);
        d.setStroke(Color.BLACK);
        e.setStroke(Color.BLACK);
        f.setStroke(Color.BLACK);
        g.setStroke(Color.BLACK);
        a.setStroke(Color.BLACK);
        b.setStroke(Color.BLACK);
        cBackground.setFill(whiteNoteFill);
        dBackground.setFill(whiteNoteFill);
        eBackground.setFill(whiteNoteFill);
        fBackground.setFill(whiteNoteFill);
        gBackground.setFill(whiteNoteFill);
        aBackground.setFill(whiteNoteFill);
        bBackground.setFill(whiteNoteFill);
        cBackground.setStroke(faintWhiteNoteLine);
        dBackground.setStroke(faintWhiteNoteLine);
        eBackground.setStroke(faintWhiteNoteLine);
        fBackground.setStroke(faintWhiteNoteLine);
        gBackground.setStroke(faintWhiteNoteLine);
        aBackground.setStroke(faintWhiteNoteLine);
        bBackground.setStroke(faintWhiteNoteLine);
        cBackground.setStrokeWidth(1);
        dBackground.setStrokeWidth(1);
        eBackground.setStrokeWidth(1);
        fBackground.setStrokeWidth(1);
        gBackground.setStrokeWidth(1);
        aBackground.setStrokeWidth(1);
        bBackground.setStrokeWidth(1);
        cBackground.setMouseTransparent(true);
        dBackground.setMouseTransparent(true);
        eBackground.setMouseTransparent(true);
        fBackground.setMouseTransparent(true);
        gBackground.setMouseTransparent(true);
        aBackground.setMouseTransparent(true);
        bBackground.setMouseTransparent(true);
        Rectangle cSharp=new Rectangle(0,baseY+keyHeight*10,keyboardWidth,keyHeight);  Rectangle cSharpBackground=new Rectangle(0,baseY+keyHeight*10,1200-keyboardWidth,keyHeight);
        Rectangle dSharp=new Rectangle(0,baseY+keyHeight*8,keyboardWidth,keyHeight);   Rectangle dSharpBackground=new Rectangle(0,baseY+keyHeight*8,1200-keyboardWidth,keyHeight);
        Rectangle fSharp=new Rectangle(0,baseY+keyHeight*5,keyboardWidth,keyHeight);   Rectangle fSharpBackground=new Rectangle(0,baseY+keyHeight*5,1200-keyboardWidth,keyHeight);
        Rectangle gSharp=new Rectangle(0,baseY+keyHeight*3,keyboardWidth,keyHeight);   Rectangle gSharpBackground=new Rectangle(0,baseY+keyHeight*3,1200-keyboardWidth,keyHeight);
        Rectangle aSharp=new Rectangle(0,baseY+keyHeight*1,keyboardWidth,keyHeight);   Rectangle aSharpBackground=new Rectangle(0,baseY+keyHeight*1,1200-keyboardWidth,keyHeight);
        cSharp.setFill(Color.BLACK);
        dSharp.setFill(Color.BLACK);
        fSharp.setFill(Color.BLACK);
        gSharp.setFill(Color.BLACK);
        aSharp.setFill(Color.BLACK);
       
        cSharpBackground.setFill(sharpNoteFill);
        dSharpBackground.setFill(sharpNoteFill);
        fSharpBackground.setFill(sharpNoteFill);
        gSharpBackground.setFill(sharpNoteFill);
        aSharpBackground.setFill(sharpNoteFill);
       
        cSharpBackground.setMouseTransparent(true);
        dSharpBackground.setMouseTransparent(true);
        fSharpBackground.setMouseTransparent(true);
        gSharpBackground.setMouseTransparent(true);
        aSharpBackground.setMouseTransparent(true);
        
        whiteNotes.addAll(Arrays.asList(c, d, e, f, g, a, b));
        sharpNotes.addAll(Arrays.asList(cSharp, dSharp, fSharp, gSharp, aSharp));
        
        keyboard.getChildren().addAll(c,cSharp,d,dSharp,e,f,fSharp,g,gSharp,a,aSharp,b);
        gridPane.getChildren().addAll(cSharpBackground,dSharpBackground,fSharpBackground,gSharpBackground,aSharpBackground,cBackground,dBackground,eBackground,fBackground,gBackground,aBackground,bBackground);
    }

    private void setUpGrid(){
        noteLayer.setStyle("-fx-background-color: transparent;");
        noteLayer.setPrefWidth(1200-keyboardWidth); 
        noteLayer.setPrefHeight(totalHeight);
        gridPane.setStyle("-fx-background-color: transparent");
        gridPane.setPrefWidth(1200-keyboardWidth); 
        gridPane.setPrefHeight(totalHeight);
        backgroundPane.setStyle("-fx-background-color: transparent;");
        backgroundPane.setPrefWidth(1200-keyboardWidth); 
        backgroundPane.setPrefHeight(totalHeight);

        double width=1200-keyboardWidth;
        //bar backgrounds
        for(int i=0;i<4;i++){
            Rectangle barBg=new Rectangle((width/4)*i,0,width/4,totalHeight);
            barBg.setMouseTransparent(true);
            if(i%2==0){
                barBg.setFill(oneThreeBarBackground); // slightly gray
            }else{
                barBg.setFill(twoFourBarBackground);
            }
            backgroundPane.getChildren().add(barBg);
        }
        //gridPane.setOnMouseClicked(this::onMouseClicked);
    }
    /*RESOLUTIONS:
     * 0: BAR
     * 1:QUARTER
     * 2:EIGHTH
     * 3 SIXTEENTH
     * 4 1/32
     * and so on.
     */
    private void drawVerticalLines(){
        for(int i=0;i<4;i++){
            double usefulNumber=((width/4)*i);
            
            // 1/64th notes (resolution 5)
            if(resolution>=5){
                double sixtyfourthWidth=barWidth/Math.pow(2,6); // 2^6 = 64
                for(int s=1;s<64;s++){
                    if(s%32!=0&&s%16!=0&&s%8!=0&&s%4!=0){
                        Line sixtyfourthLine=new Line(usefulNumber+(sixtyfourthWidth*s),0,usefulNumber+(sixtyfourthWidth*s),totalHeight);
                        sixtyfourthLine.setStroke(lightestLine);
                        gridPane.getChildren().add(sixtyfourthLine);
                    }
                }
            }
            
            // 1/32nd notes (resolution 4)
            if(resolution>=4){
                double thirtytwothWidth=barWidth/Math.pow(2,5); // 2^5 = 32
                for(int r=1;r<32;r++){
                    if(r%16!=0&&r%8!=0&&r%4!=0){
                        Line thirtytwothLine=new Line(usefulNumber+(thirtytwothWidth*r),0,usefulNumber+(thirtytwothWidth*r),totalHeight);
                        thirtytwothLine.setStroke(lightestLine);
                        gridPane.getChildren().add(thirtytwothLine);
                    }
                }
            }
            
            // 1/16th notes (resolution 3)
            if(resolution>=3){
                double sixteenthWidth=barWidth/Math.pow(2,4); // 2^4 = 16
                for(int t=1;t<16;t++){
                    if(t%8!=0&&t%4!=0){
                        Line sixteenthLine=new Line(usefulNumber+(sixteenthWidth*t),0,usefulNumber+(sixteenthWidth*t),totalHeight);
                        sixteenthLine.setStroke(lightestLine);
                        gridPane.getChildren().add(sixteenthLine);
                    }
                }
            }
            
            // 1/8th notes (resolution 2)
            if(resolution>=2){
                double eighthWidth=barWidth/Math.pow(2,3); // 2^3 = 8
                for(int y=1;y<8;y++){
                    if(y%4!=0){
                        Line eighthLine=new Line(usefulNumber+(eighthWidth*y),0,usefulNumber+(eighthWidth*y),totalHeight);
                        eighthLine.setStroke(lightLine);
                        gridPane.getChildren().add(eighthLine);
                    }
                }
            }

            // 1/4 notes (resolution 1)
            if(resolution>=1){
                double quarterWidth=barWidth/Math.pow(2,2); // 2^2 = 4
                for(int u=1;u<4;u++){
                    Line quarterLine=new Line(usefulNumber+(quarterWidth*u),0,usefulNumber+(quarterWidth*u),totalHeight);
                    quarterLine.setStroke(heavyLine);
                    gridPane.getChildren().add(quarterLine);
                }
            }

        // bar lines
        Line barLine=new Line(usefulNumber,0,usefulNumber,totalHeight);
        barLine.setStroke(heaviestLine);barLine.setStrokeWidth(2);
        gridPane.getChildren().add(barLine);
        }
    }

    private void redrawVerticalLines(){

        gridPane.getChildren().clear();         //not really optimal, create a drawhorizontal lines method, dont be lazy, will cause problems
        for(int i=0;i<10;i++){
            drawOctave(i);
        }
        drawVerticalLines();
    }

    private void setUpKeys(){
        keyboard=new Pane();
        
        keyboard.setMinWidth(keyboardWidth); keyboard.setMaxWidth(keyboardWidth);
        keyboard.setPrefWidth(keyboardWidth);

        keyboard.setPrefHeight(totalHeight);
        keyboard.setMinHeight(totalHeight);
        keyboard.setMaxHeight(totalHeight);
        

        keyboard.setStyle("-fx-background-color: lightgray;");
        Line rightBoundary=new Line(keyboardWidth,0,keyboardWidth,totalHeight); rightBoundary.setStroke(faintWhiteNoteLine); //fix
        for(int i=0;i<12;i++){
            drawOctave(i);
        }
        

        keyboard.getChildren().add(rightBoundary);
        
    }
    public void setOpenBlock(BlockNode openBlock){
        System.out.println("new block oepend");
        this.openBlockNode=openBlock;
        refresh();
    }

    private void refresh(){
        //clear existing notes
        getNoteLayer().getChildren().clear();
        
        //add notes from current block
        if(openBlockNode!=null){
            ArrayList<GraphicNote> blockNotes=openBlockNode.getGraphicNotes();
            for(int i=0;i<blockNotes.size();i++){
                getNoteLayer().getChildren().add(blockNotes.get(i));
            }
        }
    }

    public int subdivisionToStep(){
        switch(resolution){
            case 0: return 64;
            case 1: return 16;
            case 2: return 8;
            case 3: return 4;
            case 4: return 2;
            case 5: return 1;
        }
        return 0;
    }
    private void setResolution(int newResolution){resolution=newResolution;subdivisionWidth=barWidth/Math.pow(2,resolution+1);redrawVerticalLines();}
    public int getResolution(){return resolution;}
    public BlockNode getBlockNode(){return openBlockNode;}
    public double getSubdivisionWidth(){return subdivisionWidth;}
    public double getBarWidth(){return barWidth;}
    public int getKeyHeight(){return keyHeight;}

    public ArrayList<GraphicNote> getSelectedNotes(){return selectedNotes;}
    public ArrayList<GraphicNote> getAllNotes(){return notes;}
    public Pane getNoteLayer(){return noteLayer;}
    public HashMap getGraphicNotesAndNotes(){return graphicNotesAndNotes;}//useless
    public Boolean isShiftDown(){return shiftPressed;}
    public boolean getIsDragging(){return isDragging;}



    

}
