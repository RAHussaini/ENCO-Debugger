package dk.dtu.debugger.ecno.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class ObjectPropertyDescriptor extends PropertyDescriptor {

	public ObjectPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		setLabelProvider(new ILabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener listener) {
			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}
			
			@Override
			public void dispose() {
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
			}
			
			@Override
			public String getText(Object element) {
				return element.toString();
			}
			
			@Override
			public Image getImage(Object element) {
				return null;
			}
		});

	}

	@Override
	public Object getId() {
		
		return super.getId();
	}
	
	
	
	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		ComboBoxCellEditor editor = new ComboBoxCellEditor(parent, new String[]{"value1", "value2"});
//		editor.setItems(new String[]{"selection 1", "selection 2", "selection 3"});

		if(getValidator() != null)
			editor.setValidator(getValidator());
		
		return editor;
	}




}
