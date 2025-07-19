package sequencer.project.model;

public interface AudioEffect {
    short process(short sample);
    void reset();
}
