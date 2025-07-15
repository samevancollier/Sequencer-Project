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
    private Map<Integer, Clip> samples; //map pitches to samples
    
    public Instrument(String name, String folderPath, InstrumentType instrumentType){
        this.name = name;
        this.folderPath = folderPath;
        this.instrumentType = instrumentType;
        if(this.instrumentType == InstrumentType.DRUMS){
            this.samples = new HashMap<>();
            loadSamples();
        } else {
            //handle non-drum instruments
        }
        
    
    }

    public void playNote(Note note){        //THIS ONLY WORKS FOR DRUMS RN //move to audiomanager
        Clip clip = samples.get(note.getPitch());
        if (clip == null) return;
        clip.setFramePosition(0);
        clip.start();
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
                Clip clip = loadWav(file);
                samples.put(pitch, clip);
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
    //load an audio file into a clip
    private Clip loadWav(File file){ //REMEMBER THE LIMITATIONS OF CLIP, THIS IS NOT GOOD ENOUGH, ONLY FOR DRUMS...
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            return clip;
        } catch (Exception e) {
            System.err.println("no...");
            return null;
        }
    }
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
    //getters
    public String getName(){
        return name;
    }
    public Clip getSample(int pitch){
        return samples.get(pitch);
    }
    public InstrumentType getinstrumentType(){
        return instrumentType;
    }
    //much more stuff to go here probably
    // need a dispose class probably
}
