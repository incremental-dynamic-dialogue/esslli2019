package module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import inpro.incremental.IUModule;
import inpro.incremental.unit.EditMessage;
import inpro.incremental.unit.EditType;
import inpro.incremental.unit.IU;
import inpro.incremental.unit.WordIU;
import iu.DialogueActIU;
import iu.NumberIU;

public class DialogueManagerNumbers extends IUModule {
	
	public ArrayList<ArrayList<NumberIU>> recordedNumbers; // all previous numbers recorded by the app
	
	// three different variables in terms of grounding status
	public ArrayList<NumberIU> currentNumberExtensionPending;  // the part of the number understood but yet to be confirmed
	public ArrayList<NumberIU> currentNumberExtensionVocalized;
	public ArrayList<NumberIU> currentNumberConfirmed;  // assume phone/banking type number. Given one at a time.
	
	
	
	public void updateGUI() {
		
	}
	

	private void processRevokedNumberIU(NumberIU iu) {
		System.out.println("removing iu " + iu.toPayLoad());
		if (this.currentNumberExtensionVocalized.contains(iu)) {
			System.out.println("already vocalized!");
			return;
		}
		System.out.println(iu);
		System.out.println(this.currentNumberExtensionPending);
		if(this.currentNumberExtensionPending.contains(iu)) {
			boolean removed = this.currentNumberExtensionPending.remove(iu);
			System.out.println(removed);
		} else {
			System.out.println("IU not in pending!");
		}
		
	}


	private void processAddedNumberIU(NumberIU iu) {
		// US style number 001-541-754-3010
		ArrayList<EditMessage<DialogueActIU>> newDialogueActIUEdits = new ArrayList<EditMessage<DialogueActIU>>();
		currentNumberExtensionPending.add(iu);
		System.out.println("Current number extension pending");
		System.out.println(currentNumberExtensionPending);
		System.out.println("Current number confirmed");
		System.out.println(currentNumberConfirmed);
		
		if (this.currentNumberExtensionPending.size()==3 & this.currentNumberConfirmed.size()<9) {
			// after 3, 6 and 9 digits send the digits for confirmation
			String content = "";
			for (NumberIU number : this.currentNumberExtensionPending) {
				content+= number.toPayLoad() + " ";
				this.currentNumberExtensionVocalized.add(number);
			}
			newDialogueActIUEdits.add(new EditMessage<DialogueActIU>(EditType.ADD,new DialogueActIU("verify", content)));
			//this.currentNumberExtensionPending.clear();
			
		} else if (this.currentNumberExtensionPending.size()==4 &this.currentNumberConfirmed.size()==9){
			// after 13 digits, end of number tbc
			String content = "";
			for (NumberIU number : this.currentNumberExtensionPending) {
				content+= number.toPayLoad() + " ";
				this.currentNumberExtensionVocalized.add(number);
			}
			newDialogueActIUEdits.add(new EditMessage<DialogueActIU>(EditType.ADD,new DialogueActIU("verify", content)));
			//this.currentNumberExtensionPending.clear();
		}
		
		rightBuffer.setBuffer(newDialogueActIUEdits);
		
	}
	

	private void processDialogueActIU(DialogueActIU iu) {
		ArrayList<EditMessage<DialogueActIU>> newDialogueActIUEdits = new ArrayList<EditMessage<DialogueActIU>>();
		// TODO Auto-generated method stub
		if (((DialogueActIU) iu).getDialogueActType().equals("reject")){
			currentNumberExtensionPending.clear();
		} else if (((DialogueActIU) iu).getDialogueActType().equals("confirm")){
			ArrayList<NumberIU> toRemove = new ArrayList<NumberIU>();
			for (NumberIU number : this.currentNumberExtensionPending) {
				this.currentNumberConfirmed.add(number);
				toRemove.add(number);
			}
			for (NumberIU number : toRemove) {
				this.currentNumberExtensionVocalized.remove(number);
				this.currentNumberExtensionPending.remove(number);
			}
			// if it's a confirmation, go ahead
			if (this.currentNumberConfirmed.size()==13) {
				String content = "okay, so that's ";
				for (NumberIU number : this.currentNumberConfirmed) {
					content+= number.toPayLoad() + " ";
				}
				
				newDialogueActIUEdits.add(new EditMessage<DialogueActIU>(EditType.ADD,new DialogueActIU("verify", content)));
			}
			
			
		}
		rightBuffer.setBuffer(newDialogueActIUEdits);
	}
	
	
	public void newProperties(PropertySheet ps) throws PropertyException {
		super.newProperties(ps);
		logger.info("Started dialogue manager");
		currentNumberConfirmed = new ArrayList<NumberIU>();  // assume phone/banking type number. Given one at a time.
		currentNumberExtensionPending = new ArrayList<NumberIU>();
		currentNumberExtensionVocalized = new ArrayList<NumberIU>();
	}
	

	@Override
	protected void leftBufferUpdate(Collection<? extends IU> ius, List<? extends EditMessage<? extends IU>> edits) {
		System.out.println("Dialogue manager getting updates");
		System.out.println(edits);
		for (EditMessage<? extends IU> edit: edits) {
			IU iu = edit.getIU();
			if (iu instanceof NumberIU){
				String number = iu.toPayLoad();
				switch (edit.getType()) {
					case ADD:
						logger.debug(System.currentTimeMillis() + "," + number +","+edit.getType().toString()+","+iu.getID());
						logger.info("adding " + edit.getIU().toPayLoad());
						this.processAddedNumberIU((NumberIU) iu); 
						break;
					case REVOKE:
						logger.debug(System.currentTimeMillis() + "," + number +","+edit.getType().toString()+","+iu.getID());
						logger.info("revoking " + edit.getIU().toPayLoad());
						//TODO this.parser.rollback.. TODO and merge??
						this.processRevokedNumberIU((NumberIU) iu);
						break;
					case COMMIT:
						break;
					default:
						break;
				}
			} else if (iu instanceof DialogueActIU) {
				this.processDialogueActIU((DialogueActIU) iu);
			}
		}
		
		this.updateGUI();
	}


	
}