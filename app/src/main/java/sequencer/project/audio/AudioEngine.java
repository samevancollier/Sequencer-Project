package sequencer.project.audio;
import javax.sound.sampled.*;
import sequencer.project.model.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class AudioEngine {
    // The main audio output line - where all mixed audio goes
    private SourceDataLine line;
    // Audio format specification (sample rate, bit depth, channels, etc.)
    private AudioFormat format;
    // Flag to track if the audio system was set up successfully
    private boolean isInitialized = false;
    // Flag to control the main audio loop - volatile because multiple threads access it
    private volatile boolean running = true;
    
    // List of all samples currently playing - these get mixed together each frame
    private List<ActiveSample> activeSamples = new ArrayList<>();
    // Lock object to prevent race conditions when multiple threads modify activeSamples
    private final Object samplesLock = new Object();
    
    // Size of audio buffer in bytes - larger = more latency but smoother playback
    private static final int BUFFER_SIZE = 1024;
    // The buffer where we mix all samples together before sending to output
    private byte[] mixBuffer = new byte[BUFFER_SIZE];
    // The background thread that continuously mixes and outputs audio
    private Thread audioThread;
    
    // Counter to assign unique IDs to samples - thread-safe because multiple threads might play notes
    private AtomicInteger nextSampleId = new AtomicInteger(0);
    
    // Constructor - called when you create a new MixingAudioEngine
    public AudioEngine() {
        // Set up the audio system (speakers, format, etc.)
        initializeAudio();
        // Start the background thread that mixes and plays audio
        startAudioThread();
    }
    
    // Set up the audio system - configure speakers and audio format
    private void initializeAudio() {
        try {
            // Define audio format: 44.1kHz sample rate, 16-bit depth, stereo, signed, little-endian
            format = new AudioFormat(44100.0f, 16, 2, true, false);
            // Ask the system for an audio output line that can handle our format
            line = AudioSystem.getSourceDataLine(format);
            // Open the line with our format and a buffer 4x our mixing buffer size
            line.open(format, BUFFER_SIZE * 4);
            // Start the line - now it's ready to receive audio data
            line.start();
            // Mark that everything worked
            isInitialized = true;
            // Print success message to console
            System.out.println("Mixing audio engine initialized: " + format);
        } catch (LineUnavailableException e) {
            // If audio setup failed, print error message
            System.err.println("Could not initialize audio: " + e.getMessage());
            // Print full error details for debugging
            e.printStackTrace();
        }
    }
    
    // Create and start the background audio thread
    private void startAudioThread() {
        // Create a new thread that will run the audioLoop method
        audioThread = new Thread(this::audioLoop);
        // Make it a daemon thread so it doesn't prevent the program from exiting
        audioThread.setDaemon(true);
        // Give it maximum priority for smooth audio (important for real-time audio)
        audioThread.setPriority(Thread.MAX_PRIORITY);
        // Start the thread running
        audioThread.start();
    }
    
    // runs continuously in background
    private void audioLoop() {
        // Keep running until shutdown
        while (running) {
            // Clear the mix buffer - set all bytes to 0 (silence)
            for (int i = 0; i < mixBuffer.length; i++) {
                mixBuffer[i] = 0;
            }
            
            // Process all currently playing samples
            synchronized (samplesLock) { // Lock to prevent other threads from modifying the list
                // Get an iterator to safely remove finished samples while looping
                Iterator<ActiveSample> iterator = activeSamples.iterator();
                // Loop through each sample that's currently playing
                while (iterator.hasNext()) {
                    // Get the next sample
                    ActiveSample sample = iterator.next();
                    
                    // Check if this sample has finished playing
                    if (sample.isFinished()) {
                        // Remove it from the list - it's done
                        iterator.remove();
                        // Skip to next sample
                        continue;
                    }
                    
                    // Mix this sample's audio data into our output buffer
                    mixSampleIntoBuffer(sample, mixBuffer);
                }
            } // End of synchronized block - release the lock
            
            // Send the mixed audio to the speakers (only if audio system is working)
            if (isInitialized) {
                line.write(mixBuffer, 0, mixBuffer.length);
            }
            
            // Small pause to prevent this loop from using 100% CPU
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // If interrupted, exit the loop
                break;
            }
        }
    }
    
    // Take one sample and add its audio data to the mix buffer
    private void mixSampleIntoBuffer(ActiveSample sample, byte[] buffer) {
        // Get the raw audio data from this sample
        byte[] sampleData = sample.getData();
        // Get current playback position within this sample
        int samplePos = sample.getPosition();
        // Calculate how many bytes we can mix (don't go past end of sample or buffer)
        int samplesToMix = Math.min(buffer.length, sampleData.length - samplePos);
        float volumeMultiplier = sample.getVolumeMultiplier();
        // Mix the audio data - we process 2 bytes at a time (16-bit audio = 2 bytes per sample)
        for (int i = 0; i < samplesToMix; i += 2) {
            // Make sure we don't read past the end of the sample data
            if (samplePos + i + 1 < sampleData.length) {
                // Convert 2 bytes from buffer to a 16-bit signed integer
                // Byte 1: low byte, Byte 2: high byte (little-endian format)
                short bufferSample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
                // Convert 2 bytes from sample to a 16-bit signed integer
                short sampleValue = (short) ((sampleData[samplePos + i + 1] << 8) | (sampleData[samplePos + i] & 0xFF));
                // Apply volume scaling to the sample
                sampleValue = (short) (sampleValue * volumeMultiplier);
                // Add the two audio signals together (this is how mixing works)
                int mixed = bufferSample + sampleValue;
                // Clamp the result to prevent distortion from overflow
                mixed = Math.max(-32768, Math.min(32767, mixed));
                
                // Convert the mixed result back to 2 bytes and store in buffer
                buffer[i] = (byte) (mixed & 0xFF);         // Low byte
                buffer[i + 1] = (byte) ((mixed >> 8) & 0xFF); // High byte
            }
        }
        
        // Move this sample's playback position forward by the amount we just processed
        sample.advance(samplesToMix);
    }
    
    // Main method called by your sequencer to play a note (implements AudioEngine interface)
    public void playNote(Instrument instrument, Note note) {
        // Don't do anything if audio system isn't working
        if (!isInitialized) return;
        
        // Get the raw audio data for this note's pitch from the instrument
        byte[] sampleData = instrument.getSampleData(note.getPitch());
        // If no sample exists for this pitch, give up
        if (sampleData == null) return;
        
        // Convert the sample to match our output format (44.1kHz, 16-bit, stereo)
        byte[] convertedData = convertToOutputFormat(sampleData);
        
        // Add this sample to the list of currently playing samples
        synchronized (samplesLock) { // Lock because the audio thread is also reading this list
            // Create a new ActiveSample object to track this note
            ActiveSample activeSample = new ActiveSample(
                nextSampleId.getAndIncrement(), // Give it a unique ID and increment counter
                convertedData,                  // The audio data to play
                note,                          // The original note information
                instrument                     // The instrument this came from
            );
            // Add to the list - the audio thread will start mixing it immediately
            activeSamples.add(activeSample);
        } // Release the lock
    }
    
    // Stop a specific note by its pitch and instrument
    public void stopNote(int pitch, Instrument instrument) {
        // Lock the samples list to safely modify it
        synchronized (samplesLock) {
            // Remove all samples that match this pitch and instrument
            activeSamples.removeIf(sample -> 
                sample.getNote().getPitch() == pitch && // Check if pitch matches
                sample.getInstrument() == instrument);   // Check if instrument matches
        }
    }
    
    // Stop all notes currently playing from a specific instrument
    public void stopAllNotes(Instrument instrument) {
        // Lock the samples list to safely modify it
        synchronized (samplesLock) {
            // Remove all samples from this instrument
            activeSamples.removeIf(sample -> sample.getInstrument() == instrument);
        }
    }
    
    // Play a note and return its ID so you can stop it later (used for sustained notes)
    public int playNoteReturningId(Instrument instrument, Note note) {
        // Don't do anything if audio system isn't working
        if (!isInitialized) return -1;
        
        // Get the raw audio data for this note's pitch
        byte[] sampleData = instrument.getSampleData(note.getPitch());
        // If no sample exists, return -1 (invalid ID)
        if (sampleData == null) return -1;
        
        // Convert sample to our output format
        byte[] convertedData = convertToOutputFormat(sampleData);
        
        // Add to active samples and return the ID
        synchronized (samplesLock) {
            // Get a unique ID for this sample
            int id = nextSampleId.getAndIncrement();
            // Create the ActiveSample object
            ActiveSample activeSample = new ActiveSample(id, convertedData, note, instrument);
            // Add it to the list
            activeSamples.add(activeSample);
            // Return the ID so caller can stop this specific sample later
            return id;
        }
    }
    
    // Stop a specific sample by its unique ID
    public void stopSample(int sampleId) {
        // Lock the samples list to safely modify it
        synchronized (samplesLock) {
            // Remove the sample with this ID
            activeSamples.removeIf(sample -> sample.getId() == sampleId);
        }
    }
    
    // Convert audio sample to match our output format (44.1kHz, 16-bit, stereo)
    private byte[] convertToOutputFormat(byte[] inputData) {
        try {
            // Create an audio stream from the raw sample data
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                new ByteArrayInputStream(inputData));
            
            // Get the format of the input sample
            AudioFormat inputFormat = inputStream.getFormat();
            
            // Check if the input format already matches our output format
            if (inputFormat.matches(format)) {
                // No conversion needed - close stream and return original data
                inputStream.close();
                return inputData;
            }
            
            // Convert the audio stream to our target format
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(format, inputStream);
            // Read all the converted data into a byte array
            byte[] convertedData = convertedStream.readAllBytes();
            
            // Clean up - close both streams
            inputStream.close();
            convertedStream.close();
            
            // Return the converted audio data
            return convertedData;
            
        } catch (UnsupportedAudioFileException | IOException e) {
            // If conversion fails, print error and return original data
            System.err.println("Error converting audio format: " + e.getMessage());
            return inputData; // Hope the original data works anyway
        }
    }
    
    // Shut down the audio engine and clean up resources
    public void shutdown() {
        // Tell the audio loop to stop
        running = false;
        // Interrupt the audio thread to wake it up if it's sleeping
        if (audioThread != null) {
            audioThread.interrupt();
        }
        // Close the audio line
        if (line != null) {
            line.drain(); // Wait for remaining audio to finish playing
            line.close(); // Release the audio resources
        }
    }
    
    // Check if the audio engine initialized successfully
    public boolean isInitialized() {
        return isInitialized;
    }
    
    // Get the number of samples currently playing (useful for debugging)
    public int getActiveSampleCount() {
        // Lock to get a consistent count
        synchronized (samplesLock) {
            return activeSamples.size();
        }
    }
    // Inner class to track active samples
    private static class ActiveSample {
        private final int id;
        private final byte[] data;
        private final Note note;
        private final Instrument instrument;
        private int position = 0;

        private final int loopStart;    
        private final int loopEnd;      
        private final boolean shouldLoop; 
        private boolean hasPlayedOnce = false;

        private final Track track;
        
        public ActiveSample(int id, byte[] data, Note note, Instrument instrument) {
            this.id = id;
            this.data = data;
            this.note = note;
            this.instrument = instrument;
            this.shouldLoop = determineShouldLoop(note, instrument);
            this.loopStart = shouldLoop ? instrument.getLoopStart(note.getPitch()) : 0;        //freaky syntax
            this.loopEnd = shouldLoop ? instrument.getLoopEnd(note.getPitch()) : data.length;
            this.track = note.getTrack();
        }
        
        public float getVolumeMultiplier() {
            return track.getVolumeMultiplier();
        }

        public int getId() { return id; }
        public byte[] getData() { return data; }
        public Note getNote() { return note; }
        public Instrument getInstrument() { return instrument; }
        public int getPosition() { return position; }


        private boolean determineShouldLoop(Note note, Instrument instrument) {
            // Don't loop drums
            if (instrument.getInstrumentType() == InstrumentType.DRUMS) {
                return false;
            }
            // Don't loop if no loop points defined
            if (!instrument.hasLoopPoints(note.getPitch())) {
                return false;
            }
            return true;
        }
        public void advance(int bytes) {
            position += bytes;
            
            // handle looping if this sample should loop
            if (shouldLoop && position >= loopEnd) {
                hasPlayedOnce = true;
                position = loopStart + (position - loopEnd);  // Jump back to loop start
                if (position >= loopEnd) {
                    position = loopStart;
                }
            }
        }

        

        public boolean isFinished() {
            if (shouldLoop) {
                return false;  // looping samples never finish
            } else {
                return position >= data.length;  
            }
        }
    }
}