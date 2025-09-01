package sequencer.project.audio;

import java.util.List;
import java.util.Map;

import sequencer.project.GUI.PlaybackCursor;
import sequencer.project.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AudioPlayer {
    private Project sequence;
    private AudioEngine audioEngine; 
    private volatile boolean isPlaying = false;
    private volatile boolean isPaused = false;
    private Thread playbackThread;
    private int currentStep = 0;
    private List<ActiveNote> activeNotes = new ArrayList<>();
    private List<ActiveNote> pausedNotes = new ArrayList<>();

    //cursor

    private PlaybackCursor playbackCursor;
    
        
        
    
    public AudioPlayer(Project sequence, AudioEngine audioEngine) {
        this.sequence = sequence;
        this.audioEngine=audioEngine; // Initialize the mixing engine
    }

    public void linkPlaybackCursor(PlaybackCursor playbackCursor){
        this.playbackCursor=playbackCursor;
    }

    public void play() {
        System.out.println("forreal");
        if (isPlaying) return;
        
        if (!audioEngine.isInitialized()) {
            System.err.println("Audio engine not initialized!");
            return;
        }
        
        isPlaying = true;
        isPaused = false;
        currentStep = 0;            //???????
        playbackThread = new Thread(this::playbackLoop);
        playbackThread.start();
    }
    
    public void stop() {
        pause();
        isPlaying=false;
        if (playbackThread != null) {
            playbackThread.interrupt();
        }
        currentStep = 0;
        playbackCursor.updatePosition(currentStep);
    }
 
    
    public void resume() {
        if(!isPaused){
            System.out.println("not paused");
            return;
        }
        System.out.println("resuming");
        for (ActiveNote pausedNote : pausedNotes) {
            // calculate remaining length
            int remainingLength = pausedNote.getEndStep() - currentStep;
            if (remainingLength > 0) {
                // Restart the note with remaining length
                int newSampleId = audioEngine.playNoteReturningId(pausedNote.getInstrument(), new Note(pausedNote.getPitch(), currentStep, remainingLength, pausedNote.getTrack()));
                System.out.println("Restarted note with new sample ID: " + newSampleId);
                // Update the active note with new sample ID
                ActiveNote resumedNote = new ActiveNote(
                    newSampleId,
                    pausedNote.getEndStep(),
                    pausedNote.getPitch(),
                    pausedNote.getInstrument(),
                    pausedNote.getTrack()
                );
                activeNotes.add(resumedNote);
            }
        }
    // Clear paused notes
    pausedNotes.clear();
    isPaused = false;
    }

    public void setPlaybackPoint(int newPlaybackPoint){
        if(!isPaused){return;}
        currentStep = newPlaybackPoint;
        pausedNotes.clear();
    }
        

    
    private void playbackLoop() {
        System.out.println("Entered playbackLoop");
        // Calculate duration of each 1/64th note step in nanoseconds
        
        long stepDurationNanos = 15_000_000_000L/(4*sequence.getBPM()); // FIXED: 64th notes, not 16th
        // Schedule the very first step to happen immediately + one step duration
        long nextStepTime = System.nanoTime() + stepDurationNanos;
        
        while (isPlaying && !Thread.currentThread().isInterrupted()) {
            if (!isPaused && currentStep < sequence.getLength()) {
                // Process any notes that need to be stopped (for sustained instruments)
                stopNotesAtStep(currentStep);
                
                // Play new notes
                playNotesAtStep();
                
                // Move to next step
                currentStep++;
                playbackCursor.updatePosition(currentStep);
                
                // Wait until it's EXACTLY time for the next step (prevents timing drift)
                long currentTime = System.nanoTime();
                if (currentTime < nextStepTime) {
                    try {
                        // Convert nanoseconds to milliseconds for Thread.sleep()
                        long sleepMillis = (nextStepTime - currentTime) / 1_000_000;
                        if (sleepMillis > 0) {
                            Thread.sleep(sleepMillis);
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                // Schedule the next step at exact intervals (this prevents drift!)
                nextStepTime += stepDurationNanos;
            } else if (currentStep >= sequence.getLength()&&!isPaused) {
                // End of sequence
                isPlaying = false;
                break;
            } else if(isPaused) {
                nextStepTime = System.nanoTime() + stepDurationNanos;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        
        isPlaying = false;
        isPaused = false;
        System.out.println("Playback finished");
    }
    
    private void playNotesAtStep() {
        for (Track track : sequence.getTracks()) {
            Instrument instrument = track.getInstrument();
            InstrumentType instrumentType = instrument.getInstrumentType();
            List<Note> notes = track.getNotesAtStep(currentStep);
            
            if (notes != null) {
                for (Note note : notes) {
                    if (instrumentType == InstrumentType.DRUMS) {
                        // One-shots: just play them
                        audioEngine.playNote(instrument, note);
                    } else {
                        // Sustained notes: track them so we can stop them later
                        int sampleId = audioEngine.playNoteReturningId(instrument, note);
                        ActiveNote activeNote = new ActiveNote(
                            sampleId, // Store sample ID for stopping
                            currentStep + note.getLength(), 
                            note.getPitch(),
                            instrument,
                            note.getTrack()
                        );
                        activeNotes.add(activeNote);
                    }
                }
            }
        }
    }
    
    private void stopNotesAtStep(int step) {
        // For sustained instruments, stop notes that have reached their end
        Iterator<ActiveNote> iterator = activeNotes.iterator();
        while (iterator.hasNext()) {
            ActiveNote activeNote = iterator.next();
            if (activeNote.getEndStep() <= step) {
                // Stop the specific sample by its ID
                audioEngine.stopSample(activeNote.getSampleId());
                iterator.remove();
            }
        }
    }
    public void soloTrack(int trackNumber){
        Track trackToSolo = sequence.getTrack(trackNumber-1);
        for(Track track : sequence.getTracks()){
            track.mute();
        }
        trackToSolo.unmute(); trackToSolo.solo();
    }
    public void unSolo(int trackNumber){
        for(Track track : sequence.getTracks()){
            track.unmute(); track.unsolo();
        }
    }
    public void pause(){
        if(isPaused)return;
        for (ActiveNote activeNote : activeNotes) {
            audioEngine.stopSample(activeNote.getSampleId());
        }
        pausedNotes.addAll(activeNotes);
        activeNotes.clear();
        isPaused = true; 
    }
    //getters
    public boolean isPlaying(){return isPlaying;}
    public boolean isPaused(){return isPaused;}
    public int getCurrentStep(){return currentStep;}
    public AudioEngine getAudioEngine(){return audioEngine;}

    public void shutdown() {
        stop();
        audioEngine.shutdown();
    }
    
}