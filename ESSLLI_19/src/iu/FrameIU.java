package iu;

import java.util.ArrayList;

import inpro.incremental.unit.IU;
import semantics.GoalLocationRelation;

public class FrameIU extends IU {

	private String objectID; //object ID, if present
	private String action; //an action to take, with the object if present
	private ArrayList<String> destinationID; //a target final position, can be null
	private GoalLocationRelation relation; // relation to the destination object ID (optional)
	private Double confidence; //a real value between 0 and 1 about the confidence of this hyp for the frame
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * A frame with two slots.
	 */
	public FrameIU(String id, String action, double conf) {
		setObjectID(id);
		setAction(action);
		setRelation(null);
		setDestinationID(null);
		setConfidence(conf);
	}
	
	/***
	 * A frame with three slots.
	 */
	public FrameIU(String id, String action, ArrayList<String> targetpos, double conf) {
		setObjectID(id);
		setAction(action);
		setDestinationID(targetpos);
		setRelation(null);
		setConfidence(conf);
	}

	/***
	 * A frame with four slots.
	 */
	public FrameIU(String id, String action, GoalLocationRelation relation, ArrayList<String> targetpos, double conf) {
		setObjectID(id);
		setAction(action);
		setRelation(relation);
		setDestinationID(targetpos);
		setConfidence(conf);
	}
	
	/***
	 * A dummy frame.
	 */
	public FrameIU() {
		this(null, null, null, null, 0.0);
	}

	@Override
	public String toPayLoad() {
		String finalPayload = getObjectID() + ":" + getAction();
		if (this.getRelation()!=null){
			finalPayload += ":" + this.getRelation().toString();
		}
		if (this.getDestinationIDs()!=null){
			finalPayload += ":" + getDestinationIDs();
		}
		finalPayload = finalPayload + ":" + String.valueOf(getConfidence());
		return finalPayload;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String id) {
		this.objectID = id;
	}

	public ArrayList<String> getDestinationIDs() {
		return destinationID;
	}

	public void setDestinationID(ArrayList<String> id) {
		this.destinationID = id;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public GoalLocationRelation getRelation() {
		return relation;
	}

	public void setRelation(GoalLocationRelation relation) {
		this.relation = relation;
	}

}
