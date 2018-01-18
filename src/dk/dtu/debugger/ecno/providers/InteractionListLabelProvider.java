package dk.dtu.debugger.ecno.providers;

import org.eclipse.jface.viewers.LabelProvider;

import dk.dtu.imm.se.ecno.runtime.Interaction;

public class InteractionListLabelProvider extends LabelProvider{

@Override
public String getText(Object element) {
	if(element instanceof Interaction){
		Interaction interaction = (Interaction) element;
		String label = interaction.getLabel();
		label += "; isvalid:" + interaction.isValid();
		label += "; checkConditions:" + interaction.checkConditions();
		label += "; size: " + interaction.size();
		return label;
	}
	return null;
}

}
