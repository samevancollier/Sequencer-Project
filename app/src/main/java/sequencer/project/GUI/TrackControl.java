package sequencer.project.GUI;

import java.util.HashMap;
import java.util.Map;



import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sequencer.project.audio.MusicRoom;
import sequencer.project.model.InstrumentType;
import sequencer.project.model.Instrument;

import sequencer.project.model.Track;

public class TrackControl extends Pane { //should bve hbox...?
    private Label trackName;
    private Image instrumentIcon;
    private Button muteButton;
    private Button soloButton;
    private MenuButton instrumentButton;
    private TrackRow trackRow;
    private Track track;

    private double dragStartY;
    private boolean isDragging = false;

    private String labelText;

    private VBox volumeControl;
    private Rectangle[] volumeButtons;
    private Timeline visualiserTimer;
    private ThemeManager tm;

    private Color lowVolumeColour;
    private Color midVolumeColour;
    private Color highVolumeColour;
    private Color volumeFillColour;




    public TrackControl(String trackName, InstrumentType instrumentType, TrackRow trackRow){
        this.trackRow=trackRow;
        this.track=trackRow.getTrack();
        this.volumeButtons=new Rectangle[9];
        this.tm=ThemeManager.getInstance();
        this.setPadding(new Insets(0));
        this.setMinWidth(100);this.setMinHeight(100);this.setMaxWidth(100);this.setMaxHeight(100);
        
        setUpVolumeControls();
        
        
        
        


        if(trackName.contains(" ")){
            int spaceIndex=trackName.indexOf(" ");
            String firstPart=trackName.substring(0,spaceIndex);
            String secondPart=trackName.substring(spaceIndex+1);
            labelText=instrumentType.toString().toLowerCase()+"/\n"+firstPart.toLowerCase()+"\n"+secondPart.toLowerCase();
        }else{
            labelText=instrumentType.toString().toLowerCase()+"/\n"+trackName.toLowerCase();
        }
        this.trackName=new Label(labelText);
        this.trackName.setLayoutX(16); this.trackName.setLayoutY(00);
        HBox buttonBox=new HBox();
        buttonBox.setSpacing(0);
        buttonBox.setLayoutX(60);buttonBox.setLayoutY(80);

        

        
        // Create mute and solo buttons
        muteButton=new Button("M");
        soloButton=new Button("S");
        instrumentButton=new MenuButton("*");
        instrumentButton.setLayoutX(88);
        instrumentButton.setLayoutY(0);
        style();

        setUpInstrumentButton();

        muteButton.setOnAction(e->{
            System.out.println(trackName + " mute toggled");
            // Later: toggle mute state and update button appearance
        });
        
        soloButton.setOnAction(e->{
            System.out.println(trackName + " solo toggled");
            // Later: toggle solo state and update button appearance
        });

        buttonBox.getChildren().addAll(muteButton, soloButton);
        
        // Add all components to this VBox
        this.getChildren().addAll(instrumentButton,this.trackName, volumeControl,buttonBox );
        //mouse click on controlz
        setOnMouseClicked(this::onTrackControlClicked);
        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);

    }

    private void newName(Instrument newInstrument){
        String labelText;
        String newName=newInstrument.getName();
        if(newName.contains(" ")){
            int spaceIndex=newName.indexOf(" ");
            String firstPart=newName.substring(0,spaceIndex);
            String secondPart=newName.substring(spaceIndex+1);
            labelText=newInstrument.getInstrumentType().getMainCategory().toLowerCase()+"/\n"+firstPart.toLowerCase()+"\n"+secondPart.toLowerCase();
        }else{
            labelText=newInstrument.getInstrumentType().getMainCategory().toLowerCase()+"/\n"+newName.toLowerCase();
        }
        this.trackName.setText(labelText);
    }
    private void setUpInstrumentButton(){                                     //sigificantly more trouble than worth
        Map<String,Menu> categoryMenus=new HashMap<>();
        for(InstrumentType i : InstrumentType.values()){
            if(i.isMainCategory()){
                Menu newMenu=new Menu(i.toString());
                categoryMenus.put(i.toString(),newMenu);
                instrumentButton.getItems().add(newMenu);
            }
        }
        for(InstrumentType i : InstrumentType.values()){
            if(!(i.isMainCategory())){
                MenuItem newMenuItem=new MenuItem(i.toString());
                newMenuItem.setOnAction(e->{
   String instrumentKey=i.toString();
   if(instrumentKey.contains("_")){
       String[] words=instrumentKey.split("_");
       StringBuilder result=new StringBuilder();
       for(int j=0;j<words.length;j++){
           result.append(words[j].charAt(0)).append(words[j].substring(1).toLowerCase());
           if(j<words.length-1) result.append(" ");
       }
       instrumentKey=result.toString();
   }else{
       instrumentKey=instrumentKey.charAt(0) + instrumentKey.substring(1).toLowerCase();
   }
   track.setInstrument(MusicRoom.getInstance().getInstrument(instrumentKey));
   newName(track.getInstrument());
});
                
                String parentCategoryName=i.getMainCategory().toString(); 
                Menu parentMenu=categoryMenus.get(parentCategoryName);
                if(parentMenu!=null){
                    parentMenu.getItems().add(newMenuItem);
                }
            }
        }
    }


    private void setUpVolumeControls(){
        volumeControl=new VBox();
        ThemeManager tm=ThemeManager.getInstance();

        Rectangle rectangleOne=new Rectangle(10,10);
        rectangleOne.setFill(Color.TRANSPARENT);
        rectangleOne.setStroke(tm.getLineColour());

        Rectangle rectangleTwo=new Rectangle(10,10);
        rectangleTwo.setFill(Color.TRANSPARENT);
        rectangleTwo.setStroke(tm.getLineColour());

        Rectangle rectangleThree=new Rectangle(10,10);
        rectangleThree.setFill(Color.TRANSPARENT);
        rectangleThree.setStroke(tm.getLineColour());

        Rectangle rectangleFour=new Rectangle(10,10);
        rectangleFour.setFill(Color.TRANSPARENT);
        rectangleFour.setStroke(tm.getLineColour());

        Rectangle rectangleFive=new Rectangle(10,10);
        rectangleFive.setFill(Color.TRANSPARENT);
        rectangleFive.setStroke(tm.getLineColour());

        Rectangle rectangleSix=new Rectangle(10,10);
        rectangleSix.setFill(Color.TRANSPARENT);
        rectangleSix.setStroke(tm.getLineColour());

        Rectangle rectangleSeven=new Rectangle(10,10);
        rectangleSeven.setFill(Color.TRANSPARENT);
        rectangleSeven.setStroke(tm.getLineColour());

        Rectangle rectangleEight=new Rectangle(10,10);
        rectangleEight.setFill(Color.TRANSPARENT);
        rectangleEight.setStroke(tm.getLineColour());

        Rectangle rectangleNine=new Rectangle(10,10);
        rectangleNine.setFill(Color.TRANSPARENT);
        rectangleNine.setStroke(tm.getLineColour());

        volumeButtons[0]=rectangleOne;
        volumeButtons[1]=rectangleTwo;
        volumeButtons[2]=rectangleThree;
        volumeButtons[3]=rectangleFour;
        volumeButtons[4]=rectangleFive;
        volumeButtons[5]=rectangleSix;
        volumeButtons[6]=rectangleSeven;
        volumeButtons[7]=rectangleEight;
        volumeButtons[8]=rectangleNine;

        for(int i=0;i<volumeButtons.length;i++){
            final int volume=i+1;
            volumeButtons[i].setOnMouseClicked(e->{track.setVolume(volume);volumeFill(volume);});
        }

        
        volumeFill(6);


        volumeControl.getChildren().addAll(rectangleNine,rectangleEight,rectangleSeven,rectangleSix,rectangleFive,rectangleFour,rectangleThree,rectangleTwo,rectangleOne);
        volumeControl.setLayoutX(0);volumeControl.setLayoutY(0);        volumeControl.setLayoutX(0);volumeControl.setLayoutY(0);
        initaliseVolumeAnimation();
    }

    public void volumeFill(int volume){
        Color fill=tm.getVolumeFill(trackRow.getPallete());
        for(int i=0;i<volumeButtons.length;i++){
            if(i<volume){
                volumeButtons[i].setFill(fill);
            }else{
                volumeButtons[i].setFill(Color.TRANSPARENT);
            }
        }
    }

    private void initaliseVolumeAnimation(){
        visualiserTimer = new Timeline(new KeyFrame(Duration.millis(50), e -> updateVisualiser()));
        visualiserTimer.setCycleCount(Timeline.INDEFINITE);
        visualiserTimer.play();
    }

    private void updateVisualiser(){
        if(track == null) return;
        
        // force amplitude decay every updat
        track.decayAmplitude();
        
        int volume = track.getVolume();
        float amplitude = track.getCurrentAmplitude();
        

        //System.out.println("Amplitude: " + amplitude + ", Volume: " + volume);
        
        int litRectangles = Math.round(amplitude * 200); // try higher sensitivity
        litRectangles = Math.min(litRectangles, volume); // cap at volume setting
        
        //System.out.println("Lit rectangles: " + litRectangles);
     
        for(int i = 0; i < 9; i++){
            if(i < volume && i < litRectangles){
               
                if(i < 6){
                    volumeButtons[i].setFill(lowVolumeColour); 
                }else if(i < 8){
                    volumeButtons[i].setFill(midVolumeColour);
                }else{
                    volumeButtons[i].setFill(highVolumeColour); 
                }
            }else if(i<volume){
                volumeButtons[i].setFill(volumeFillColour); 
            }else{
                volumeButtons[i].setFill(Color.TRANSPARENT);
            }
               
        }
    }
    
    

    private void style(){
        ThemeManager tm=ThemeManager.getInstance();
        this.setStyle("-fx-background-color: " + trackRow.getColour().toString().replace("0x", "#") + "; -fx-border-color: " + tm.getLineColour().toString().replace("0x", "#") + "; -fx-border-width: 1px");
        trackName.setFont(tm.getFont(14));
        trackName.setStyle("-fx-text-fill: white;"); //HARDCODED, BAD, use TM< also different colours for selected style etc
        highVolumeColour=tm.getHighVolumeColour(trackRow.getPallete());
        midVolumeColour=tm.getMidVolumeColour(trackRow.getPallete());
        lowVolumeColour=tm.getLowVolumeColour(trackRow.getPallete());
        volumeFillColour=tm.getVolumeFill(trackRow.getPallete());
        instrumentButton.setPrefHeight(10);instrumentButton.setPrefWidth(10);
        instrumentButton.setFont(tm.getFont(4));
        instrumentButton.setMinWidth(12);
        instrumentButton.setMinHeight(12);
        instrumentButton.setMaxWidth(12);
        instrumentButton.setMaxHeight(12);
        instrumentButton.setPrefHeight(12);
        instrumentButton.setPrefWidth(12);
        instrumentButton.setStyle("-fx-border-color:"+tm.getLineColourToString()+"; -fx-background-color: transparent; -fx-padding: 0; -fx-text-fill: white; -fx-opacity: 1.0;");
        Platform.runLater(() -> {
            Node arrow = instrumentButton.lookup(".arrow-button");
            if(arrow != null) {
                arrow.setVisible(false);
                arrow.setManaged(false);
                instrumentButton.setText("*");
            }
        });
        styleButton(muteButton);styleButton(soloButton);

      //add check for if instrument spriteis null, use track name instead
    }
    private void styleButton(Button button){
        ThemeManager tm=ThemeManager.getInstance();
        button.setMinWidth(20);
        button.setMinHeight(20);
        button.setMaxWidth(20);
        button.setMaxHeight(20);
        button.setFont(tm.getFont(10));
        button.setOpacity(1);
        /*Image graphic=new Image(getClass().getResourceAsStream("/images/"+tm.getCurrentTheme()+"_"+button.getText()+".png"));
        ImageView image = new ImageView(graphic); 
        image.setPreserveRatio(true);
        button.setText("");*/
        button.setStyle("-fx-border-color:"+tm.getLineColourToString()+"; -fx-background-color: transparent; -fx-padding: 0;-fx-text-fill: white;");
    }

    private void onTrackControlClicked(MouseEvent e) {
        if(!isDragging){
            boolean isShiftHeld=e.isShiftDown();
            dragStartY = e.getSceneY();
            isDragging = false;
            trackRow.getContainer().selectTrack(trackRow, isShiftHeld);
            e.consume();
        }
    }
    

    private void onMouseDragged(MouseEvent e) {
        if (!isDragging) {
            isDragging = true;
            // visual feedback for drag start
            setOpacity(0.5);
            System.out.println("dragging" + trackRow.getContainer().getSelectedTracks().size()+" tracks");
        }
        
        double deltaY = e.getSceneY() - dragStartY;
        double trackHeight = trackRow.getTrackHeight();
        
        // if dragged more than half a track height, move the track
        if (Math.abs(deltaY) > trackHeight / 2) {
            if (deltaY < 0) {
                // dragged up
                trackRow.getContainer().moveTracksUp();
            } else {
                // dragged down
                trackRow.getContainer().moveTracksDown();
            }
            
            // reset drag start position
            dragStartY = e.getSceneY();
        }
        
        e.consume();
    }
    
    private void onMouseReleased(MouseEvent e) {
        if (isDragging) {
            setOpacity(1.0);
            isDragging = false;
        }
        e.consume();
    }
    
    private void onMousePressed(MouseEvent e){
        dragStartY = e.getSceneY();        // record start position
        isDragging = false; 
    }
}
