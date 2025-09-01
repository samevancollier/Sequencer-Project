package sequencer.project.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import sequencer.project.model.InstrumentType;
import sequencer.project.model.Track;

public class TrackControl extends Pane { //should bve hbox...?
    private Label trackName;
    private Image instrumentIcon;
    private Button muteButton;
    private Button soloButton;
    private TrackRow trackRow;
    private Track track;

    private double dragStartY;
    private boolean isDragging = false;

    private VBox volumeControl;
    private Rectangle[] volumeButtons;
    private ThemeManager tm;



    public TrackControl(String trackName, InstrumentType instrumentType, TrackRow trackRow){
        this.trackRow=trackRow;
        this.track=trackRow.getTrack();
        this.volumeButtons=new Rectangle[9];
        this.tm=ThemeManager.getInstance();
        this.setPadding(new Insets(0));
        this.setMinWidth(100);this.setMinHeight(100);this.setMaxWidth(100);this.setMaxHeight(100);
        
        setUpVolumeControls();
        
        
        
        

        String labelText;
        if(trackName.contains(" ")){
            int spaceIndex=trackName.indexOf(" ");
            String firstPart=trackName.substring(0,spaceIndex);
            String secondPart=trackName.substring(spaceIndex+1);
            labelText=instrumentType.toString().toLowerCase()+"/\n"+firstPart.toLowerCase()+"\n"+secondPart.toLowerCase();
        }else{
            labelText=instrumentType.toString().toLowerCase()+"/\n"+trackName.toLowerCase();
        }
        this.trackName=new Label(labelText);
        this.trackName.setLayoutX(15); this.trackName.setLayoutY(00);
        VBox buttonBox=new VBox();
        buttonBox.setSpacing(0);
        buttonBox.setLayoutX(80);buttonBox.setLayoutY(0);
        
        // Create mute and solo buttons
        muteButton=new Button("M");
        soloButton=new Button("S");
        styleButton(muteButton);
        styleButton(soloButton);
        style();

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
        this.getChildren().addAll(this.trackName, volumeControl,buttonBox);
        //mouse click on controlz
        setOnMouseClicked(this::onTrackControlClicked);
        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);

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

    private void style(){
        ThemeManager tm=ThemeManager.getInstance();
        this.setStyle("-fx-background-color: " + trackRow.getColour().toString().replace("0x", "#") + "; -fx-border-color: " + tm.getLineColour().toString().replace("0x", "#") + "; -fx-border-width: 1px");
        trackName.setFont(tm.getFont(14));
        trackName.setStyle("-fx-text-fill: white;"); //HARDCODED, BAD, use TM< also different colours for selected style etc
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
