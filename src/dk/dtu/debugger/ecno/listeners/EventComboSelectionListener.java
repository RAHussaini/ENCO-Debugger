package dk.dtu.debugger.ecno.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import dk.dtu.debugger.ecno.utils.ComboExt;

public class EventComboSelectionListener extends SelectionAdapter {

	private ComboExt<?> invoker;
	public EventComboSelectionListener(ComboExt<?> invoker) {
		super();
		this.invoker = invoker;
	}
	@Override
	public void widgetSelected(SelectionEvent e) {
		invoker.getComboValue(e.getSource().toString());
	}
	
	
}
