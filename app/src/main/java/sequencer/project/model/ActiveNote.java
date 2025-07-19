package sequencer.project.model;

public class ActiveNote {
    private int sampleId; // ID from MixingAudioEngine
    private int endStep;
    private int pitch;
    private Instrument instrument;
    private Track track;
    
    public ActiveNote(int sampleId, int endStep, int pitch, Instrument instrument, Track track) {
        this.sampleId = sampleId;
        this.endStep = endStep;
        this.pitch = pitch;
        this.instrument = instrument;
        this.track = track;

    }
    public Track getTrack(){return track;}
    
    public int getSampleId() {
        return sampleId;
    }
    
    public int getEndStep() {
        return endStep;
    }
    
    public int getPitch() {
        return pitch;
    }

    public Instrument getInstrument() {
        return instrument;
    }
    
    public void setEndStep(int endStep) {
        this.endStep = endStep;
    }
}