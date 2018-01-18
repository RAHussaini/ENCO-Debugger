package dk.dtu.debugger.ecno.models;

import java.util.List;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

import dk.dtu.debugger.ecno.utils.ComboExt;
import dk.dtu.imm.se.ecno.core.IFormalParameter;

public class ParameterViewModel implements SelectionListener, ModifyListener{

	private String name;
	private List<String> keys;
	private List<Object> values;
	private Object parameterValue;
	private IFormalParameter parameter;

	public ParameterViewModel(String name, List<String> keys, List<Object> values, IFormalParameter parameter){
		this.name = name;
		this.keys = keys;
		this.values = values;
		this.parameter = parameter;
	}
	
	public String getName() {
		return name;
	}

	public IFormalParameter getParameter(){
		return this.parameter;
	}
	public List<String> getOptionKeys() {
		return keys;
	}

	public List<Object> getSelectionOptions() {
		return values;
	}
	
	public Object getParameterValue(){
		return this.parameterValue;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if(e.getSource() instanceof ComboExt<?>){
			this.parameterValue = ((ComboExt<?>) e.getSource()).getSelectedValue();
			System.out.println("real value: " + parameterValue);
		}
//		System.out.println("widget selected: " + e.getSource());
		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
//		System.out.println("default: " + e + ";" + e.getSource());
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if(e.getSource() instanceof Text){
			Text text = (Text) e.getSource();
			String value = text.getText();
			this.parameterValue = value;
//			System.out.println("modify text: " + e.getSource() + ";"  + e + ";"  + value);

		}
		
	}
	
}
