package sequencer.project.GUI;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import sequencer.project.model.InstrumentType;

public class TopControlBar extends HBox{
    
    private GUIController controller;
    
    // project controls
    private Button folderButton;
    private Button paintbrushButton;
    private Button settingsButton;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private MenuButton addTrackButton;

   
    // project name
    private TextField projectNameField;
    
    // transport controls
    
    
    // time signature
    private Label timeSignatureLabel;
    private Label bPMLabel;
    
    public TopControlBar(GUIController controller){
        this.controller=controller;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        this.setPrefHeight(50);
    }
    
    private void initializeComponents(){
        // project control buttons
        folderButton=new Button("FOLDER");
        paintbrushButton=new Button("PAINTBRUSH");
        settingsButton=new Button("SETTINGS");
        playButton=new Button("▶");
        pauseButton=new Button("⏸");
        stopButton=new Button("⏹");

        addTrackButton=new MenuButton("+");

        
        // project name field
        projectNameField=new TextField("untitled project");
        projectNameField.setPrefWidth(200); projectNameField.setPrefHeight(50);
        
        // transport controls (redundant)
        
        
        // time signature
        timeSignatureLabel=new Label("4/4");
        timeSignatureLabel.setFont(Font.font(14));

        bPMLabel=new Label("120");
        timeSignatureLabel.setFont(Font.font(14));
        
        // style the project buttons
        styleButton(folderButton);
        styleButton(paintbrushButton);
        styleButton(settingsButton);
        styleButton(playButton);
        styleButton(pauseButton);
        styleButton(stopButton);
        styleMenuButton(addTrackButton);

        styleAsButton(bPMLabel);styleAsButton(timeSignatureLabel);

        //set up buttons

        setUpAddTrackButton();
        
        // style project name field
        projectNameField.setStyle("-fx-background-color: #3a3a3a; -fx-text-fill: white; -fx-border-color: #555; -fx-border-radius: 3; -fx-background-radius: 3;");
        
        // style time signature
        timeSignatureLabel.setStyle("-fx-text-fill: white;");
    }
    
    private void setupLayout(){
        // set overall container properties
        this.setAlignment(Pos.CENTER);
        this.setSpacing(0);
        this.setPadding(new Insets(0));
        this.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #555; -fx-border-width: 0 0 1 0;");
        
        
        
        // add all components to the hbox
        this.getChildren().addAll(
            folderButton,
            paintbrushButton, 
            settingsButton,
            projectNameField,
            playButton,pauseButton,stopButton,addTrackButton,
            timeSignatureLabel,bPMLabel
        );
    }
    
    private void setupEventHandlers(){
        folderButton.setOnAction(e->{
            System.out.println("folder button clicked");
            // show file menu for save/open/new
        });
        
        paintbrushButton.setOnAction(e->{
            System.out.println("paintbrush button clicked");
            // show skin selection
        });
        
        settingsButton.setOnAction(e->{
            System.out.println("settings button clicked");
            // show settings dialog
        });
        playButton.setOnAction(e->{
            System.out.println("playing");

        });
        pauseButton.setOnAction(e->{
            System.out.println("paused");
       
        });
        stopButton.setOnAction(e->{
            System.out.println("stop");
        });
        
        addTrackButton.setOnAction(e->{
            
        });
        
        projectNameField.setOnAction(e->{
            System.out.println("project name changed to: "+projectNameField.getText());
            // save project name
        });
    }
    private void setUpAddTrackButton(){                                     //sigificantly more trouble than worth
        Map<String,Menu> categoryMenus=new HashMap<>();
        for(InstrumentType i : InstrumentType.values()){
            if(i.isMainCategory()){
                Menu newMenu=new Menu(i.toString());
                categoryMenus.put(i.toString(),newMenu);
                addTrackButton.getItems().add(newMenu);
            }
        }
        for(InstrumentType i : InstrumentType.values()){
            if(!(i.isMainCategory())){
                MenuItem newMenuItem=new MenuItem(i.toString());
                newMenuItem.setOnAction(e->controller.getContainer().addTrack(i.toString(),i.getMainCategoryAsType()));
                
                String parentCategoryName=i.getMainCategory().toString(); 
                Menu parentMenu=categoryMenus.get(parentCategoryName);
                if(parentMenu!=null){
                    parentMenu.getItems().add(newMenuItem);
                }
            }
        }
    }
    private void styleButton(Button button){
        button.setMinWidth(50); button.setMaxWidth(50);
        button.setMinHeight(50); button.setMaxHeight(50);
        button.setPrefHeight(50);button.setPrefWidth(50);
        button.setFont(Font.font(16));
        button.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 0; -fx-background-radius: 0;");
        
        // hover effect
        button.setOnMouseEntered(e->button.setStyle("-fx-background-color: #5a5a5a; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 3; -fx-background-radius: 3;"));
        button.setOnMouseExited(e->button.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 3; -fx-background-radius: 3;"));
    }

    private void styleMenuButton(MenuButton button){
        button.setMinWidth(50); button.setMaxWidth(50);
        button.setMinHeight(50); button.setMaxHeight(50);
        button.setPrefHeight(50);button.setPrefWidth(50);
        button.setFont(Font.font(16));
        button.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 0; -fx-background-radius: 0;");
        
        // hover effect
        button.setOnMouseEntered(e->button.setStyle("-fx-background-color: #5a5a5a; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 3; -fx-background-radius: 3;"));
        button.setOnMouseExited(e->button.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 3; -fx-background-radius: 3;"));
    }

    private void styleAsButton(Label label){
        label.setMinHeight(50);
        label.setMinWidth(50);
        label.setPrefHeight(50);label.setPrefWidth(50);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-border-color: #666; -fx-border-radius: 0; -fx-background-radius: 0;");
    }

    
    
    // getter for the controller to add this to layout
    public HBox getNode(){
        return this;
    }
    
  
    // method to get project name
    public String getProjectName(){
        return projectNameField.getText();
    }
    
    // method to set project name
    public void setProjectName(String name){
        projectNameField.setText(name);
    }
}