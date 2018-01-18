package dk.dtu.debugger.ecno.properties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import dk.dtu.debugger.ecno.models.EventViewModel;
import dk.dtu.imm.se.ecno.core.IFormalParameter;
import dk.dtu.imm.se.ecno.runtime.Event;
import dk.dtu.imm.se.ecno.runtime.Parameter;

public class EventProperties implements IPropertySource{

	private final Event event;
	private final EventViewModel eventViewModel;
	private final List<Parameter> parameters;
//	private Field[] fields;
	public EventProperties(EventViewModel event) {
		super();
		this.eventViewModel = event;
		this.event = (Event) event.getNode();
		this.parameters = new ArrayList<>();
		for(IFormalParameter p : this.event.getType().getFormalParametersList()){
			parameters.add(this.event.getParameter(p));
		}
				
//		this.fields = this.event.getClass().getFields();
	}
	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[parameters.size()];

		for (int i=0;i<parameters.size();i++) {				
			// Add each property supported.

			PropertyDescriptor descriptor;
			Parameter param = parameters.get(i);
			
			descriptor = new ObjectPropertyDescriptor(i, param.getType().getName());
			propertyDescriptors[i] = (IPropertyDescriptor)descriptor;
			descriptor.setCategory(eventViewModel.getName());
		}

		// Return it.
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(Object name) {
		for(Parameter param : this.parameters){
			if(name.equals(param)){
				Object value = param.getValue();
				if(value instanceof String){
					return value;
				}else{
					//for now
					return value;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object arg0) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object arg0) {
	}

	@Override
	public void setPropertyValue(Object arg0, Object arg1) {
	}
	
	@Override
	public String toString() {
		return this.event.getType().getName();
	}

}
