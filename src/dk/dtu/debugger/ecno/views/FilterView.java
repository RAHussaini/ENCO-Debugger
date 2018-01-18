package dk.dtu.debugger.ecno.views;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import dk.dtu.debugger.ecno.controllers.ECNODebuggerEngineController;
import dk.dtu.debugger.ecno.listeners.IElementTypeListener;
import dk.dtu.debugger.ecno.listeners.IEventTypeListener;
import dk.dtu.debugger.ecno.listeners.IRemoveControlListener;
import dk.dtu.debugger.ecno.listeners.ViewPartListener;
import dk.dtu.debugger.ecno.models.FilterViewModel;
import dk.dtu.debugger.ecno.utils.ComboExt;
import dk.dtu.imm.se.ecno.core.IElementType;
import dk.dtu.imm.se.ecno.core.IEventType;
//************************************
import static dk.dtu.debugger.ecno.utils.ViewsUtil.*;
import static dk.dtu.debugger.ecno.utils.ViewsUtil.applyGridLayout;
//******************************************

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class FilterView extends ViewPart implements 
IEventTypeListener, 
IElementTypeListener,
IRemoveControlListener{

	public static final String ID = "dk.dtu.debugger.ecno.views.FilterView";
	private ScrolledComposite filterScroll;
	private Composite filterContainer;
	private ViewPartListener<DebugView> graphViewListener;
	private ComboExt<IEventType> eventTypes;
	private ComboExt<IElementType> elementTypes;
	private static List<FilterViewModel> filters = new ArrayList<>();
	private boolean isDirty = false; // if graphView was not available this will be set to true


	/**
	 * The constructor.
	 */
	public FilterView() {
		// will be notified when graphView is available
		graphViewListener = new ViewPartListener<DebugView>() {
			@Override
			public void viewPartAdded(DebugView part) {
				updateFilters(part);
			}
		};
		DebugView.addViewPartListener(graphViewListener);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		FillLayout parentLayout = new FillLayout();
		parentLayout.type= SWT.VERTICAL;
		parentLayout.marginHeight = 10;
		parentLayout.marginWidth = 5;
		parent.setLayout(parentLayout);

		filterScroll = new ScrolledComposite(parent, SWT.V_SCROLL);

		filterContainer = new Composite(filterScroll, SWT.NONE);		
		filterContainer.setLayout(new FillLayout(SWT.VERTICAL));

		filterScroll.setContent(filterContainer);
		filterScroll.setExpandVertical(true);


		Composite addFilterContainer = new Composite(parent, SWT.ALL);
		applyGridLayout(addFilterContainer, 2);

		Label eventTypesLabel = new Label(addFilterContainer, SWT.NONE);
		eventTypesLabel.setText("Event types: ");
		applyGridLayoutData(eventTypesLabel, 1);

		Label elementTypesLabel = new Label(addFilterContainer, SWT.NONE);
		elementTypesLabel.setText("Element types: ");
		applyGridLayoutData(elementTypesLabel, 1);

		eventTypes = new ComboExt<>(addFilterContainer, SWT.READ_ONLY);
		eventTypes.setEnabled(false);
		applyGridLayoutData(eventTypes, 1);

		elementTypes = new ComboExt<>(addFilterContainer, SWT.READ_ONLY);
		elementTypes.setEnabled(false);
		applyGridLayoutData(elementTypes, 1);



		Button addFilterBtn = new Button(addFilterContainer, SWT.PUSH);
		applyGridLayoutData(addFilterBtn, 1);
		addFilterBtn.setText("Add filter");
		addFilterBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						addFilter();
					}
				});	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		Button applyLayoutBtn = new Button(addFilterContainer, SWT.PUSH);
		applyGridLayoutData(applyLayoutBtn, 1);
		applyLayoutBtn.setText("Apply layout");
		applyLayoutBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DebugView view = getGraphView();
				if(view != null) view.update(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});


		ECNODebuggerEngineController.getInstance().addEventTypeListener(this);
		ECNODebuggerEngineController.getInstance().addElementTypeListener(this);
	}
	/**
	 * 
	 * REV 26/11/2016 - used to get graphView to avoid having class variable with disposed ViewPart.
	 * This will also update graphView with current filter in case filters have been removed/added
	 * while graphView was not available.
	 * @return
	 */
	private DebugView getGraphView(){
		IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(DebugView.ID);

		if (part != null && part instanceof DebugView) {
			DebugView debugView = (DebugView) part;
			if(isDirty){
				updateFilters(debugView);
				isDirty = false;
			}
			
			return debugView;
		}
		isDirty = true;
		return null;
	}

	private void updateFilters(DebugView view){
		if(view == null) return;
		view.setFilters(filters.toArray(new ViewerFilter[0]));
	}

	private void addFilter(){
		final IElementType elementType = elementTypes.getSelectedValue();
		final IEventType eventType = eventTypes.getSelectedValue();
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				FilterViewModel breakpoint = new FilterViewModel(FilterView.this,
						filterContainer, 
						eventType, 
						elementType);

				filters.add(breakpoint);
				DebugView view = getGraphView();
				if(view != null) view.addFilter(breakpoint);
				updateScrollSize();
			}
		});
	}

	@Override
	public synchronized void elementTypesChanged(final IElementType[] updatedList) {
		final String[] keys = new String[updatedList.length + 1];
		int index = 0;
		for(IElementType type : updatedList){
			if(type != null)
				keys[index++] = type.getName();
		}
		keys[index] = "null";

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if(updatedList.length > 0)elementTypes.setEnabled(true);
				else elementTypes.setEnabled(false);
				elementTypes.setComboValues(updatedList, keys);
			}
		});
	}

	@Override
	public void eventTypesChanged(final IEventType[] updatedList) {
		final String[] keys = new String[updatedList.length];
		int index = 0;
		for(IEventType type : updatedList){
			keys[index++] = type.getName();
		}
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if(updatedList.length > 0) eventTypes.setEnabled(true);
				else eventTypes.setEnabled(false);

				eventTypes.setComboValues(updatedList, keys);
			}
		});

	}
	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		ECNODebuggerEngineController.getInstance().removeElementTypeListener(this);
		ECNODebuggerEngineController.getInstance().removeEventTypeListener(this);
		DebugView.removeViewPartListener(graphViewListener);
		super.dispose();
	}

	@Override
	public void removeControl(Object control) {
		filters.remove(control);
		DebugView view = getGraphView();
		if(view != null)view.removeFilter((ViewerFilter) control);

		updateScrollSize();
	}

	private void updateScrollSize(){
		Point size = this.filterContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		this.filterScroll.setMinSize(size);
		this.filterContainer.setSize(size);
		this.filterContainer.layout();
		this.filterScroll.layout();
		this.filterContainer.redraw();
		this.filterScroll.redraw();
	}

	@Override
	public void setFocus() {
	}

}