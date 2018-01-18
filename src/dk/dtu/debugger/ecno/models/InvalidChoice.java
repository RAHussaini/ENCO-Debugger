package dk.dtu.debugger.ecno.models;

import java.util.List;

import dk.dtu.imm.se.ecno.core.IEventType;
import dk.dtu.imm.se.ecno.engine.ExecutionEngine.InvalidChoiceType;

public class InvalidChoice {

	private InvalidChoiceType type;
	private List<IEventType> eventTypes;
	private ElementViewModel element;
	
	public InvalidChoice(ElementViewModel element, List<IEventType> eventTypes, InvalidChoiceType type){
		this.element = element;
		this.eventTypes = eventTypes;
		this.type = type;
	}
	
	public ElementViewModel getElement(){
		return this.element;
	}
	
	public List<IEventType> getEventTypes(){
		return this.eventTypes;
	}
	
	public String getErrorMessage(){
		String message = "";
		switch(type){
		case ASSIGNMENT_INCOMPLETE:
			message = "All assignments are not complete";
			break;
		case CONDITION:
			message = "A condition was not fulfilled";
			break;
		case DO_ASSIGNMENT:
			message = "An assignment failed";
			break;
		}
		
		return message;
	}
}
