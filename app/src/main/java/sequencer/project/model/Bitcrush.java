package sequencer.project.model;

public class Bitcrush implements AudioEffect {
    private int bitDepth;
    public short process(short sample){
        int reduction = 16-bitDepth;
        return (short) ((sample >> reduction) << reduction); //move all bits to the left by reduction positions
    }
    public void reset(){

    }
    public void setBitDepth(int reduction){
        bitDepth=reduction;
    }
}
