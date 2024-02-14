import java.io.File; 
import java.util.Locale; 
import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral; 
import javax.speech.EngineList; 
import javax.speech.EngineCreate; 
import javax.speech.synthesis.Synthesizer; 
import javax.speech.synthesis.SynthesizerModeDesc; 

public class HelloWorld2 { 

   public static void main(String[] argv) { 
      try { 
         // Create a new SynthesizerModeDesc that will match the FreeTTS Synthesizer. 
         SynthesizerModeDesc desc = new SynthesizerModeDesc(null, "general", Locale.US, Boolean.FALSE, null);

         FreeTTSEngineCentral central = new FreeTTSEngineCentral(); 
         Synthesizer synthesizer = null;

         EngineList list = central.createEngineList(desc); 

         if(list.size() > 0) { 
            EngineCreate creator = (EngineCreate) list.get(0); 
            synthesizer = (Synthesizer) creator.createEngine(); 
         } 

         if (synthesizer == null) { 
            System.err.println("Can't find synthesizer!"); 
            System.exit(1); 
         } 

         // Get it ready to speak 
         synthesizer.allocate(); 
         synthesizer.resume(); 

         // Speak the "Hello world" string 
         synthesizer.speakPlainText("Hello, world!", null); 

         // Wait till speaking is done 
         synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY); 

         // Clean up 
         synthesizer.deallocate(); 
      } catch(Exception e) { 
         e.printStackTrace(); 
      } 

      System.exit(0); 
   } 
} 
