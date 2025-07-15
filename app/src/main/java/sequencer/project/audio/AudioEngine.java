package sequencer.project.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import sequencer.project.model.*;

public class AudioEngine { //probably doesn't need to be an interface anymore...
    private AudioFormat audioFormat; //specifies a particular arrangement of data in a sound stream
    private SourceDataLine dataLine; //data line to which data may be written
    private boolean isInitialised = false; //idk
    private volatile boolean running = true; //???
    //active samples being mixed
    private List<ActiveSample> activeSamples = new ArrayList<>();
    private final Object samplesLock = new Object(); //idfk
    //audio buffer for mixing
    private static final int BUFFER_SIZE = 1024;
    private byte[] mixBuffer = new byte[BUFFER_SIZE];
    private Thread audioThread;
    
    private AtomicInteger nextSampleId = new AtomicInteger(0);
    
    
    private final Object lineLock = new Object(); //i honestly dont understand what this is really
    
    //separate thread for audio writing to avoid blocking the sequencer
    private BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();
    private Thread audioWritingThread;
    
    //CONSTRUCTOR
    public AudioEngine(){
        initializeAudio();
        startAudioThread();
    }
    private void initializeAudio(){
        try {
            audioFormat = new AudioFormat(44100.0f, 16, 2, true, false); //CD quality...
            dataLine = AudioSystem.getSourceDataLine(audioFormat);
            dataLine.open(audioFormat, 8192); // 8KB buffer
            dataLine.start();   //truthfully idfk what this is doing
            isInitialised = true;
            System.out.println("Audio initialized: " + audioFormat)
        } catch (Exception e) {
            System.err.println("could not init audio!");
            e.printStackTrace();
        }
    }

    private void startAudioThread(){ //ugly, weird and i dont really know what i did here
        audioWritingThread = new Thread(()->{
            while(running){
                try {
                    byte[] audioByte = audioQueue.take();
                    if(audioByte!=null && isInitialised){
                        synchronized(lineLock){
                            dataLine.write(audioByte, 0, audioByte.length);
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        audioWritingThread.setDaemon(true); //IDK
        audioWritingThread.start();
    }

    public void playNote(Instrument instrument, Note note){
        if(!isInitialised){
            System.err.println("audio not init");
            return;
        }
        byte[] sampleData = instrument.getSample(note.getPitch());
        byte[] formattedSampleData = convertToOutputFormat(sampleData);
        audioQueue.offer(formattedSampleData);
    };

    private byte[] convertToOutputFormat(byte[] inputData){ //UHHHHHHHHHHHHH
    try {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
            new ByteArrayInputStream(inputData));
        
        // check if conversion is needed
        if (inputStream.getFormat().matches(audioFormat)) {
            inputStream.close();
            return inputData; // No conversion needed
        }
        
        // convert
        AudioInputStream convertedStream = AudioSystem.getAudioInputStream(audioFormat, inputStream);
        byte[] result = convertedStream.readAllBytes();
        
        inputStream.close();
        convertedStream.close();
        return result;
        
    } catch (Exception e) {
        System.err.println("Format conversion failed: " + e.getMessage());
        return inputData; // return original and hope
    }
}
    void stopNote(){};  //Bruh
}
