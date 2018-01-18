package dk.dtu.debugger.ecno.listeners;

public interface DebuggerStateListener {

	public enum DebuggerState{
		INITIALIZED,
		STARTED,
		STOPPED
	}
	void stateChanged(DebuggerState state);
	
}
