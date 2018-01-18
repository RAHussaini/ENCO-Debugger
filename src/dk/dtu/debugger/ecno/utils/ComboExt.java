package dk.dtu.debugger.ecno.utils;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public  class ComboExt<T> extends Combo {

	private final Hashtable<String, T> values;
	public ComboExt(Composite parent, int style) {
		super(parent, style);
		values = new Hashtable<>();
	}
	
	@SuppressWarnings("unchecked")
	public void setComboValues(List<T> data, String[] keys) {
		setComboValues((T[]) data.toArray(), keys);
	}
	
	public synchronized void setComboValues(T[] data, String[] keys){
		values.clear();
		int index = 0;
		for(T e : data){
			values.put(keys[index++], e);
		}
		setItems(keys);
	}

	public T getSelectedValue(){
		return this.values.get(getText());
	}
	public T getComboValue(String key){
		return values.get(key);
	}
	
	@Override
	protected void checkSubclass() {
		//DO NOTHING. this allows subclassing
	}
}
