package iu;

import inpro.incremental.unit.IU;

public class DialogueActIU extends IU {
	
	String dialogueAct;
	String content;

	public DialogueActIU(String dialogueAct) {
		super();
		this.dialogueAct = dialogueAct;
		this.content = "";
	}
	
	public DialogueActIU(String dialogueAct, String content) {
		super();
		this.dialogueAct = dialogueAct;
		this.content = content;
	}

	@Override
	public String toPayLoad() {
		// TODO Auto-generated method stub
		return dialogueAct + ":" + content;
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return content;
	}
	
	public String getDialogueActType() {
		return dialogueAct;
	}

}
