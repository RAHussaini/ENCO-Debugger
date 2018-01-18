package dk.dtu.debugger.ecno.controllers;

import java.util.List;

import dk.dtu.debugger.ecno.listeners.DebuggerStateListener;
import dk.dtu.debugger.ecno.listeners.GraphItemListener;
import dk.dtu.debugger.ecno.listeners.IBreakpointListener;
import dk.dtu.debugger.ecno.listeners.IElementTypeListener;
import dk.dtu.debugger.ecno.listeners.IEventTypeListener;
import dk.dtu.debugger.ecno.listeners.InteractionListener;
import dk.dtu.debugger.ecno.models.ObjectViewModel;
import dk.dtu.imm.se.ecno.core.IElementType;
import dk.dtu.imm.se.ecno.core.IEventType;
import dk.dtu.imm.se.ecno.engine.ExecutionEngine;
import dk.dtu.imm.se.ecno.runtime.Interaction;

public interface IECNODebuggerEngineController {

	//misc
	void calculateInteractions();
	boolean isElementType(Object element, IElementType type);
	void setEngine(ExecutionEngine engine);
	
	// attach listeners
	void addEventTypeListener(IEventTypeListener listener);
	void addElementTypeListener(IElementTypeListener listener);
	void addElementListener(GraphItemListener listener);
	void addInteractionListener(InteractionListener listener);
	void addBreakpointListener(IBreakpointListener listener);
	void addDebuggerStateListener(DebuggerStateListener listener);
	
	//remove listeners
	void removeEventTypeListener(IEventTypeListener listener);
	void removeElementTypeListener(IElementTypeListener listener);
	void removeElementListener(GraphItemListener listener);
	void removeInteractionListener(InteractionListener listener);
	void removeBreakpointListener(IBreakpointListener listener);
	void removeDebuggerStateListener(DebuggerStateListener listener);
	
	//debug
	void continueFromBreakpoint();
	void addBreakpoint(IBreakpoint breakpoint);
	void removeBreakpoint(IBreakpoint breakpoint);
	
	// getters
	List<IEventType> getEventTypes();
	IEventType[] getEventTypes(Object element);
	List<IElementType> getElementTypes();
	List<ObjectViewModel> getElements();
	List<Interaction> getInteractions(Object element, IEventType eventType);
}
