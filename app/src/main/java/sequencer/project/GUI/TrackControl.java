package sequencer.project.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import sequencer.project.model.InstrumentType;

public class TrackControl extends VBox {
    private Label trackName;
    private Label instrumentIcon;
    private Button muteButton;
    private Button soloButton;

    public TrackControl(String trackName, InstrumentType instrumentType){
        this.setSpacing(0);
        this.setPadding(new Insets(0));
        this.setAlignment(Pos.TOP_LEFT);
        this.setMinWidth(200);this.setMinHeight(200);
        this.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");

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

    }
    private void styleButton(Button button){
        button.setMinWidth(30);
        button.setMinHeight(25);
        button.setMaxWidth(30);
        button.setMaxHeight(25);
        button.setFont(Font.font("Times New Roman", 10));
    }
}
