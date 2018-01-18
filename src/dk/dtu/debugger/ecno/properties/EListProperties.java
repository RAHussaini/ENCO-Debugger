package dk.dtu.debugger.ecno.properties;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import dk.dtu.imm.se.ecno.core.IFormalParameter;

public class EListProperties implements IPropertySource{

	private final EList<?> list;
	public EListProperties(EList<?> list) {
		this.list = list;
	}
	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[list.size()];

		for (int i=0;i<list.size();i++) {				
			// Add each property supported.

			PropertyDescriptor descriptor;
			Object item = list.get(i);


			descriptor = new PropertyDescriptor(item, item.getClass().getSimpleName());
			propertyDescriptors[i] = (IPropertyDescriptor)descriptor;
			descriptor.setCategory("EList(size=" + list.size() + ")");
		}

		// Return it.
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(Object name) {
		for(Object o : list){
			if(o.equals(name)){
				return new ObjectProperties(o);
//				return o.toString();
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
		return list.getClass().getSimpleName() + " (size=" + list.size() + ")" ;
	}

}
