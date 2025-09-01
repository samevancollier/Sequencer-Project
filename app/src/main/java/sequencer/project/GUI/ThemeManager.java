package sequencer.project.GUI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ThemeManager {
    private static volatile ThemeManager instance;
    private Scene scene;
    private String currentTheme;
    private final Map<String, JsonNode> themes;
    private final ObjectMapper mapper;
    private TrackContainer trackContainer;
    private GUIController controller;
    
    public static ThemeManager getInstance(){
        if(instance==null){
            synchronized(ThemeManager.class){
                if(instance==null){
                    instance=new ThemeManager();
                }
            }
        }
        return instance;
    }
    
    public ThemeManager(){
        this.themes=new HashMap<>();
        this.mapper=new ObjectMapper();
        initializeThemes();
        currentTheme="BIOS";
    }
    
    private void initializeThemes(){
        loadTheme("BIOS");
        
    }
    
    private void loadTheme(String themeName){
        try{
            String jsonPath="/themes/"+themeName+".json";
            InputStream inputStream=getClass().getResourceAsStream(jsonPath);
            if(inputStream==null){
                System.err.println("theme file not found: "+jsonPath);
                return;
            }
            JsonNode themeData=mapper.readTree(inputStream);
            themes.put(themeName,themeData);
        }catch(Exception e){
            System.err.println("failed to load theme: "+themeName+" - "+e.getMessage());
        }
    }
    
    public void setTrackContainer(TrackContainer trackContainer){this.trackContainer=trackContainer;}
    public void setController(GUIController controller){this.controller=controller;}
    public void setScene(Scene scene){this.scene=scene;}
    
    public void setTheme(String themeName){
        if(!themes.containsKey(themeName)){
            System.err.println("theme not found: "+themeName);
            return;
        }
        
        try{
            currentTheme=themeName;
            controller.getBackground().changeBackground(themeName);
        }catch(Exception e){
            System.err.println("could not load theme: "+themeName+" - "+e.getMessage());
        }
    }
    
    public ArrayList<String> getThemes(){
        return new ArrayList<String>(themes.keySet());
    }
    
    private JsonNode getCurrentThemeData(){
        return themes.get(currentTheme);
    }
    
    // font
    public javafx.scene.text.Font getFont(double size){
        try{
            JsonNode theme=getCurrentThemeData();
            if(theme==null || !theme.has("font")) return null;
            
            InputStream fontStream=getClass().getResourceAsStream("/fonts/"+theme.get("font").asText());
            if(fontStream==null) return null;
            
            return javafx.scene.text.Font.loadFont(fontStream, size);
        }catch(Exception e){
            System.err.println("failed to load font: "+e.getMessage());
            return null;
        }
    }
    public String getCurrentTheme(){return currentTheme;}
    // image getters
    public String getPlayButton(){return getImage("playButton");}
    public String getPauseButton(){return getImage("pauseButton");}
    public String getStopButton(){return getImage("stopButton");}
    public String getFolderButton(){return getImage("folderButton");}
    public String getPaintbrushButton(){return getImage("paintbrushButton");}
    public String getMuteButton(){return getImage("muteButton");}
    public String getSoloButton(){return getImage("soloButton");}
    public String getSettingsButton(){return getImage("settingsButton");}
    public String getKeyboardSprite(){return getImage("keyboardSprite");}
    public String getDrumsSprite(){return getImage("drumsSprite");}
    
    private String getImage(String imageName){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("images")){
            String filename=theme.get("images").get(imageName).asText();
            // remove .png extension and add theme prefix
            String baseName=filename.substring(0,filename.lastIndexOf('.'));
            String extension=filename.substring(filename.lastIndexOf('.'));
            return "resources/images/"+currentTheme+"_"+baseName+extension;
        }
        return null;
    }
    
    // background images
    public String getCurrentBackgroundImagePath(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("backgroundImage")){
            String filename=theme.get("backgroundImage").asText();
            String baseName=filename.substring(0,filename.lastIndexOf('.'));
            String resourcePath="/backgrounds/"+currentTheme+"_"+baseName+".png";  // removed "resources/"
            System.out.println("Looking for: "+resourcePath);
            java.net.URL resource=getClass().getResource(resourcePath);
            if(resource==null){
                System.err.println("Resource not found: "+resourcePath);
                return null;
            }
            return resource.toExternalForm();
        }
        return null;
    }
    
    public String getPianoRollBackground13(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("pianoRollBackgrounds")){
            String filename=theme.get("pianoRollBackgrounds").get("bars13").asText();
            String baseName=filename.substring(0,filename.lastIndexOf('.'));
            String extension=filename.substring(filename.lastIndexOf('.'));
            return "resources/images/"+currentTheme+"_"+baseName+extension;
        }
        return null;
    }
    
    public String getPianoRollBackground24(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("pianoRollBackgrounds")){
            String filename=theme.get("pianoRollBackgrounds").get("bars24").asText();
            String baseName=filename.substring(0,filename.lastIndexOf('.'));
            String extension=filename.substring(filename.lastIndexOf('.'));
            return "resources/images/"+currentTheme+"_"+baseName+extension;
        }
        return null;
    }
    
    // track colors
    public Color getTrackColourBase(int index){return getTrackColour(index,"base");}
    public Color getTrackColourHighlight(int index){return getTrackColour(index,"highlight");}
    public Color getTrackColourSelected(int index){return getTrackColour(index,"selected");}
    public Color getHeaderColour(int index){return getTrackColour(index,"header");}
    public Color getEmptyColour(int index){return getTrackColour(index,"empty");}
    public Color getVolumeFill(int index){return getTrackColour(index,"volumefill");}

    public String getTrackColourBaseToString(int index){return getTrackColour(index,"base").toString().replace("0x", "#");}
    public String getTrackColourHighlightToString(int index){return getTrackColour(index,"highlight").toString().replace("0x", "#");}
    public String getTrackColourSelectedToString(int index){return getTrackColour(index,"selected").toString().replace("0x", "#");}
    public String getHeaderColourToString(int index){return getTrackColour(index,"header").toString().replace("0x", "#");}
    public String getEmptyColourToString(int index){return getTrackColour(index,"empty").toString().replace("0x", "#");}
    public String getLineColourToString(){return getLineColour().toString().replace("0x", "#");} 
    
    public String getDefaultFontColour(){
        JsonNode theme=getCurrentThemeData();
        return theme.get("colors").get("defaultfontcolour").asText();
    }

    
    public Color getOneAndThreeBG(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("oneandthreebackground").asText());
    }
    public Color getTwoAndFourBG(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("twoandfourbackground").asText());
    }
    public Color getSharpNoteFill(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("sharpnotebackgroundfill").asText());
    }
    public Color getWhiteNoteDivider(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("whitenotedividingline").asText());
    }
    public Color getHeaviestPianoRollLine(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("heaviestpianorollline").asText());
    }
    public Color getHeavyPianoRollLine(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("heavypianorollline").asText());
    }
    public Color getLightPianoRollLine(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("lightpianorollline").asText());
    }
    public Color getLightestPianoRollLine(){
        JsonNode theme=getCurrentThemeData();
        return Color.web(theme.get("colors").get("lightestpianorollline").asText());
    }
    private Color getTrackColour(int index,String variant){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("trackColors")){
            JsonNode trackColors=theme.get("trackColors");
            if(index<trackColors.size()){
                String colorString=trackColors.get(index).get(variant).asText();
                return Color.web(colorString);
            }
        }
        return null;
    }
    
    public int getTrackColourCount(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("trackColors")){
            return theme.get("trackColors").size();
        }
        return 0;
    }
    
    // other colors
    public String getTrackLineColor(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("colors")){
            return theme.get("colors").get("trackLines").asText();
        }
        return null;
    }

    public String getDefaultColour(){
        JsonNode theme=getCurrentThemeData();
        return theme.get("colors").get("default").asText();
    }

    public Color getLineColour(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("colors")){
            return Color.web(theme.get("colors").get("lines").asText());
        }
        return null;
    }
    
    public String getNotesBase(){return getNoteColor("base");}
    public String getNotesHighlight(){return getNoteColor("highlight");}
    public String getNotesSelected(){return getNoteColor("selected");}
    
    private String getNoteColor(String variant){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("colors") && theme.get("colors").has("notes")){
            return theme.get("colors").get("notes").get(variant).asText();
        }
        return null;
    }
    
    public String getSelectedNotesColor(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("colors")){
            return theme.get("colors").get("selectedNotes").asText();
        }
        return null;
    }
    
    // opacity
    public double getDefaultOpacity(){
        JsonNode theme=getCurrentThemeData();
        if(theme!=null && theme.has("defaultOpacity")){
            return theme.get("defaultOpacity").asDouble();
        }
        return 1.0;
    }
}