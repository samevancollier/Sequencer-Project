package sequencer.project.GUI;

import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import sequencer.project.model.AudioEffect;
import sequencer.project.model.Bitcrush;
import sequencer.project.model.FX;
import sequencer.project.model.Track;

public class FXArea extends GridPane{
    private Button fXSlotOne,fXSlotTwo,fXSlotThree,fXSlotFour;
    private Track track;
    private AudioEffect[] assignedEffects=new AudioEffect[4]; // track assigned effects
    
    public FXArea(TrackRow trackRow){
        this.track=trackRow.getTrack();
        ThemeManager tm=ThemeManager.getInstance();
        fXSlotOne=new Button("+");fXSlotTwo=new Button("+");fXSlotThree=new Button("+");fXSlotFour=new Button("+");
        styleButton(fXSlotOne);styleButton(fXSlotTwo);styleButton(fXSlotThree);styleButton(fXSlotFour);
        setStyle("-fx-border-color:"+tm.getLineColourToString()+"; -fx-border-width: 1; -fx-padding: 0");
        setAlignment(Pos.CENTER);
       
        // set up button actions
        fXSlotOne.setOnAction(e->showEffectMenu(0,fXSlotOne));
        fXSlotTwo.setOnAction(e->showEffectMenu(1,fXSlotTwo));
        fXSlotThree.setOnAction(e->showEffectMenu(2,fXSlotThree));
        fXSlotFour.setOnAction(e->showEffectMenu(3,fXSlotFour));

        fXSlotOne.setOnMouseClicked(e->{if(e.getClickCount()==2&&assignedEffects[0]!=null)assignedEffects[0].launchWindow();});
        fXSlotTwo.setOnMouseClicked(e->{if(e.getClickCount()==2&&assignedEffects[1]!=null)assignedEffects[1].launchWindow();});
        fXSlotThree.setOnMouseClicked(e->{if(e.getClickCount()==2&&assignedEffects[2]!=null)assignedEffects[2].launchWindow();});
        fXSlotFour.setOnMouseClicked(e->{if(e.getClickCount()==2&&assignedEffects[3]!=null)assignedEffects[3].launchWindow();});
       
       
        // arrange in 2x2 grid: (column, row)
        add(fXSlotOne, 0, 0);   // top left
        add(fXSlotTwo, 1, 0);   // top right  
        add(fXSlotThree, 0, 1); // bottom left
        add(fXSlotFour, 1, 1);  // bottom right
       
        // remove gaps between buttons
        setHgap(0);
        setVgap(0);
    }
   
    private void showEffectMenu(int slotIndex,Button button){
        ContextMenu menu=new ContextMenu();
        
        // add menu item for each effect
        for(FX effect : FX.values()){
            MenuItem item=new MenuItem(effect.name());
            item.setOnAction(e->{
                AudioEffect audioEffect=createEffect(effect);
                assignedEffects[slotIndex]=audioEffect;
                button.setText(effect.name());

                
                track.addFX(audioEffect,slotIndex);
            });
            menu.getItems().add(item);
        }
        
        // add clear option if effect is already assigned
        if(assignedEffects[slotIndex]!=null){
            menu.getItems().add(new SeparatorMenuItem());
            MenuItem clearItem=new MenuItem("Clear");
            clearItem.setOnAction(e->{
                assignedEffects[slotIndex]=null;
                button.setText("+");
                track.removeFX(slotIndex);
            });
            menu.getItems().add(clearItem);
        }
        
        menu.show(button,button.localToScreen(0,0).getX(),button.localToScreen(0,0).getY()+button.getHeight());
    }

    private AudioEffect createEffect(FX effect){
        switch(effect){
            case Bitcrush:
                return new Bitcrush();
            default:
                return null;
        }
    }
   
    private void styleButton(Button button){
        ThemeManager tm=ThemeManager.getInstance();
        button.setMinWidth(50);button.setMaxWidth(50);
        button.setMinHeight(50);button.setMaxHeight(50);
        button.setPrefHeight(50);button.setPrefWidth(50);
        button.setFont(tm.getFont(16));
        button.setStyle("-fx-background-color: "+tm.getDefaultColour()+"; -fx-text-fill: white; -fx-border-color: "+tm.getLineColourToString()+"; -fx-border-radius: 0; -fx-background-radius: 0; -fx-padding:0");
    }
    
    public AudioEffect getAssignedEffect(int slotIndex){return assignedEffects[slotIndex];}
}