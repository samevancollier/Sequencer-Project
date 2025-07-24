package sequencer.project.GUI;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class FXArea extends GridPane {
    private Button fXSlotOne;private Button fXSlotTwo;private Button fXSlotThree;private Button fXSlotFour;
    private TrackRow track;
    
    public FXArea(){
        fXSlotOne=new Button("+");fXSlotTwo=new Button("+");fXSlotThree=new Button("+");fXSlotFour=new Button("+");
        styleButton(fXSlotOne);styleButton(fXSlotTwo);styleButton(fXSlotThree);styleButton(fXSlotFour);
        
        // arrange in 2x2 grid: (column, row)
        add(fXSlotOne, 0, 0);   // top left
        add(fXSlotTwo, 1, 0);   // top right  
        add(fXSlotThree, 0, 1); // bottom left
        add(fXSlotFour, 1, 1);  // bottom right
        
        // remove gaps between buttons
        setHgap(0);
        setVgap(0);
    }
    
    private void styleButton(Button button){
        button.setMinWidth(50);button.setMaxWidth(50);
        button.setMinHeight(50);button.setMaxHeight(50);
        button.setPrefHeight(50);button.setPrefWidth(50);
        button.setFont(Font.font(16));
        button.setStyle("-fx-background-color: #6f5df8ff; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 0; -fx-background-radius: 0;");
    }
}