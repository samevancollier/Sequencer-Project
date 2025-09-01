package sequencer.project.model;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sequencer.project.GUI.ThemeManager;

public class Bitcrush implements AudioEffect {
    private int bitDepth=1;
    public short process(short sample){
        int reduction = 16-bitDepth;
        return (short) ((sample >> reduction) << reduction); //move all bits to the left by reduction positions
    }
    public void reset(){

    }
    public void setBitDepth(int reduction){
        bitDepth=reduction;
    }

    public void launchWindow(){
        ThemeManager tm=ThemeManager.getInstance();
        Stage stage=new Stage();
        stage.setTitle("BITCRUSH");
        stage.setWidth(300);
        stage.setHeight(300);
        
        VBox root=new VBox(10);
        root.setPadding(new Insets(20));
        
        Label label=new Label("Bit Depth:");
        Slider slider=new Slider(1,16,8);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        
        Label valueLabel=new Label("8");

        root.setStyle("-fx-background-color: "+tm.getDefaultColour()+" ;");
        label.setFont(tm.getFont(18));
        valueLabel.setFont(tm.getFont(15));
        
        slider.valueProperty().addListener((obs,oldVal,newVal)->{
            int bitDepth=newVal.intValue();
            valueLabel.setText(String.valueOf(bitDepth));
            setBitDepth(bitDepth);
        });
        
        root.getChildren().addAll(label,slider,valueLabel);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
