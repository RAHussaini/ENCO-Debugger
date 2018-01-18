package dk.dtu.debugger.ecno.listeners;

import org.eclipse.swt.widgets.Combo;

public interface ComboListener<T,C extends Combo> {

	public void itemSelected(T item, C invoker);
}
