package module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import module.game.Game;
//import module.robot.Robot;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import qmul.ds.InteractiveContextParser;
import qmul.ds.Utterance;
import qmul.ds.gui.ParserGUI;
import qmul.ds.dag.DAG;
import qmul.ds.dag.DAGTuple;
import qmul.ds.dag.GroundableEdge;
import qmul.ds.dag.UtteredWord;
import qmul.ds.formula.TTRRecordType;
//import util.WorldBelief;
//import util.WorldBeliefParser;


public class SemanticParser {
	/**
	 * Wrapper for the DS-TTR parser.
	 * Either runs through the SemanticParserGUI class or just the plain Parser class if no
	 * gui is required (faster).
	 */
	private static Logger logger;
	public static final String GRAMMAR_FOLDER = "resources/dsttr"; // "resources/dsttr";
	public static int beam = 10; // the parse beam
	public InteractiveContextParser parser;
	//public ArrayList<DAG<DAGTuple, GroundableEdge>> state_history;
	//public List<DAGTuple> state_history;
	public List<Integer> state_history; // simply 
	public ParserGUI gui; // whether the parser is done through the display (slight delay if so, but can see what's going on)
	
	public SemanticParser(String grammarResource, boolean GUI){
		logger = Logger.getLogger(SemanticParser.class);
		
		//state_history = new ArrayList<DAG<DAGTuple, GroundableEdge>>();
		state_history = new ArrayList<Integer>();
		String resource_url = String.join(File.separator, new String[]{GRAMMAR_FOLDER, grammarResource});
		parser = null;
		if (GUI){
			//gui = new SemanticParserGUI(resource_url, null);
			//gui.setVisible(true);
			gui = new ParserGUI(resource_url, null);
			gui.setVisible(true);
			parser = (InteractiveContextParser) gui.getParser();
			try {
				logger.info("Setting up SemanticParserGUI...");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("SemanticParserGUI ready.");
		} else {

			parser = new InteractiveContextParser(resource_url);
			//parser = new InteractiveContextParser(
			//resource_url,
			//"sys",
			//"usr"
			//);
			//gui = null;
		}
		init();
		
	
	}

	public synchronized boolean parseWord(String word){
		logger.info("Parsing " + word);
		boolean successfulParse = false;
		DAG<DAGTuple, GroundableEdge> state = null;
		DAG<DAGTuple, GroundableEdge> tuple = null;
		//First try the parse
		if (this.gui!=null){
			try {
				//successfulParse = gui.getSemanticParserPanel().parseWordFromExternalSource(word, false);
				
				UtteredWord u_word=new UtteredWord(word, "Dylan");
				Utterance utt = new Utterance(u_word);
				successfulParse = ((InteractiveContextParser) gui.getParser()).parseUtterance(utt);
				System.out.println(((InteractiveContextParser) gui.getParser()).getDialogueHistory().toString());
			} catch (Exception e){
				logger.warn("Semantic parser error");
				e.printStackTrace();
				return false;
				
			}
			//while (!gui.getSemanticParserPanel().done()&!gui.getSemanticParserPanel().error()){
				//wait till the parsing is done or error!
				
			//}
			//tuple = this.gui.getSemanticParserPanel().getParserState(beam).getCurrentTuple();
		} else {
			this.parser.parseWord(new UtteredWord(word, "usr"));
		}
		
		// Add the new state if the word was parsed successfully
		
		try {
			//state_history.add(tuple);
			state_history.add(1); // TODO a dummy
		} catch (Exception e){
			logger.error("Failed to add state");
			e.printStackTrace();
			successfulParse = false;
		}
		return successfulParse;
		
	}
	
	public synchronized void rollBack(int n){
		boolean success = false;
		if (this.gui!=null){
			try {
				//successfulParse = gui.getSemanticParserPanel().parseWordFromExternalSource(word, false);

				success = ((InteractiveContextParser) gui.getParser()).rollBack(n);
				System.out.println(((InteractiveContextParser) gui.getParser()).getDialogueHistory().toString());
			} catch (Exception e){
				logger.warn("Semantic parser error on rollback");
				e.printStackTrace();
			} 
		} else {
			this.parser.rollBack(n);
		}
		
		
	}
	
	/**
	
	public synchronized void rollBack(int n){
		
		 //Get back to the parse state n words back
		 
		int roll_back_to = state_history.size()-n-1;
		logger.info("rolling back to state " + roll_back_to);
		this.gui.getSemanticParserPanel().removeLastNWordsFromDisplay(n);
		if (this.gui!=null){
			if (state_history.size()>1 && roll_back_to>0){
				//recursively go through the parents (backtrack)
				DAG<DAGTuple, GroundableEdge> state = ((InteractiveContextParser) this.gui.getSemanticParserPanel().getParser()).getState();
				DAGTuple current = state.getCurrentTuple();
				GroundableEdge repairableEdge;
				int test = n;
				this.state_history = this.state_history.subList(0, roll_back_to+1);
				logger.debug("rolling back " + n);
				
				while (test > 0){
					//repairableEdge = state.getParentEdge(current);
					//current = state.getSource(repairableEdge);
					repairableEdge = state.getParentEdge(current);
					current = state.getSource(repairableEdge);
					state.setCurrentTuple(current);
					state.removeChildren(current);
					logger.debug(test);
					logger.debug(state.wordStack());
					//this.gui.getSemanticParserPanel().getParser().getState().setCurrentTuple(before);
					//this.gui.getSemanticParserPanel().setParserState(this.gui.getSemanticParserPanel().getParserState(1).getParent());
					test--;
				}
				
				//this.gui.getSemanticParserPanel().setParserState(state_history.get(roll_back_to));
				this.gui.getSemanticParserPanel().updateGUI();
			} else {
				init();
			}
			
			
		} else {
			// normal parsing mode
			if (state_history.size()>1 && roll_back_to>0){
				//this.parser.getState().setCurrentTuple(state_history.get(roll_back_to));
				//TODO
			} else {
				this.parser.init();
				state_history.clear();
			}
		
		}
		
		
	}
	*/
	
	public void init(){
		/**
		 * Clears history and resets parser for a new utterance/sequence.
		 */
		logger.info("initializing parser...");
		if (this.gui!=null){
			state_history.clear();
			this.gui.getParser().init();
			//state_history.add(this.gui.getSemanticParserPanel().getParserState(beam).getCurrentTuple());
			state_history.add(1);
			//this.gui.getSemanticParserPanel().updateGUI(); // should do this automatically
			
		} else {
			state_history.clear();
			this.parser.init();
		}
		
		logger.info("parser initialized");
		
	}
	
	public TTRRecordType getMaxSemantics(){
		TTRRecordType rec = null;
		if (this.gui!=null){
			rec = (TTRRecordType) ((InteractiveContextParser) this.gui.getParser()).getBestTuple().getSemantics();
		} else {
			rec = this.parser.getFinalSemantics();
		}

		return rec;
	}
	
	public static void main(String[] args) throws InterruptedException {
		PropertyConfigurator.configure("log4j.properties");
		/*
		Game a_game = null;
		Robot a_robot = null;
		RobotActionRequestFactory factory = new RobotActionRequestFactory(a_game, a_robot, "resources/classifiers/famula_explore", "lookup");
		
		WorldBeliefParser parser = new WorldBeliefParser();
		// Get the XML input from a file
		try {
		BufferedReader br = new BufferedReader(new FileReader("resources/XMLWorldBelief_2017-07-25_15-23-38.xml"));
		List<String> objectTexts = new ArrayList<String>();

			String input = "";
			String line = br.readLine();
			while (line != null) {
				// System.out.println(line);
				input += line;
				input += System.lineSeparator();
				line = br.readLine();
			}
			String xmlInput = input.toString();
			// Parse the xmlInput
			WorldBelief wb = parser.parseWorldBeliefFromXML(xmlInput);
	
			// Get the scores for a property.
			// Only objects, which have this property, are part of the HashMap.
			//System.out.println(wb.getObjectScoresForProperty("color_red_prob"));
			//System.out.println(wb.getObjectScoresForProperty("label_apple_prob"));
			//System.out.println(wb.getObjectScoresForProperty("elongated_short_prob").get("object0"));
			// Get the xml-text of the object ID
			//System.out.println(wb.getXMLStringForObj(0));
			
			//SIMULATE new vision
			factory.updateVision(wb);
			//factory.updateActionRequestDistribution();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		*/
		SemanticParser p = new SemanticParser("2015-english-ttr-robot", true);
		
		if (true) {
		
		//SIMULATE new word
		p.parseWord("put");
		//factory.updateParseSemantics(p.getMaxSemantics());
		//logger.info(factory.getCurrentActionRequest());
		p.parseWord("the");
		//factory.updateParseSemantics(p.getMaxSemantics());
		//logger.info(factory.getCurrentActionRequest());
		p.parseWord("blue");
		//factory.updateParseSemantics(p.getMaxSemantics());
		//logger.info(factory.getCurrentActionRequest());
		//p.parseWord("green");
		//factory.updateParseSemantics(p.getMaxSemantics());
		//logger.info(factory.getCurrentActionRequest());
		p.parseWord("object");
		//factory.updateParseSemantics(p.getMaxSemantics());
		//logger.info(factory.getCurrentActionRequest());
		
		p.rollBack(1);
		
		
		
		if (false){
			p.parseWord("to");
			//factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			p.parseWord("the");
			//factory.updateParseSemantics(p.getMaxSemantics());
			///logger.info(factory.getCurrentActionRequest());
			p.parseWord("right");
			//factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			p.parseWord("of");
			///factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			p.parseWord("the");
			//factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			p.parseWord("three");
			//factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			p.parseWord("green");
			//factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			p.parseWord("apples");
			//factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			//p.parseWord("apple");
			//factory.updateParseSemantics(p.getMaxSemantics());
			//logger.info(factory.getCurrentActionRequest());
			//TTRRecordType sem = TTRRecordType.parse("[x9 : e|p7==color_red(x9) : t|p9==quant_1(x9) : t|p8==label_apple(x9) : t|head==x9 : e]");
		}
		}
		
	
	}
		

}
