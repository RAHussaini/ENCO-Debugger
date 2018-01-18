package dk.dtu.debugger.ecno.controllers;

import dk.dtu.imm.se.ecno.runtime.Interaction;

public interface IBreakpoint {
	
	boolean isBreakpoint(Interaction interaction);

}
