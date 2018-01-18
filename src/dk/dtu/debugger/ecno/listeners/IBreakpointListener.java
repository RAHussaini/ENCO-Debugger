package dk.dtu.debugger.ecno.listeners;

import dk.dtu.debugger.ecno.models.InteractionViewModel;

public interface IBreakpointListener {

	public void breakpointHit(InteractionViewModel interaction);
}
