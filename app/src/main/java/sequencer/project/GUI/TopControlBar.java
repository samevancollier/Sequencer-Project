package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import sequencer.project.audio.AudioPlayer;
import sequencer.project.model.InstrumentType;
import sequencer.project.model.Project;

public class TopControlBar extends HBox{
    
    private GUIController controller;
    
    // project controls
    private Button folderButton;
    private MenuButton themeButton;
    private Button settingsButton;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private MenuButton addTrackButton;

    //backend

    private AudioPlayer audioPlayer;
    // project name
    private TextField projectNameField;
    
    // transport controls
    
    
    // time signature
    private Label timeSignatureLabel;
    private Label bPMLabel;

    private ThemeManager tM;
    private ArrayList<Button> buttons;
    private Project project;


    //rando

    private boolean isDragging=false;
    private double dragStartY;
    private int dragStartBPM;
    
    public TopControlBar(GUIController controller, Project project){
        this.project=project;
        this.controller=controller;
        this.tM=ThemeManager.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        this.setPrefHeight(50);
        this.setStyle("-fx-background-color: "+tM.getDefaultColour()+"; -fx-border-color: "+tM.getLineColourToString()+";");

        this.audioPlayer=controller.getAudioPlayer();
        
    }

    private void styleButtons(){
        for(Button b:buttons){
            Image graphic=new Image(getClass().getResourceAsStream("/images/"+tM.getCurrentTheme()+"_"+b.getText()+".png"));
            ImageView image = new ImageView(graphic); 
            image.setPreserveRatio(true);
            b.setText("");
            b.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            b.setGraphic(image);
            
            b.setMinHeight(50);b.setMaxHeight(50);b.setMinWidth(50);b.setMaxWidth(50);//no reason for this tobe inside this loop
            b.setStyle("-fx-border-color: transparent "+tM.getLineColourToString()+" transparent"+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1;");
        }
    }
    private void styleTextBased(){
        projectNameField.setFont(tM.getFont(14));
        projectNameField.setStyle("-fx-text-fill: " + tM.getDefaultFontColour() + ";-fx-background-color: "+tM.getDefaultColour()+"; -fx-border-color: transparent "+tM.getLineColourToString()+" transparent "+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1; -fx-alignment: center;");
        
        timeSignatureLabel.setFont(tM.getFont(14));
        timeSignatureLabel.setStyle("-fx-text-fill: " + tM.getDefaultFontColour() + ";-fx-background-color: transparent; -fx-border-color: transparent "+tM.getLineColourToString()+" transparent "+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1;");
        
        bPMLabel.setFont(tM.getFont(14));
        bPMLabel.setStyle("-fx-text-fill: " + tM.getDefaultFontColour() + "; -fx-background-color: transparent; -fx-border-color: transparent "+tM.getLineColourToString()+" transparent "+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1;");
        
        addTrackButton.setFont(tM.getFont(14));
        addTrackButton.setStyle("-fx-text-fill: " + tM.getDefaultFontColour() + "; -fx-background-color: "+tM.getDefaultColour()+"; -fx-border-color: transparent "+tM.getLineColourToString()+" transparent "+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1;");
        
        folderButton.setFont(tM.getFont(14));
        folderButton.setStyle("-fx-text-fill: " + tM.getDefaultFontColour() + "; -fx-background-color: "+tM.getDefaultColour()+"; -fx-border-color: transparent "+tM.getLineColourToString()+" transparent "+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1;");
        
        settingsButton.setFont(tM.getFont(14));
        settingsButton.setStyle("-fx-text-fill: " + tM.getDefaultFontColour() + "; -fx-background-color: "+tM.getDefaultColour()+"; -fx-border-color: transparent "+tM.getLineColourToString()+" transparent "+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1;");
        
        themeButton.setFont(tM.getFont(14));
        themeButton.setStyle("-fx-text-fill: " + tM.getDefaultFontColour() + "; -fx-background-color: "+tM.getDefaultColour()+"; -fx-border-color: transparent "+tM.getLineColourToString()+" transparent "+tM.getLineColourToString()+"; -fx-border-width: 0 1 0 1;");
    }
    
    private void initializeComponents(){
        // project control buttons
        folderButton=new Button("folder");
        themeButton=new MenuButton("paintbrush");
        settingsButton=new Button("settings");
        playButton=new Button("play");
        pauseButton=new Button("pause");
        stopButton=new Button("stop");

        addTrackButton=new MenuButton("+");
        buttons=new ArrayList<>();
        buttons.add(playButton);buttons.add(stopButton);buttons.add(pauseButton);

        
        // project name field
        projectNameField=new TextField("untitled project");
        projectNameField.setPrefWidth(200); projectNameField.setPrefHeight(50);
        
        // transport controls (redundant)
        
        
        // time signature
        timeSignatureLabel=new Label("4/4");
     

        bPMLabel=new Label("120");
   
        
        // style the project buttons
        styleButton(folderButton);
        styleMenuButton(themeButton);
        styleButton(settingsButton);
        
        
        
        styleMenuButton(addTrackButton);

        styleAsButton(bPMLabel);styleAsButton(timeSignatureLabel);

        //set up buttons

        setUpAddTrackButton();
        setUpThemeButton();
        setupBPMLabel();
        // style project name field
        styleButtons();
        styleTextBased();
    }
    
    private void setupLayout(){
        // set overall container properties
        this.setAlignment(Pos.CENTER);
        this.setSpacing(0);
        this.setPadding(new Insets(0));
       
        
        
        
        // add all components to the hbox
        this.getChildren().addAll(
            folderButton,
            themeButton, 
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
        
        playButton.setOnAction(e->{
            if(audioPlayer.isPaused()){
                audioPlayer.resume();
            } else {
                audioPlayer.play();
            }
            
            //System.out.println("wtf");
            //System.out.println(controller.getAudioPlayer()==null ? "null" : "not null");
        });
        
        settingsButton.setOnAction(e->{
            System.out.println("settings button clicked");
            // show settings dialog
        });
        
        pauseButton.setOnAction(e->{
            audioPlayer.pause();
        });
        stopButton.setOnAction(e->{
            audioPlayer.stop();
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
                newMenuItem.setOnAction(e->{
                    String displayName=i.toString().replace("_"," ");
                    displayName=displayName.substring(0,1).toUpperCase()+displayName.substring(1).toLowerCase();
                    controller.getContainer().addTrack(displayName,i.getMainCategoryAsType());
                });
                
                String parentCategoryName=i.getMainCategory().toString(); 
                Menu parentMenu=categoryMenus.get(parentCategoryName);
                if(parentMenu!=null){
                    parentMenu.getItems().add(newMenuItem);
                }
            }
        }
    }
    private void setUpThemeButton(){
        ArrayList<String> themes=ThemeManager.getInstance().getThemes();
        for(String i : themes){
            MenuItem menuItem=new MenuItem(i);
            menuItem.setOnAction(e->ThemeManager.getInstance().setTheme(i));
            themeButton.getItems().add(menuItem);
        }
    }
    private void setupBPMLabel(){
        updateBPMDisplay();
        
        // mouse pressed - start drag tracking
        bPMLabel.setOnMousePressed(e->{
            isDragging=true;
            dragStartY=e.getSceneY();
            dragStartBPM=project.getBPM();
        });
        
        // mouse dragged - update bpm based on vertical movement
        bPMLabel.setOnMouseDragged(e->{
            if(!isDragging)return;
            
            double deltaY=dragStartY-e.getSceneY(); // up is positive
            int bpmChange=(int)(deltaY/3); // 3 pixels = 1 bpm change
            int newBPM=Math.max(20,Math.min(300,dragStartBPM+bpmChange));
            
            project.setBPM(newBPM);
            updateBPMDisplay();
        });
        
        // mouse released - stop dragging
        bPMLabel.setOnMouseReleased(e->{
            isDragging=false;
        });
        
        // double click - text input
        bPMLabel.setOnMouseClicked(e->{
            if(e.getClickCount()==2){
                showBPMTextInput();
            }
        });
        
        // visual feedback for interactivity
        bPMLabel.setStyle("-fx-cursor: hand;");
    }

    private void updateBPMDisplay(){
        bPMLabel.setText(project.getBPM()+"");
    }

    private void showBPMTextInput(){
        TextField textField=new TextField(String.valueOf(project.getBPM()));
        textField.setPrefWidth(bPMLabel.getWidth());
        textField.selectAll();
        
        // replace label with text field temporarily
        Parent parent=bPMLabel.getParent();
        if(parent instanceof Pane){
            Pane pane=(Pane)parent;
            int index=pane.getChildren().indexOf(bPMLabel);
            pane.getChildren().set(index,textField);
            textField.requestFocus();
            
            // enter or focus lost - apply changes
            Runnable applyChanges=()->{
                try{
                    int newBPM=Integer.parseInt(textField.getText().trim());
                    if(newBPM>=20&&newBPM<=300){
                        project.setBPM(newBPM);
                    }
                }catch(NumberFormatException ignored){}
                
                updateBPMDisplay();
                pane.getChildren().set(index,bPMLabel);
            };
            
            textField.setOnAction(e->applyChanges.run());
            textField.focusedProperty().addListener((obs,oldVal,newVal)->{
                if(!newVal)applyChanges.run();
            });
        }
    }

    private void styleButton(Button button){
        button.setMinWidth(50); button.setMaxWidth(50);
        button.setMinHeight(50); button.setMaxHeight(50);
        button.setPrefHeight(50);button.setPrefWidth(50);
    }

    private void styleMenuButton(MenuButton button){
        button.setMinWidth(50); button.setMaxWidth(50);
        button.setMinHeight(50); button.setMaxHeight(50);
        button.setPrefHeight(50);button.setPrefWidth(50);
    }

    private void styleAsButton(Label label){
        label.setMinHeight(50);
        label.setMinWidth(50);
        label.setPrefHeight(50);label.setPrefWidth(50);
        label.setAlignment(Pos.CENTER);
    }

   
    
    
    // getter for the controller to add this to layout
    public HBox getNode(){
        return this;
    }

    public Button getPlayButton(){return playButton;}
    
  
    // method to get project name
    public String getProjectName(){
        return projectNameField.getText();
    }
    
    // method to set project name
    public void setProjectName(String name){
        projectNameField.setText(name);
    }
}