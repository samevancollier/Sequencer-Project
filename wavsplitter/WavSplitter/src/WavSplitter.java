import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

public class WavSplitter{
    private static final double SILENCE_THRESHOLD=0.01; // amplitude threshold for silence
    private static final int MIN_SOUND_SAMPLES=1000; // minimum samples for a sound
    private static final int MIN_SILENCE_SAMPLES=500; // minimum samples for silence
    
    private static final String[] NOTES={"c","cs","d","ds","e","f","fs","g","gs","a","as","b"};
    
    public static void main(String[] args){
        if(args.length!=2){
            System.out.println("usage: java WavSplitter <input.wav> <lowest_octave>");
            return;
        }
        
        String inputFile=args[0];
        int lowestOctave=Integer.parseInt(args[1]);
        
        try{
            splitWav(inputFile,lowestOctave);
        }catch(Exception e){
            System.out.println("error: "+e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void splitWav(String inputFile,int lowestOctave) throws Exception{
        File file=new File(inputFile);
        AudioInputStream audioStream=AudioSystem.getAudioInputStream(file);
        AudioFormat format=audioStream.getFormat();
        
        // read entire file into memory
        byte[] audioData=audioStream.readAllBytes();
        audioStream.close();
        
        // convert to samples
        double[] samples=bytesToSamples(audioData,format);
        
        // find sound segments
        List<int[]> soundSegments=findSoundSegments(samples);
        
        // save each segment
        int noteIndex=0;
        int currentOctave=lowestOctave;
        
        for(int i=0;i<soundSegments.size();i++){
            int[] segment=soundSegments.get(i);
            int startSample=segment[0];
            int endSample=segment[1];
            
            // calculate time for error reporting
            double timeInSeconds=startSample/(double)format.getSampleRate();
            
            String note=NOTES[noteIndex];
            String noteName=currentOctave+note+note.charAt(0);
            String outputFile=noteName+".wav";
            
            try{
                saveSegment(audioData,format,startSample,endSample,outputFile);
                System.out.println("saved: "+outputFile+" ("+String.format("%.2f",timeInSeconds)+"s)");
                
                noteIndex++;
                if(noteIndex>=NOTES.length){
                    noteIndex=0;
                    currentOctave++;
                }
            }catch(Exception e){
                System.out.println("failed to parse at time: "+String.format("%.2f",timeInSeconds)+"s - "+e.getMessage());
                return;
            }
        }
        
        System.out.println("split complete. created "+soundSegments.size()+" files.");
    }
    
    private static double[] bytesToSamples(byte[] audioData,AudioFormat format){
        int bytesPerSample=format.getSampleSizeInBits()/8;
        int numSamples=audioData.length/bytesPerSample;
        double[] samples=new double[numSamples];
        
        for(int i=0;i<numSamples;i++){
            if(bytesPerSample==1){
                samples[i]=(audioData[i]&0xFF)/128.0-1.0;
            }else if(bytesPerSample==2){
                int sample=(audioData[i*2]&0xFF)|(audioData[i*2+1]<<8);
                samples[i]=sample/32768.0;
            }
        }
        
        return samples;
    }
    
    private static List<int[]> findSoundSegments(double[] samples){
        List<int[]> segments=new ArrayList<>();
        boolean inSound=false;
        int soundStart=0;
        int silenceCount=0;
        int soundCount=0;
        
        for(int i=0;i<samples.length;i++){
            double amplitude=Math.abs(samples[i]);
            
            if(amplitude>SILENCE_THRESHOLD){
                if(!inSound){
                    if(silenceCount>=MIN_SILENCE_SAMPLES||segments.isEmpty()){
                        soundStart=i;
                        inSound=true;
                        soundCount=0;
                    }
                }
                silenceCount=0;
                soundCount++;
            }else{
                if(inSound){
                    silenceCount++;
                    if(silenceCount>=MIN_SILENCE_SAMPLES&&soundCount>=MIN_SOUND_SAMPLES){
                        segments.add(new int[]{soundStart,i-silenceCount});
                        inSound=false;
                    }
                }
            }
        }
        
        // handle final sound if file ends with sound
        if(inSound&&soundCount>=MIN_SOUND_SAMPLES){
            segments.add(new int[]{soundStart,samples.length-1});
        }
        
        return segments;
    }
    
    private static void saveSegment(byte[] originalData,AudioFormat format,int startSample,int endSample,String filename) throws Exception{
        int bytesPerSample=format.getSampleSizeInBits()/8;
        int startByte=startSample*bytesPerSample;
        int endByte=endSample*bytesPerSample;
        int segmentLength=endByte-startByte;
        
        byte[] segmentData=new byte[segmentLength];
        System.arraycopy(originalData,startByte,segmentData,0,segmentLength);
        
        ByteArrayInputStream bais=new ByteArrayInputStream(segmentData);
        AudioInputStream segmentStream=new AudioInputStream(bais,format,segmentLength/format.getFrameSize());
        
        File outputFile=new File(filename);
        AudioSystem.write(segmentStream,AudioFileFormat.Type.WAVE,outputFile);
        
        segmentStream.close();
        bais.close();
    }
}