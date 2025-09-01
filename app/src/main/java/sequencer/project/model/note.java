package sequencer.project.model;


//represents a single midi note

public class Note {
    private int pitch;
    private int step;
    private int length;
    private int velocity;      //the default velocity for new notes should be set at the track level
    private Track track;
    private boolean selected; //unsure if needed
    private boolean shouldLoop;
    //constants
    public static final int MAX_PITCH = 127; //i didnt need to use seperate pitch and velocity variables as its all 0-127...
    public static final int MIN_PITCH = 0;
    public static final int MAX_VELOCITY = 127;
    public static final int MIN_VELOCITY = 0;
    public static final int MIDDLE_C = 60;  //should i have a max length?? probably...
    //constructor
    public Note(int initPitch, int initPosition, int initLength, Track track){
        pitch = initPitch; step = initPosition; length = initLength; velocity = 127; this.track = track;
    }
     public Note(int initPitch, int initPosition, int initLength){ //simple note for block purposes
        pitch = initPitch; step = initPosition; length = initLength; velocity = 127;
    }
    //getters
    public int getPitch(){return pitch;}
    public int getStep(){return step;}
    public int getLength(){return length;}
    public int getVelocity(){return velocity;}
    public Track getTrack(){return track;}
    //setters
    public void setPitch(int newPitch){
        if(newPitch < MIN_PITCH || newPitch > MAX_PITCH){
            throw new IllegalArgumentException("IMPOSSIBLE!!!");
        }
        this.pitch = newPitch;
    }
    public void setPosition(int newPosition){
        this.step = newPosition;
    }
    public void setLength(int newLength){
        this.length = newLength;
    }
    public void setVelocity(int newVelocity){
        if(newVelocity < MIN_VELOCITY || newVelocity > MAX_VELOCITY){
            throw new IllegalArgumentException("IMPOSSIBLE!!!");
        }
        this.velocity = newVelocity;
    }
    public void setTrack(Track track){this.track=track;}
    //here i will need something to check if notes overlap, as well as something for copying notes
    public void transpose(int semitones){
        setPitch(pitch + semitones);
    }
    public void move(int movement){ //right now, you can't move notes, which is not great, 
        setPosition(step + movement);
    }
}
