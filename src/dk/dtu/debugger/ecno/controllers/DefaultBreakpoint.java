package dk.dtu.debugger.ecno.controllers;

import dk.dtu.imm.se.ecno.runtime.Interaction;

public class DefaultBreakpoint implements IBreakpoint {

	public boolean isBreakpoint(Interaction interaction){
		
		return true;
	}
}
