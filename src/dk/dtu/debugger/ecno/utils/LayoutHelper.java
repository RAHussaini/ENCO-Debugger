package dk.dtu.debugger.ecno.utils;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

public class LayoutHelper{

	public static void applyGridLayoutData(Control control, int hspan){
		GridData data = new GridData();
    	data.horizontalSpan = hspan;
    	data.grabExcessHorizontalSpace = true;
    	data.horizontalAlignment = GridData.FILL;
		control.setLayoutData(data);
	}
}
