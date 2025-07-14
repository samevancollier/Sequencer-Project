package sequencer.project.audio;
import sequencer.project.model.instrument;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class musicroom {
    private Map<String, instrument> instruments;
    private String basicPath = "app/src/main/resources/samples/";

    public musicroom(){
        this.instruments = new HashMap<>();
        instruments.put("Teenage Drums", new instrument("Teenage Drums", basicPath + "TeenageDrums", "Drums"));
    }

    public instrument getInstrument(String instrumentName){
        try {
            return instruments.get(instrumentName);
        } catch (Exception e) {
            System.out.println("can't find it mate");
            return null;
        }
    }
}
