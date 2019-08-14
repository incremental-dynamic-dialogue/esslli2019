package module;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * @author Julian Hough
 *
 */
public class TextSpeech {

	private String voiceName;
	private Boolean speaking;
	private VoiceManager voiceManager;
    private Voice Talkvoice;
	
	
	public void initVoice() {
		System.out.println("Using voice: " + voiceName);
		

		voiceManager = VoiceManager.getInstance();
		//Voice[] voices = voiceManager.getVoices();
        //for (int v = 0; v<voices.length; v++){
       // 	System.out.println(voices[v]);
        //}
        //System.out.println(voices[0].DATABASE_NAME);
		
		
        Talkvoice = voiceManager.getVoice(voiceName);

        if (Talkvoice == null)
        {
            System.err.println(
                "Cannot find a voice named "
                + voiceName + ".  Please specify a different voice.");
            System.exit(1);
        }

        /* Allocates the resources for the voice.
         */

        Talkvoice.allocate();
        /* Synthesize speech.
         */

        Talkvoice.setPitchShift(1.0f);
        Talkvoice.setRate(150.0f);
        Talkvoice.setPitch((float) 120.0);
        Talkvoice.setPitchRange((float) 12.0);
        Talkvoice.setVolume((float) 1.0);
		
	}

	public TextSpeech() {
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		//System.setProperty("freetts.voices", "de.dfki.lt.freetts.en.us.MbrolaVoiceDirectory");
		//System.setProperty("mbrola.base", "C:\\Program Files (x86)\\Mbrola Tools");
		//System.setProperty("Dfreetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
	
		voiceName = "kevin16";
		speaking = false;
		//voiceName = "us1";
		//this.readTweet("Hi, I'm loading.");
		this.initVoice();
	
	}
		    
	public void read(String text) {
		

        //while (speaking == true){
        //	//do nothing
        //}
		System.out.println("Reading out:" + text);
        this.speaking = true;
        Talkvoice.speak(text);

        this.speaking = false;
	   
	
	}
	
	public void deallocate() {

        /* Clean up and leave.
         */
        Talkvoice.deallocate();
	}
	
	public boolean speaking(){
		return speaking;
	}
		    
	public static void main(String args[]){
			TextSpeech reader = new TextSpeech();
			reader.read("hello");
			reader.read("okay");
			reader.read("okay");
			reader.deallocate();
			
		}
}




