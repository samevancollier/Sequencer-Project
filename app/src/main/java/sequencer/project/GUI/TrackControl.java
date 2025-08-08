package sequencer.project.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import sequencer.project.model.InstrumentType;

public class TrackControl extends VBox { //should bve hbox...?
    private Label trackName;
    private Label instrumentIcon;
    private Button muteButton;
    private Button soloButton;
    private TrackRow trackRow;

    private double dragStartY;
    private boolean isDragging = false;

    public TrackControl(String trackName, InstrumentType instrumentType, TrackRow trackRow){
        this.trackRow=trackRow;
        this.setSpacing(0);
        this.setPadding(new Insets(0));
        this.setAlignment(Pos.TOP_LEFT);
        this.setMinWidth(100);this.setMinHeight(100);this.setMaxWidth(100);this.setMaxHeight(100);
        this.setStyle("-fx-background-color: #797979ff; -fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
       

        

        this.trackName=new Label(trackName);
        this.instrumentIcon=new Label(instrumentType.toString());

        HBox buttonBox=new HBox();
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        // Create mute and solo buttons
        muteButton=new Button("M");
        soloButton=new Button("S");
        styleButton(muteButton);
        styleButton(soloButton);

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
        this.getChildren().addAll(this.trackName, instrumentIcon, buttonBox);
        //mouse click on controlz
        setOnMouseClicked(this::onTrackControlClicked);
        setOnMousePressed(this::onMousePressed);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);

    }
    private void styleButton(Button button){
        button.setMinWidth(15);
        button.setMinHeight(15);
        button.setMaxWidth(15);
        button.setMaxHeight(15);
        button.setFont(Font.font("Times New Roman", 10));
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
