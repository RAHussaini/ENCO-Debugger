package dk.dtu.debugger.ecno.listeners;

import dk.dtu.imm.se.ecno.core.IEventType;

public interface IEventTypeListener {
	
	public void eventTypesChanged(IEventType[] updatedList); 

}
