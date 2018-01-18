package dk.dtu.debugger.ecno.listeners;

import java.util.List;


import dk.dtu.debugger.ecno.models.InteractionViewModel;


public interface InteractionListener {

	void interactionsUpdated(List<InteractionViewModel> interactions);
}
