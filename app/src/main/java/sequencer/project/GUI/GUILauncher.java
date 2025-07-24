package sequencer.project.GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUILauncher extends Application{
    @Override
    public void start(Stage primaryStage) {
        GUIController gUIController = new GUIController();
        // Create the main layout container
        //BorderPane root = new BorderPane();
        //root.setPadding(new Insets(0));
        
        // Create placeholder sections with labels
        TransportControls transportControls = new TransportControls();
        
        
        
        
        Label gridLabel = new Label("Sequencer Grid");
        gridLabel.setFont(Font.font(16));
        gridLabel.setTextFill(Color.DARKRED);
        gridLabel.setPadding(new Insets(20));
        
       
        // Create the scene and set up the window
        Scene scene = new Scene(gUIController.getRoot(),1200,800);
        
        // Configure the main window
        primaryStage.setTitle("Desktop Sequencer/Sampler");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Show the window
        primaryStage.show();
       
        System.out.println("Sequencer application started successfully!");
    }
    
    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}

