package dk.dtu.debugger.ecno.models;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import dk.dtu.debugger.ecno.controllers.ECNODebuggerEngineController;
import dk.dtu.debugger.ecno.controllers.IBreakpoint;
import dk.dtu.debugger.ecno.listeners.IRemoveControlListener;
import dk.dtu.imm.se.ecno.core.IElementType;
import dk.dtu.imm.se.ecno.core.IEventType;
import dk.dtu.imm.se.ecno.runtime.Event;
import dk.dtu.imm.se.ecno.runtime.Interaction;
import dk.dtu.imm.se.ecno.runtime.Link;
//************************************
import static dk.dtu.debugger.ecno.utils.ViewsUtil.*;
import static dk.dtu.debugger.ecno.utils.ViewsUtil.applyGridLayout;
//******************************************

public class BreakpointViewModel implements IBreakpoint {

	private Button removeBtn;
	private IEventType eventType;
	private IElementType elementType;
	private String operator;
	private Group group;

	public BreakpointViewModel(IRemoveControlListener removeListener,
			Composite parent, 
			IEventType eventType, 
			IElementType elementType, 
			String operator){
		this.eventType = eventType;
		this.elementType = elementType;
		this.operator = operator;
		init(parent, removeListener);

	}

	public void dispose(){
		group.dispose();

	}

	public void init(Composite parent, final IRemoveControlListener removeListener){
		group = new Group(parent, SWT.ALL);
		//		group.setSize(500, 350);
		String groupName = "";
		groupName += eventType == null ? "" : eventType.getName() + " ";
		groupName += (eventType != null && elementType != null) ? operator + " " : "";
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
				removeListener.removeControl(BreakpointViewModel.this);
				dispose();
//				System.out.println("dispose breakpoint");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		applyGridLayoutData(removeBtn, 2);
	}

	@Override
	public boolean isBreakpoint(Interaction interaction) {
		boolean checkEventType = false;
		boolean checkElementType = false;

		boolean hasElementType = false;
		boolean hasEventType = false;
		if(eventType != null) {
			checkEventType = true;
			for(Link link : interaction.getLinks()){
				Collection<Event> events = interaction.getEvents(link);
				for(Event e : events){
					if(e.getType().getName().equals(eventType.getName())){
						hasEventType = true;
						break;
					}
				}
				if(hasEventType) break;
			}
		}
		if(interaction.getTriggerEvent().getType().getName().equalsIgnoreCase(eventType.getName())){
			hasEventType = true;
		}
		if(elementType != null){
			checkElementType = true;
			for(Object o : interaction.getElements()){
				if(ECNODebuggerEngineController.getInstance().isElementType(o, elementType)){
					hasElementType = true;
					break;
				}
			}
			if(ECNODebuggerEngineController.getInstance().isElementType(interaction.getTriggerElement(), elementType)){
				hasElementType = true;
			}
		}
		

		if(checkElementType && checkEventType){
			if(operator.equalsIgnoreCase("and"))
				return hasElementType && hasEventType;
			if(operator.equalsIgnoreCase("or"))
				return hasElementType || hasEventType; 
		}
		if(checkElementType && !checkEventType){
			return hasElementType;
		}
		if(checkEventType && !checkElementType){
			return hasEventType;
		}
		return false;
	}

	public IBreakpoint getDebugController(){
		return this;
	}


}
