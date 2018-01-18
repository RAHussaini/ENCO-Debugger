package dk.dtu.debugger.ecno.properties;

import java.lang.reflect.Field;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import dk.dtu.imm.se.ecno.core.IFormalParameter;

public class ObjectProperties implements IPropertySource{

	private final Object object;
	private final Field[] fields;
	public ObjectProperties(Object object) {
		this.object = object;
		this.fields = object.getClass().getDeclaredFields();
	}
	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[fields.length];
		int num = 0;
		for (int i=0;i<fields.length;i++) {				
			// Add each property supported.

			PropertyDescriptor descriptor;
			Field field = fields[i];
			if(field.getType().equals(String.class)){
				descriptor = new TextPropertyDescriptor(field, field.getName());
			}else if(field.getType().equals(EList.class)){
				descriptor = new PropertyDescriptor(field, field.getName());
			}else{
				descriptor = new ObjectPropertyDescriptor(num++, field.getName());
			}
			propertyDescriptors[i] = (IPropertyDescriptor)descriptor;
			descriptor.setCategory("Object");
		}

		// Return it.
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(Object name) {
		for(Field f : fields){

			//System.out.println("type: " + f.getType().getName());
			if(name.equals(f)){

				try {
					f.setAccessible(true);
					Object value = f.get(object);
					if(value instanceof EList<?>){
						EList<?> list = (EList<?>) value;
						return new EListProperties(list);
					}else if(value instanceof String){
						return value;
					}else if(value instanceof Object){
						new ObjectProperties(value);
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
	public void setPropertyValue(Object arg0, Object arg1) {
	}

}
