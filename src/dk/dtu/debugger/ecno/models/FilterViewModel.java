package dk.dtu.debugger.ecno.models;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import dk.dtu.debugger.ecno.controllers.ECNODebuggerEngineController;
import dk.dtu.debugger.ecno.listeners.IRemoveControlListener;
import dk.dtu.imm.se.ecno.core.IElementType;
import dk.dtu.imm.se.ecno.core.IEventType;
import dk.dtu.imm.se.ecno.runtime.Event;

//************************************
import static dk.dtu.debugger.ecno.utils.ViewsUtil.*;
import static dk.dtu.debugger.ecno.utils.ViewsUtil.applyGridLayout;
//******************************************

public class FilterViewModel extends ViewerFilter{
	
	private Button removeBtn;
	private IEventType eventType;
	private IElementType elementType;
	private Group group;
	
	public FilterViewModel(IRemoveControlListener removeListener,
			Composite parent, 
			IEventType eventType, 
			IElementType elementType){
		this.eventType = eventType;
		this.elementType = elementType;
		init(parent, removeListener);
	}
	
	public void init(Composite parent, final IRemoveControlListener removeListener){
		group = new Group(parent, SWT.ALL);
		//		group.setSize(500, 350);
		String groupName = "";
		groupName += eventType == null ? "" : eventType.getName() + " ";
		groupName += (eventType != null && elementType != null) ? "OR " + " " : "";
		groupName += elementType == null ? "" : elementType.getName();
		group.setText(groupName);
		applyGridLayout(group, 2);
		//		applyGridLayoutData(group, 2);

		String eventTypeText = eventType == null ? "(none)" : eventType.getName();
		Label eventTypeLabel = new Label(group, SWT.NONE);
		eventTypeLabel.setText("Event type: " + eventTypeText);
		applyGridLayoutData(eventTypeLabel, 2);

		String elementTypeText = elementType == null ? "(none)" : elementType.getName();		
		Label elementTypeLabel = new Label(group, SWT.NONE);
		elementTypeLabel.setText("Element type: " + elementTypeText);
		applyGridLayoutData(elementTypeLabel, 2);


		this.removeBtn = new Button(group, SWT.PUSH);
		this.removeBtn.setText("Remove");
		this.removeBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeListener.removeControl(FilterViewModel.this);
				dispose();
//				System.out.println("dispose breakpoint");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		applyGridLayoutData(removeBtn, 2);
	}
	
	public void dispose(){
		group.dispose();

	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		boolean hasElementType = false;
		boolean hasEventType = false;
		if(eventType != null) {
			if(element instanceof EventViewModel){
				EventViewModel eventViewModel = (EventViewModel) element;
				Event event = (Event) eventViewModel.getNode();
				if(event.getType().getName().equals(eventType.getName())){
					hasEventType = true;
				}
			}
		}
		if(elementType != null){
			if(element instanceof ElementViewModel){
				ElementViewModel elementViewModel = (ElementViewModel) element;
				if(ECNODebuggerEngineController.getInstance().isElementType(elementViewModel.getNode(), elementType)){
					hasElementType = true;
				}
			}
		}

		return !(hasElementType || hasEventType); // filter needs to return false for element that should not be shown
	}
	
	

}
