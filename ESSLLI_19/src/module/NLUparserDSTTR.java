package module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4String;
import inpro.incremental.IUModule;
import inpro.incremental.unit.EditMessage;
import inpro.incremental.unit.EditType;
import inpro.incremental.unit.IU;
import inpro.incremental.unit.WordIU;
import iu.RobotFrameIU;
import util.Pair;

/**
 * Abstract class for a generic incremental NLU module with a DS grammar.
 * Parses words and sends a TTR record type IU onto a dialogue manager.
 * 
 * This is a subclass of an incremental module in InproTk.
 * 
 * @author jhough
 */
public class NLUparserDSTTR extends IUModule {
	
	
	@S4String(defaultValue = "")
	public final static String GRAMMAR = "grammar";
	
	@S4String(defaultValue = "")
	public final static String LANGUAGE = "language";
	
	//public DArecognizer DA;
	public WordUtil wordUtil;
	
	//@S4String(defaultValue = "")
	//public final static String CLASSIFIERS = "classifiers";
	
	private SemanticParser parser; //Semantic Parser
	
	//@S4Component(type = SceneUpdateModule.class)
	//public final static String SCENE = "scene";
	//private SceneUpdateModule scene;
	
	//@S4Component(type = Robot.class)
	//public final static String ROBOT = "robot";
	//public Robot robot; //Has access by reference to robot state
	
	//public Game game; //Has access by reference to game state
	
	
	
	protected RobotFrameIU currentFrame; //the current frame being sent to the robot, initialized to null
	//protected DialogueAct currentDialogueAct; //the current dialogue act, initialized to null
	//protected List<Pair<WordIU, String>> currentDialogueActWordStack; //the words in the current dialogue act //TODO change to all words so far?
	protected int lastWordEndTime; //millis

	private List<Pair<Integer, Integer>> wordIUtoParserStateMap;
	private List<String> parsedWords; // a list of the words parsed successfully
	//public HashMap<Integer, DialogueAct> processedDialogueActs; // a list of the dialogue acts with one to one mapping to parsed words
	
	public double getTimeSinceLastWordEnd(){
		return this.getTime() - lastWordEndTime;
	}
	
	public void setLastWordEndTime(int time){
		lastWordEndTime = time;
	}

	
	public synchronized void groundWordIUinParserStateIdx(IU iu, int state_idx){
		logger.debug("grounding " + iu.getID() + " " + iu.toPayLoad());
		Pair<Integer, Integer> pair = new Pair<Integer, Integer>(iu.getID(), state_idx);
		wordIUtoParserStateMap.add(pair);
		logger.debug(this.wordIUtoParserStateMap);
		parsedWords.add(iu.getID() + " " + iu.toPayLoad());
	}
	
	public synchronized void rollBackParserStateFromWordIU(IU iu){
		logger.debug("rolling back to before IU with id " + iu.getID());
		if (this.wordIUtoParserStateMap.isEmpty()){
		//if (this.parsedWords.isEmpty()){
			logger.debug("Empty state, no need to roll back");
			return;
		}
		//this.processedDialogueActs.remove(iu.getID());
		int rollback = 0;
		int current_idx = wordIUtoParserStateMap.size()-1;
		logger.debug("Current right frontier index: " + current_idx);
		for (int i=current_idx; i >= 0; i=i-1){
			int id = wordIUtoParserStateMap.get(i).getLeft();
			rollback++;
			if (iu.getID()==id){
				logger.debug("matched ID " + id);
				logger.debug("rolling back " + rollback);
				this.parser.rollBack(rollback);
				//this.wordIUtoParserStateMap = this.wordIUtoParserStateMap.subList(0, (current_idx-rollback));
				this.parsedWords = this.parsedWords.subList(0, (current_idx-rollback));
				return;
			}

		}
		logger.debug("No matching parser state found");
		
	}
	
	public SemanticParser getParser(){
		return this.parser;
	}
	
	/**
	 * Do the desired processing for ADDed WordIU. 
	 * 
	 * @param iu
	 */
	protected synchronized void processAddedWordIU(WordIU iu) {
		String word = wordUtil.normalizeFromASR(iu.toPayLoad().toLowerCase());
		//DialogueAct dact = DA.recognizeGroundingByKeyWord(word);
		boolean successfulParse = false;
		try {
			successfulParse = this.parser.parseWord(word);
		} catch (RuntimeException e) {
			logger.error(e.getMessage());
			logger.info("Parse failed.");
		}
		if (successfulParse){
			this.groundWordIUinParserStateIdx(iu, this.parser.state_history.size()-1);
		}
		//this.processedDialogueActs.put(iu.getID(), dact);
		//this.requestFactory.updateParseSemantics(this.getParser().getMaxSemantics());
		//this.currentDialogueAct = this.processedDialogueActs.get(iu.getID());
		//logger.info(this.currentDialogueAct);
		// assume the error is in the classifier rather than ASR
		
	}
	
	/**
	 * Do the desired processing for REVOKEd WordIU. 
	 * 
	 * @param iu
	 */
	protected synchronized void processRevokedWordIU(WordIU iu) {
		this.rollBackParserStateFromWordIU(iu);
	}
	
	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		super.newProperties(ps);
		System.out.println(ps.getString(GRAMMAR));
		System.out.println(ps.getString(LANGUAGE));
		wordUtil = new WordUtil(ps.getString(LANGUAGE).split("-")[0]);
		parser = new SemanticParser(ps.getString(GRAMMAR),true);
		//DA = new DArecognizer(ps.getString(LANGUAGE));
		
		
		lastWordEndTime = this.getTime();
		currentFrame = new RobotFrameIU("-1", "-1", 1.0);
		//currentDialogueAct = null;
		//currentDialogueActWordStack = new ArrayList<Pair<WordIU, String>>();
		wordIUtoParserStateMap = new ArrayList<Pair<Integer, Integer>>();
		parsedWords = new ArrayList<String>();
		//processedDialogueActs = new HashMap<Integer, DialogueAct>();
		
	}
	
	@Override
	protected void leftBufferUpdate(Collection<? extends IU> ius,
			List<? extends EditMessage<? extends IU>> edits) {
		
		ArrayList<EditMessage<RobotFrameIU>> newEdits = new ArrayList<EditMessage<RobotFrameIU>>();
		for (EditMessage<? extends IU> edit: edits) {
			IU iu = edit.getIU();
			if (iu instanceof WordIU){
				String word = iu.toPayLoad();
				switch (edit.getType()) {
					case ADD:
						logger.debug(System.currentTimeMillis() + "," + word +","+edit.getType().toString()+","+iu.getID());
						logger.info("adding " + edit.getIU().toPayLoad());
						this.processAddedWordIU((WordIU) iu); 
						break;
					case REVOKE:
						logger.debug(System.currentTimeMillis() + "," + word +","+edit.getType().toString()+","+iu.getID());
						logger.info("revoking " + edit.getIU().toPayLoad());
						//TODO this.parser.rollback.. TODO and merge??
						this.processRevokedWordIU((WordIU) iu);
						break;
					case COMMIT:
						break;
					default:
						break;
				}
			}
		}
		/*
		
		logger.info(this.requestFactory.getCurrentActionRequest());
		if (this.requestFactory.getConfidenceInArgMax()>this.threshold){
			List<FrameIU> newFrames = this.requestFactory.getCurrentActionRequest();
			if (newFrames!=null){
				for (FrameIU newFrame : newFrames){
					if (this.currentFrame==null||this.currentFrame.getObjectID().equals("-1")){
						this.currentFrame = newFrame;
						newEdits.add(new EditMessage<FrameIU>(EditType.ADD, newFrame));
						
					} else {
						if (this.currentFrame.getAction().equals(newFrame.getAction())&&
								this.currentFrame.getObjectID().equals(newFrame.getObjectID())&&
								this.currentFrame.getDestinationIDs().equals(newFrame.getDestinationIDs())){
							continue;
						}
						newEdits.add(new EditMessage<FrameIU>(EditType.ADD, newFrame));
					}
				}
			}
		}
		*/
		
		boolean TEST = true;
		if (!TEST){
			rightBuffer.setBuffer(newEdits);	
		}
		
		
	}

}
