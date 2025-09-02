package sequencer.project.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum InstrumentType {
    // main categories 
    DRUMS("DRUMS", "All drum instruments", true),
    SYNTH("SYNTH", "All synthesizers", true),
    
    // drum subcategories
    TEENAGE_DRUMS("DRUMS"),
    
    // synth subcategories
    SQUARE("SYNTH"),
    BZZ("SYNTH");
    
    
    private final String mainCategory;
    private final String description;
    private final boolean isMainCategory;
    
    
    InstrumentType(String mainCategory, String description, boolean isMainCategory) {
        this.mainCategory=mainCategory;
        this.description=description;
        this.isMainCategory=isMainCategory;
        
    }
    InstrumentType(String mainCategory, String description){
        this.mainCategory=mainCategory;
        this.description=description;
        this.isMainCategory=false;
        
    }
    InstrumentType(String mainCategory){
        this.mainCategory=mainCategory;
        this.description=null;
        this.isMainCategory=false;
        
    }
    public String getMainCategory() {return mainCategory;}
    public InstrumentType getMainCategoryAsType() {return InstrumentType.valueOf(mainCategory);} //retarded honestly
    public String getDescription() {return description;}
    
    public boolean isMainCategory() {return isMainCategory;}
    
    public boolean isDrum() {return "DRUMS".equals(mainCategory);}
    
    public boolean isSynth() {return "SYNTH".equals(mainCategory);}
    
    // get all subcategories for a main category
    
    
    
}