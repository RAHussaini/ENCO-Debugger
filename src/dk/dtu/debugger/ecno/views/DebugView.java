package dk.dtu.debugger.ecno.views;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import dk.dtu.debugger.ecno.controllers.ECNODebuggerEngineController;
import dk.dtu.debugger.ecno.controllers.IECNODebuggerEngineController;
import dk.dtu.debugger.ecno.listeners.ComboListener;
import dk.dtu.debugger.ecno.listeners.DebuggerStateListener;
import dk.dtu.debugger.ecno.listeners.GraphItemListener;
import dk.dtu.debugger.ecno.listeners.GraphNodeSelectionListener;
import dk.dtu.debugger.ecno.listeners.IBreakpointListener;
import dk.dtu.debugger.ecno.listeners.InteractionListener;
import dk.dtu.debugger.ecno.listeners.ViewPartListener;
import dk.dtu.debugger.ecno.models.ElementViewModel;
import dk.dtu.debugger.ecno.models.EventViewModel;
import dk.dtu.debugger.ecno.models.InteractionViewModel;
import dk.dtu.debugger.ecno.models.ObjectViewModel;
import dk.dtu.debugger.ecno.providers.GraphLabelProvider;
import dk.dtu.debugger.ecno.providers.GraphNodeContentProvider;
import dk.dtu.debugger.ecno.utils.ComboExt;
import dk.dtu.debugger.ecno.utils.HideEncounteredElementsFilter;
import dk.dtu.debugger.ecno.utils.HideEventsFilter;


public class DebugView extends ViewPart 
implements IZoomableWorkbenchPart, 
ISelectionChangedListener, 
GraphItemListener,
ComboListener<InteractionViewModel, ComboExt<InteractionViewModel>>,
DebuggerStateListener,
IBreakpointListener,
InteractionListener{

	public static final String ID = "dk.dtu.debugger.ecno.views.debugViewId";
	private GraphViewer viewer;

	private boolean applyLayout = true;
	private boolean addingFilter = false;
	private final List<GraphNodeSelectionListener> listeners = new ArrayList<>();

	private boolean showEncounteredElements = false;

	private volatile List<ObjectViewModel> elements = new ArrayList<>();
	private volatile List<ObjectViewModel> addedElements = new ArrayList<>();
	private volatile List<ObjectViewModel> encounteredElements = new ArrayList<>();

	private HideEncounteredElementsFilter hideEncounteredElementsFilter;
	private HideEncounteredElementsFilter hideAddedElementsFilter;
	private HideEventsFilter hideEventsFilter;
	private static List<ViewPartListener<DebugView>> viewPartListeners = new ArrayList<>();

	public enum LayoutType {
		SPRING, GRID, HORIZONTAL_TREE, RADIAL, TREE
	}

	public DebugView(){		
		
		// standard filters
		//		hideEncounteredElementsFilter = new HideEncounteredElementsFilter(addedElements, encounteredElements);
		//		hideAddedElementsFilter = new HideEncounteredElementsFilter(encounteredElements, addedElements);
		hideEncounteredElementsFilter = new HideEncounteredElementsFilter(encounteredElements);
		hideAddedElementsFilter = new HideEncounteredElementsFilter(addedElements);

		//		hideEventsFilter = new HideEventsFilter();

	}

	public void createPartControl(Composite parent) {

		viewer = new GraphViewer(parent, SWT.NONE);
		
		viewer.setContentProvider(new GraphNodeContentProvider());
		viewer.setLabelProvider(new GraphLabelProvider(viewer));
		viewer.setNodeStyle(ZestStyles.NODES_CACHE_LABEL);
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		viewer.setUseHashlookup(true);
		viewer.setInput(this.elements);

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		// register the menu with the framework
		getSite().registerContextMenu(menuManager, viewer);
		// make the viewer selection available
		getSite().setSelectionProvider(viewer);

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				System.out.println("double click");
				//TODO open ECNO net for element
			}
		});

		applyLayout(LayoutType.TREE);
		viewer.applyLayout();


		IECNODebuggerEngineController insp = ECNODebuggerEngineController.getInstance();
		insp.addElementListener(this);
		insp.addDebuggerStateListener(this);
		insp.addBreakpointListener(this);
		insp.addInteractionListener(this);

		viewer.addSelectionChangedListener(this);

		fillToolBar();
		getSite().setSelectionProvider(viewer);
		showEncounteredElements(false);

		for(ViewPartListener<DebugView> l : viewPartListeners) l.viewPartAdded(this);

	}

	public static void addViewPartListener(ViewPartListener<DebugView> part){
		if(!viewPartListeners.contains(part))viewPartListeners.add(part);
	}

	public static void removeViewPartListener(ViewPartListener<DebugView> part){
		viewPartListeners.remove(part);
	}

	@Override
	public void dispose() {
		IECNODebuggerEngineController insp = ECNODebuggerEngineController.getInstance();
		insp.removeElementListener(this);
		insp.removeDebuggerStateListener(this);
		super.dispose();

	}

	public void applyLayout(LayoutType type) {
		LayoutAlgorithm layout;
		switch (type) {
		case GRID:
			layout = new GridLayoutAlgorithm(LayoutStyles.NONE);
			//			((GridLayoutAlgorithm) layout).setLayoutArea(0, 0, 5000, 5000);
			break;
		case HORIZONTAL_TREE:
			layout = new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			//			((HorizontalTreeLayoutAlgorithm) layout).setLayoutArea(0, 0, 5000, 5000);
			break;
		case RADIAL:
			layout = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			//			((RadialLayoutAlgorithm) layout).setLayoutArea(0, 0, 5000, 5000);
			break;
		case SPRING:
			layout = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			((SpringLayoutAlgorithm) layout).setLayoutArea(0, 0, 5000, 5000);
			break;
		case TREE: //default
		default:
			layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
			//			((TreeLayoutAlgorithm) layout).setLayoutArea(0, 0, 5000, 5000);
			break;

		}
		this.viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{layout, new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING)}), true);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	private void fillToolBar() {
		//		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(
		//				this);
		//		IActionBars bars = getViewSite().getActionBars();
		//		bars.getMenuManager().add(toolbarZoomContributionViewItem);
	}

	@Override
	public void itemSelected(InteractionViewModel interaction, ComboExt<InteractionViewModel> invoker){

		List<ElementViewModel> elements = interaction.getElements();
		List<EventViewModel> events = interaction.getEvents();

		this.hideEventsFilter.setExceptions(events);

		for(ObjectViewModel element : this.elements){
			element.highlight(false);
		}

		for(ElementViewModel element : elements){
			element.highlight(true);
		}
		for(EventViewModel event : events){
			event.highlight(true);
		}
		//		update(false);	

	}

	public void update(final boolean forceLayout, final Object... elements){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
//				System.out.println("updating...");
				if(forceLayout)
					viewer.applyLayout();

				viewer.refresh();
				viewer.update(elements, null);
				
//				System.out.println("finished updating.");
			}
		});
//		System.out.println("update task created...");
	}
	public void update(){
		update(applyLayout);
	}


	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if(!(event.getSelection() instanceof IStructuredSelection)) return;

		Object selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if(selection == null) return;

		if(selection instanceof ObjectViewModel){
			for(GraphNodeSelectionListener listener : this.listeners){
				listener.nodeSelected((ObjectViewModel)selection);
			}
		}
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}

	public void setFilters(ViewerFilter[] filters){
		ViewerFilter[] newList = new ViewerFilter[filters.length+(showEncounteredElements ? 2 : 1)];
		int index = 0;
		for(ViewerFilter f : filters){
			newList[index++] = f;
		}
		newList[index++] = hideEventsFilter;
		if(showEncounteredElements) newList[index] = hideEncounteredElementsFilter;
		this.viewer.setFilters(filters);
	}

	public void removeFilter(ViewerFilter filter){
		this.viewer.removeFilter(filter);
	}

	public void addFilter(ViewerFilter... filter) {
		
		ViewerFilter[] filters = this.viewer.getFilters();
		final ViewerFilter[] newFilters = new ViewerFilter[filters.length+filter.length];
		for(int i = 0; i < filters.length; i++){
			newFilters[i] = filters[i];
		}
		for(int i = 0; i < filter.length; i++){
			newFilters[i+filters.length] = filter[i];
		}
		addingFilter = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				viewer.setFilters(newFilters);
				addingFilter = false;
				updateAddFilterButton();
				// TODO Auto-generated method stub
				
			}
		}).start();
		updateAddFilterButton();
		
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
				
//			}
//		});
	}
	
	private void updateAddFilterButton(){
		if(addingFilter){
			
		}
	}

	public void removeFilter(final ViewerFilter... filter) {
//		Display.getDefault().asyncExec(new Runnable() {	
//			@Override
//			public void run() {
				for(ViewerFilter f : filter)
					viewer.removeFilter(f);
//			}
//		});
	}



	public void showEncounteredElements(boolean show){
		if((showEncounteredElements = show)){
			this.viewer.removeFilter(hideEncounteredElementsFilter);
		}else{
			this.viewer.addFilter(hideEncounteredElementsFilter);
		}
		update(true);
	}

	public void showAddedElements(boolean show){
		if(show){
			this.viewer.removeFilter(hideAddedElementsFilter);
		}else{
			this.viewer.addFilter(hideAddedElementsFilter);
		}
		update(true);
	}

	@Override
	public void elementAdded(ObjectViewModel element) {
		addElement(element);
	}

	@Override
	public void elementRemoved(ObjectViewModel element) {
//		this.encounteredElements.remove(element);
//		this.addedElements.remove(element);
//		this.elements.remove(element);
	}

	@Override
	public void elementEncountered(ObjectViewModel element) {		
		addElement(element);
	}

	@Override
	public void eventAdded(ObjectViewModel item) {
		addElement(item);

	}

	@Override
	public void stateChanged(DebuggerState state) {
		switch(state)
		{
		case INITIALIZED:
		case STARTED: //TODO might be necessary to do something.
			update(true);
			break;
		case STOPPED:
			this.elements.clear();
			this.encounteredElements.clear();
			this.addedElements.clear();
			break;
		}
	}

	private final List<ObjectViewModel> tempElements = new ArrayList<>();
	private final List<ObjectViewModel> tempInteractionElements = new ArrayList<>();

	@Override
	public void breakpointHit(final InteractionViewModel interaction) {
		//		elements.addAll(tempElements);
//		System.out.println("temp i elements: " + tempInteractionElements.size());
//		System.out.println("temp   elements: " + tempElements.size());
//		System.out.println("elements: " + elements.size());

//		Display.getDefault().asyncExec(new Runnable() {
//			
//			@Override
//			public void run() {
				elements.clear();
//				elements.removeAll(tempInteractionElements);
//				tempInteractionElements.clear();
//				tempElements.clear();
//				tempElements.addAll(elements);
//				elements.clear();
//				tempInteractionElements.addAll(interaction.getElements());
//
				interaction.getTriggerElement().highlight(true);
				interaction.getTriggerEvent().highlight(true);
//
//
//				tempInteractionElements.addAll(interaction.getEvents());
//				tempInteractionElements.add(interaction.getTriggerEvent());
//
//				elements.addAll(tempInteractionElements);
				for(ObjectViewModel o : interaction.getElements()) addElement(o);
				for(ObjectViewModel o : interaction.getEvents()) addElement(o);
//				elements.addAll(interaction.getElements());
//				elements.addAll(interaction.getEvents());
				addElement(interaction.getTriggerElement());
				addElement(interaction.getTriggerEvent());
				update(true);
//			}
//		});
		

	}

	@Override
	public void interactionsUpdated(List<InteractionViewModel> interactions) {
		update(true, this.elements);
	}

	private Lock elementLock = new ReentrantLock();
	private void addElement(ObjectViewModel element){
		elementLock.lock();
		if(element.getName().contains("Anton")){
			System.err.println("Anton test: " + element.getNode() + ";" + element.hashCode() + ";");
		}
		System.out.println("contains?:" + this.elements.contains(element) + ";" + element);
		if(!this.elements.contains(element)){
			this.elements.add(element);
		}
		elementLock.unlock();
	}


}

