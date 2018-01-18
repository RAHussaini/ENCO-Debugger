package dk.dtu.debugger.ecno.properties;

import java.lang.reflect.Field;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import dk.dtu.debugger.ecno.models.ElementViewModel;
import dk.dtu.imm.se.ecno.core.IFormalParameter;

public class ElementProperties implements IPropertySource{

	private final Object element;
	private final ElementViewModel elementViewModel;
	private final Field[] fields;

	public ElementProperties(ElementViewModel element){
		super();
		this.elementViewModel = element;
		this.element = element.getNode();
		this.fields = this.element.getClass().getDeclaredFields();
		
	}
	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		
		
		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[fields.length];

		for (int i=0;i<fields.length;i++) {				
			// Add each property supported.

			PropertyDescriptor descriptor;
			Field field = fields[i];
			if(field.getType().equals(String.class)){
				descriptor = new TextPropertyDescriptor(field, field.getName());
			}else if(field.getType().equals(EList.class)){
				descriptor = new PropertyDescriptor(field, field.getName());
			}else{
				descriptor = new PropertyDescriptor(field, field.getName());
//				descriptor = new ComboBoxPropertyDescriptor(field, field.getName(), new String[]{"label1", "label2"});
			}
			propertyDescriptors[i] = (IPropertyDescriptor)descriptor;
			descriptor.setCategory(elementViewModel.getName());
		}
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(Object name) {
		int num = 0;
		for(Field f : fields){
			
			//System.out.println("type: " + f.getType().getName());
			if(name.equals(f)){
				
				try {
					f.setAccessible(true);
					Object value = f.get(element);
					if(value instanceof EList<?>){
						EList<?> list = (EList<?>) value;
						return new EListProperties(list);
					}else if(value instanceof String){
						return value;
					}else if(f.getType().equals(String.class)){
						return new ObjectPropertyDescriptor(num++, "display");
					}else{
						//for now...
						return value;
					}
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					break;
				}
			}
		}
//		System.out.println("returning name: " + name);
		return name;
	}

	@Override
	public boolean isPropertySet(Object arg0) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object arg0) {
	}

	@Override
	public void setPropertyValue(Object prop, Object value) {
//		System.out.println("prop: " + prop + "; value: " + value);
	}

}
