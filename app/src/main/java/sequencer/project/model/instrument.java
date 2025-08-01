package sequencer.project.model;

import javax.sound.sampled.*;

import sequencer.project.audio.MusicRoom;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//I SHOULD MAKE INSTRUMENT AN EXTENDABLE CLASS FOR DRUMS, PROBABLY

public class Instrument {
    private String name;
    private String folderPath;
    private InstrumentType instrumentType;

    private Map<Integer, byte[]> samples = new HashMap<>(); // map pitches to samples

    private Map<Integer, Integer> loopStartPoints = new HashMap<>(); // byte position where loop starts
    private Map<Integer, Integer> loopEndPoints = new HashMap<>();   // byte position where loop ends
    
    public Instrument(String name, InstrumentType instrumentType){
        this.name = name;
        this.folderPath = "app/src/main/resources/samples/" + name.replace(" ", "");
        this.instrumentType = instrumentType;
        loadSamples();
        if(instrumentType.getMainCategoryAsType()!=InstrumentType.DRUMS){ //CHANGED, hERE
            setDefaultLoopPoints();
        }
    }
    private void setDefaultLoopPoints(){    //ADD SOMETHING TO SET LOOP POINts that dont click
        for (Map.Entry<Integer, byte[]> entry : samples.entrySet()) { 
            Integer pitch = entry.getKey();    // key
            byte[] data = entry.getValue();    // value (audio bytes)
            int defaultLoopStart = alignToSampleBoundary((int) (data.length * 0.25));
            int defaultLoopEnd = alignToSampleBoundary((int) (data.length * 0.90));
            
            // Make sure loop points are on 16-bit sample boundaries (even numbers)
            defaultLoopStart = (defaultLoopStart / 2) * 2;
            defaultLoopEnd = (defaultLoopEnd / 2) * 2;
            
            loopStartPoints.put(pitch, defaultLoopStart);
            loopEndPoints.put(pitch, defaultLoopEnd);
        }
    }
    // Helper method to align to 4-byte boundaries (stereo 16-bit = 4 bytes per frame)
    private int alignToSampleBoundary(int position) {
        return (position / 4) * 4;
    }
    //loads samples
    private void loadSamples() {
        File folder = new File(folderPath);
        if(!folder.exists() || !folder.isDirectory()) {
            System.err.println("not found...");
            return;
        }
        File[] files = folder.listFiles();
        for(File file : files){
            try{
                System.out.println(file.getName()); //debug
                int pitch = pitchFromFilename(file.getName());
                byte[] sample = loadWav(file);
                samples.put(pitch, sample);
                
            } catch(Exception e) {
                System.err.println("failed to load sample");
                e.printStackTrace();
            }
        }
        // stuff here obviously...
    }
    //get pitch from the filename
    private int pitchFromFilename(String filename){
        String noteString = filename.substring(0, 2);
        int octave = Character.getNumericValue(noteString.charAt(0));
        char note = noteString.charAt(1); 
        int noteValue = getNoteValue(note);
        return (octave + 1) * 12 + noteValue;
    }
    //load an audio file  
    private byte[] loadWav(File file){ 
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            byte[] sample = stream.readAllBytes();
            stream.close();
            // DEBUG: Print the actual format
            AudioFormat actualFormat = stream.getFormat();
            System.out.println("Sample format for " + file.getAbsolutePath() + ": " + actualFormat);
            return sample;
        } catch (Exception e) {
            System.err.println("no...");
            return null;
        }
    }
    
    //getters
    public String getName(){
        return name;
    }
    public byte[] getSample(int pitch){
        return samples.get(pitch);
    }
    public InstrumentType getInstrumentType(){
        return instrumentType;
    }

    public byte[] getSampleData(int pitch) {
        return samples.get(pitch);
    }

    public int getLoopStart(int pitch) {
        return loopStartPoints.getOrDefault(pitch, 0);
    }
    
    // Get loop end point for a pitch (in bytes)  
    public int getLoopEnd(int pitch) {
        byte[] data = samples.get(pitch);
        if (data == null) return 0;
        return loopEndPoints.getOrDefault(pitch, data.length);
    }

    public boolean hasLoopPoints(int pitch) {
        return loopStartPoints.containsKey(pitch) && loopEndPoints.containsKey(pitch);
    }
    //much more stuff to go here probably
    // need a dispose class probably
    //associate letters with notes
    private int getNoteValue(char note) {
        switch (note) {
            case 'c': return 0;   // C
            case 'C': return 1;   // C#
            case 'd': return 2;   // D  
            case 'D': return 3;   // D#
            case 'e': return 4;   // E
            case 'f': return 5;   // F
            case 'F': return 6;   // F#
            case 'g': return 7;   // G
            case 'G': return 8;   // G#
            case 'a': return 9;   // A
            case 'A': return 10;  // A#
            case 'b': return 11;  // B
            default: return -1;
        }
    }
}
