package sequencer.project.audio;
import sequencer.project.model.Instrument;
import sequencer.project.model.InstrumentType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MusicRoom {

    private static volatile MusicRoom instance;
    
    public static MusicRoom getInstance(){
        if(instance==null){
            synchronized(MusicRoom.class){
                if(instance==null){
                    instance=new MusicRoom();
                }
            }
        }
        return instance;
    }
    
    private Map<String, Instrument> instruments;
    private String basicPath = "app/src/main/resources/samples/";

    public MusicRoom(){
        this.instruments = new HashMap<>();
        instruments.put("Teenage Drums", new Instrument("Teenage Drums", InstrumentType.TEENAGE_DRUMS));
        instruments.put("Square", new Instrument("Square", InstrumentType.SQUARE));
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
