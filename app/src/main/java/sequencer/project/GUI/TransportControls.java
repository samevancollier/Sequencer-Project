package sequencer.project.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class TransportControls extends HBox { //redunant
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Label bpmLabel;
    public TransportControls(){
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(0);
        this.setPadding(new Insets(0));
        playButton=new Button("PLAY");
        pauseButton=new Button("PAUSE");
        stopButton=new Button("STOP");
        // Create BPM display
        bpmLabel=new Label("BPM: 140");
        bpmLabel.setFont(Font.font(14));
        bpmLabel.setPadding(new Insets(0));
        styleButton(playButton);
        styleButton(pauseButton);
        styleButton(stopButton);

        playButton.setOnAction(e->{System.out.println("play clicked");});   //obviously add behaivour later
        pauseButton.setOnAction(e->{System.out.println("pause clicked");});
        stopButton.setOnAction(e->{System.out.println("stop clicked");});

        this.getChildren().addAll(playButton, pauseButton, stopButton, bpmLabel);
        
    }
    private void styleButton(Button button){
        button.setMinWidth(50);
        button.setMinHeight(50);
        button.setFont(Font.font("Times New Roman", 12));
    }  
    public void setPlaying(){ //move to topcontrol bar...?
        playButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
    }
    public void setStopped() {
        playButton.setDisable(false);
        pauseButton.setDisable(true);//distinction between paused and stopped
        stopButton.setDisable(true);
    }
}
