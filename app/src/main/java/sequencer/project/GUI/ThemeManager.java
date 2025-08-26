package sequencer.project.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import sequencer.project.audio.MusicRoom;

public class ThemeManager {

    private static volatile ThemeManager instance;

    private Scene scene;
    private String currentTheme;
    private final Map<String, ThemeConfig> themes;
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

    
    
    public ThemeManager() {
        
       
        this.themes = new HashMap<>();
        initializeThemes();
        currentTheme="sonic";
    }
    
    private void initializeThemes() { //make it do this automatixally
        themes.put("sonic", new ThemeConfig("sonic.css"));
        themes.put("terraria", new ThemeConfig("terraria.css"));
        themes.put("ascii", new ThemeConfig("ascii.css"));
       
    }
    public void setTrackContainer(TrackContainer trackContainer) {
        this.trackContainer = trackContainer;
    }
    
    public void setTheme(String themeName) {
        ThemeConfig config = themes.get(themeName);
        if (config == null) {
            System.err.println("Theme not found: " + themeName);
            return;
        }
        
        try {
            // Load CSS
            String cssPath = "/themes/" + config.cssFile;
            System.out.println("Looking for CSS at: " + cssPath);
            System.out.println("CSS resource exists: " + (getClass().getResource(cssPath) != null));
            
            // Load CSS
            scene.getStylesheets().clear();
            String cssUrl = getClass().getResource(cssPath).toExternalForm();
            System.out.println("CSS URL: " + cssUrl);
            scene.getStylesheets().add(cssUrl);
            
            // DEBUG - verify it was added
            System.out.println("Scene stylesheets after adding: " + scene.getStylesheets());
            
            currentTheme = themeName;
            controller.getBackground().changeBackground(themeName);
        } catch (Exception e) {
            System.err.println("could not load theme: " + themeName + " - " + e.getMessage());
        }
    }
    
    public String getCurrentBackgroundImagePath() {
        ThemeConfig config = themes.get(currentTheme);
        return config != null ? config.getBackgroundImagePath() : null;
    }

    public ArrayList<String> getThemes(){
        ArrayList<String> themeList=new ArrayList<String>(themes.keySet());
        return themeList;
    }

    

    public void setController(GUIController controoler){this.controller=controoler;}

    public void setScene(Scene scene){this.scene=scene;}


    private static class ThemeConfig {
        final String cssFile;
        final String backgroundImagePath; //why
        
        ThemeConfig(String cssFile) {
            this.cssFile=cssFile;
            this.backgroundImagePath=getBackgroundImagePath();
        }
        public String getBackgroundImagePath() {
            
            return "/backgrounds/" + getThemeName() + "_background.png";
        }
        public String getThemeName() {
            return cssFile.substring(0, cssFile.lastIndexOf('.'));
        }
    }
}