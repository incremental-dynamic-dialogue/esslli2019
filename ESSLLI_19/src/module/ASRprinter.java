package module;

import inpro.incremental.IUModule;
import inpro.incremental.unit.EditMessage;
import inpro.incremental.unit.IU;
import inpro.incremental.unit.SlotIU;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class ASRprinter extends IUModule{

	//FileWriter writer;
	// output to this csv file
	String FileName = "resources/SampleAudio/increco_output_r2_sphinx";
	private int count = 0;
	public void newProperties(PropertySheet ps) throws PropertyException {
		super.newProperties(ps);
		System.out.println("Starting print module...");
		
		
		//try {
		//  writer = new FileWriter(FileName);
        //	writer.append("currentTime,word,type,iuID,editID\n");
		//	writer.close();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		
	}

	@Override
	protected void leftBufferUpdate(Collection<? extends IU> ius,
			List<? extends EditMessage<? extends IU>> edits) {

		ArrayList<EditMessage<SlotIU>> newEdits = new ArrayList<EditMessage<SlotIU>>();
		String output = "";
		for (EditMessage<? extends IU> edit: edits) {
				IU iu = edit.getIU();
				System.out.println("IU");
				System.out.println(iu.duration());
				String word = iu.toPayLoad();
				System.out.println(System.currentTimeMillis() + "," + word +","+edit.getType().toString()+","+iu.getID() + ","+count);
				output += " " + word;
				// write information to csv file
				//try {
				//	writer = new FileWriter(FileName, true);
				//	writer.append(System.currentTimeMillis() + "," + word +","+edit.getType().toString()+","+iu.getID() + ","+count+"\n");
				//	writer.close();
				//} catch (IOException e) {
				//	e.printStackTrace();
				//}

			}
		count += 1;
	}
	
}
