package dk.dtu.debugger.ecno.utils;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import dk.dtu.debugger.ecno.models.EventViewModel;

public class HideEventsFilter extends ViewerFilter{


	private List<EventViewModel> eventExceptions = null;

	public void setExceptions(List<EventViewModel> events){
		this.eventExceptions = events;
	}
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof EventViewModel){
			if(eventExceptions == null) return false;
			
			for(Object o : eventExceptions){
				if(o.equals(element)) return true;
			}	
			return false;
		}
		return true; // default - let node through
	}

}
