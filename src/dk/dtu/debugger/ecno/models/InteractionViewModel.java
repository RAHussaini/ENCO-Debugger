package dk.dtu.debugger.ecno.models;

import java.util.List;

import dk.dtu.imm.se.ecno.runtime.Interaction;

public class InteractionViewModel {

	private Interaction interaction;
	private List<ElementViewModel> elements;
	private List<EventViewModel> events;
	private ElementViewModel triggerElement;
	private EventViewModel triggerEvent;
	private String name;
	
	public InteractionViewModel(Interaction interaction, 
			List<ElementViewModel> elements, 
			List<EventViewModel> events,
			ElementViewModel triggerElement,
			EventViewModel triggerEvent,
			String name){
		this.interaction = interaction;
		this.elements = elements;
		this.events = events;
		this.triggerElement = triggerElement;
		this.triggerEvent = triggerEvent;
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Interaction getInteraction(){
		return this.interaction;
	}

	public List<ElementViewModel> getElements() {
		return elements;
	}

	public List<EventViewModel> getEvents() {
		return events;
	}

	public ElementViewModel getTriggerElement() {
		return triggerElement;
	}

	public EventViewModel getTriggerEvent() {
		return triggerEvent;
	}
}
