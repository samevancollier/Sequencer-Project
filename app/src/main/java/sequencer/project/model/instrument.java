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

                // get audio format info
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioStream.getFormat();
                
                System.out.println("Sample rate: " + format.getSampleRate());
                System.out.println("Bit depth: " + format.getSampleSizeInBits());
                System.out.println("Channels: " + format.getChannels());
                System.out.println("Encoding: " + format.getEncoding());
                System.out.println("Frame size: " + format.getFrameSize());
                System.out.println("---");
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
        int octave=Character.getNumericValue(filename.charAt(0));
        String noteString=filename.substring(1,3); // get characters 1-2 (note part)
        int noteValue=getNoteValue(noteString);
        return (octave+1)*12+noteValue;
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
    
    private int getNoteValue(String noteString){
        switch(noteString){
            case "cc": return 0;   // c
            case "cs": return 1;   // c#
            case "dd": return 2;   // d  
            case "ds": return 3;   // d#
            case "ee": return 4;   // e
            case "ff": return 5;   // f
            case "fs": return 6;   // f#
            case "gg": return 7;   // g
            case "gs": return 8;   // g#
            case "aa": return 9;   // a
            case "as": return 10;  // a#
            case "bb": return 11;  // b
            default: return -1;
        }
    }
}
