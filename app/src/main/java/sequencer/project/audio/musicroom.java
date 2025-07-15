package sequencer.project.audio;
import sequencer.project.model.Instrument;
import sequencer.project.model.InstrumentType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MusicRoom {
    private Map<String, Instrument> instruments;
    private String basicPath = "app/src/main/resources/samples/";

    public MusicRoom(){
        this.instruments = new HashMap<>();
        instruments.put("Teenage Drums", new Instrument("Teenage Drums", basicPath + "TeenageDrums", InstrumentType.DRUMS));
        instruments.put("Square", new Instrument("Square", basicPath + "Square", InstrumentType.SYNTH));
    }

    public Instrument getInstrument(String instrumentName){
        try {
            return instruments.get(instrumentName);
        } catch (Exception e) {
            System.out.println("can't find it mate");
            return null;
        }
    }
}
