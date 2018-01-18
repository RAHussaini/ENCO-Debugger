package dk.dtu.debugger.ecno.listeners;

import dk.dtu.debugger.ecno.models.ObjectViewModel;

public interface GraphItemListener {

	void elementAdded(ObjectViewModel item);
	void elementEncountered(ObjectViewModel item);
	void elementRemoved(ObjectViewModel item);
	void eventAdded(ObjectViewModel item);
}
