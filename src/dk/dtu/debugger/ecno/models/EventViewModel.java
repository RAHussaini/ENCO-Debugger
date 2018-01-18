package dk.dtu.debugger.ecno.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

import dk.dtu.debugger.ecno.properties.EventProperties;
import dk.dtu.imm.se.ecno.core.IEventType;
import dk.dtu.imm.se.ecno.runtime.Event;

public class EventViewModel extends ObjectViewModel {

	public EventViewModel(IEventType event){

		this.node = event;

		this.name = event.getName() + ":" + event.getClass().getSimpleName();
	}

	public EventViewModel(Event event){
		this.node = event;
		this.name = event.getType().getName() + ":" + event.getClass().getSimpleName();
	}
	private List<ObjectViewModel> references;


	public void addReference(ObjectViewModel element){
		if(references == null) references = new ArrayList<>();
		this.references.add(element);
	}

	public Object[] getReferences(){
		if(this.references == null) return null;
			return this.references.toArray();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return (T) this;
		if(adapter == IPropertySource.class)
			return (T) new EventProperties(this);
//		System.out.println("getAdapter: " + adapter.getName());
		return null;
	}
}
