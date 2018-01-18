package dk.dtu.debugger.ecno.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.events.SelectionAdapter;

import dk.dtu.imm.se.ecno.core.IElementBehaviour;
import dk.dtu.imm.se.ecno.core.IElementType;
import dk.dtu.imm.se.ecno.core.IEventType;
import dk.dtu.imm.se.ecno.engine.ExecutionEngine;
import dk.dtu.imm.se.ecno.engine.ExecutionEngine.InvalidChoiceType;
import dk.dtu.imm.se.ecno.engine.IController;
import dk.dtu.imm.se.ecno.runtime.Event;
import dk.dtu.imm.se.ecno.runtime.IChoice;
import dk.dtu.imm.se.ecno.runtime.Interaction;
import dk.dtu.imm.se.ecno.runtime.InteractionIterator;
import dk.dtu.imm.se.ecno.runtime.InvalidStateException;
import dk.dtu.imm.se.ecno.runtime.Link;
import dk.dtu.debugger.ecno.listeners.DebuggerStateListener;
import dk.dtu.debugger.ecno.listeners.DebuggerStateListener.DebuggerState;
import dk.dtu.debugger.ecno.listeners.GraphItemListener;
import dk.dtu.debugger.ecno.listeners.IBreakpointListener;
import dk.dtu.debugger.ecno.listeners.IElementTypeListener;
import dk.dtu.debugger.ecno.listeners.IEventTypeListener;
import dk.dtu.debugger.ecno.listeners.InteractionListener;
import dk.dtu.debugger.ecno.models.ElementViewModel;
import dk.dtu.debugger.ecno.models.EventViewModel;
import dk.dtu.debugger.ecno.models.InteractionViewModel;
import dk.dtu.debugger.ecno.models.InvalidChoice;
import dk.dtu.debugger.ecno.models.ObjectViewModel;
import dk.dtu.debugger.ecno.utils.ElementHelper;

public class ECNODebuggerEngineController extends SelectionAdapter implements 
IController,
dk.dtu.imm.se.ecno.engine.ECNODebugger,
IECNODebuggerEngineController
{

	//	protected static Logger log = zesttreeviewtest.util.Logger.getLog();

	private static ECNODebuggerEngineController instance;
	//	private static boolean applyLayoutOnChange = false;
	private ExecutionEngine engine;

	//breakpoints
	private List<IBreakpoint> breakpoints = new ArrayList<>();
	private CountDownLatch latch = null;

	//lists
	private volatile ArrayList<ObjectViewModel> elements = new ArrayList<>(); // all elements

	private volatile List<ObjectViewModel> encounteredElements = new ArrayList<>();
	private volatile List<ObjectViewModel> addedElements = new ArrayList<>();
	private volatile List<EventViewModel> events = new ArrayList<>();

	private volatile List<IEventType> eventTypes = new ArrayList<>();
	private volatile List<IElementType> elementTypes = new ArrayList<>();

	//listeners
	private volatile List<IEventTypeListener> eventTypeListeners = new ArrayList<>();
	private volatile List<IElementTypeListener> elementTypeListeners = new ArrayList<>();
	private volatile List<InteractionListener> interactionListeners = new ArrayList<>();
	private volatile List<GraphItemListener> elementListener = new ArrayList<>();
	private volatile List<IBreakpointListener> breakpointListener = new ArrayList<>();
	private volatile List<DebuggerStateListener> debuggerStateListeners = new ArrayList<>();

	private volatile Hashtable<Object, ElementViewModel> elementLookupTable = new Hashtable<>();
	

	private volatile Hashtable<Object, List<Object>> elementLinks = new Hashtable<>();

	private DebuggerState state = DebuggerState.INITIALIZED;

	//FAILED events and interactions can't be cached
	//	private volatile HashMap<Event, EventViewModel> eventsLookupTable = new HashMap<>();
	//	private volatile HashMap<Interaction, InteractionViewModel> interactionLookupTable = new HashMap<>();

	private volatile List<InteractionViewModel> interactions = new ArrayList<>();

	private ECNODebuggerEngineController(){
	}

	public static IECNODebuggerEngineController getInstance(){
		if(instance == null){
			System.err.println("new instance");
			return (instance = new ECNODebuggerEngineController());
		}
		return instance;
	}

	public void destroy(){
		elements.clear();
		addedElements.clear();
		encounteredElements.clear();
		elementTypes.clear();
		eventTypes.clear();
		interactions.clear();
		this.engine.removeController(this);
		this.engine = null;
		notifyElementTypesChanged();
		notifyEventTypesChanged();
		changeState(DebuggerState.STOPPED);
		//		InspectionToolEngineController listener = getInstance();
		//		listener.setPropertiesView(propertiesView);
	}

	//	@SuppressWarnings("restriction")
	//	public IChoice checkChoice(IChoice choice){
	//		if(choice instanceof ParallelChoice){
	//			ParallelChoice pChoice = (ParallelChoice) choice;
	//			List<IChoice> choices = pChoice.getChoices();
	//			if(choices == null || choices.isEmpty()){
	//				System.err.println("pChoice is empty");
	//				return pChoice;
	//			}
	//			System.out.println("parallel choice: " + pChoice);
	//			for(IChoice c : choices){
	//				IChoice nC = checkChoice(c);
	//				for(IEventType et : nC.getEventTypes()){
	//					Event event = nC.getEvent(et);
	//					System.err.println("event: " + event);
	//				}
	//				return nC;
	//			}
	//		}else if(choice instanceof SimpleChoice){
	//			SimpleChoice sChoice = (SimpleChoice) choice;
	//			System.err.println("simple choice: " + sChoice);
	//			for(IEventType et : sChoice.getEventTypes()){
	//				Event event = sChoice.getEvent(et);
	//				System.err.println("event: " + event);
	//			}
	//			return sChoice;
	//		}
	//		else if(choice instanceof InheritingChoice){
	//			InheritingChoice iChoice = (InheritingChoice) choice;
	//			System.err.println("inheriting choice: " + iChoice);
	//			for(IEventType et : iChoice.getEventTypes()){
	//				Event event = iChoice.getEvent(et);
	//				System.err.println("event: " + event);
	//			}
	//			return choice;
	//		}
	//		else if(choice instanceof CompositeChoice){
	//			CompositeChoice cChoice = (CompositeChoice) choice;
	//			System.err.println("composite choice: " + cChoice);
	//			for(IEventType et : cChoice.getEventTypes()){
	//				Event event = cChoice.getEvent(et);
	//				System.err.println("event: " + event);
	//			}
	//			return cChoice;
	//		}
	//		System.err.println("choice: " + choice.getClass().getSimpleName());
	//		for(IEventType et : choice.getEventTypes()){
	//			Event event = choice.getEvent(et);
	//			System.err.println("event: " + event);
	//		}
	//		return choice;
	//	}

	private Lock elementLock = new ReentrantLock();
	private synchronized ElementViewModel generateElementNodes(Object element){
		elementLock.lock();
		if(element.toString().contains("Anton")) System.err.println("Anton node: " + element);
		
		boolean lookupTest = false;
		if((lookupTest = elementLookupTable.containsKey(element))) {
			if(element.toString().contains("Anton"))System.err.println("element already exists");
			elementLock.unlock();
			return elementLookupTable.get(element);
		}
		
		ObjectViewModel test = elementLookupTable.get(element);
		if(element.toString().contains("Anton")){
			System.err.println("lookup: " + test + ";" + lookupTest);
		}

		List<IEventType> eventTypes = ElementHelper.getEventTypes(engine, element);

		addEventTypes(eventTypes);
		IElementType elementType = engine.getElementType(element);
		addElementTypes(elementType);

		ElementViewModel newElement = new ElementViewModel(element, engine.getElementType(element));
		elementLookupTable.put(element, newElement);
		if(element.toString().contains("Anton")) System.err.println("Anton's viewmodel has been created");
		//		List<Object> newElementReferences = ElementHelper.getReferences(engine, eventTypes, element);
		//		newElement.setReferences(newElementReferences, elements);
		elementLock.unlock();
		return newElement;
	}

	private void addEventTypes(List<IEventType> list){
		boolean isDirty = false;
		for(IEventType e : list){
			if(!this.eventTypes.contains(e)){
				this.eventTypes.add(e);
				isDirty = true;
			}
		}
		if(isDirty){
			notifyEventTypesChanged();
		}
	}

	private void addElementTypes(IElementType... list ){
		boolean isDirty = false;
		for(IElementType e : list){
			if(!this.elementTypes.contains(e) && e != null){
				this.elementTypes.add(e);
				isDirty = true;
			}
		}
		if(isDirty){
			notifyElementTypesChanged();
		}
	}

	private void notifyEventTypesChanged(){
		for(IEventTypeListener l : this.eventTypeListeners){
			l.eventTypesChanged(this.eventTypes.toArray(new IEventType[0])); //TODO maybe it is necessary with a copy
		}
	}

	private void notifyElementTypesChanged(){
		for(IElementTypeListener l : this.elementTypeListeners){
			l.elementTypesChanged(this.elementTypes.toArray(new IElementType[0])); //TODO maybe it is necessary with a copy
		}
	}



	private void clearInteractionsAndRelatedItems(){
		for(InteractionViewModel interaction : this.interactions){
			for(EventViewModel event : interaction.getEvents()){
				this.elements.remove(event);
				for(GraphItemListener l : this.elementListener){
					l.eventAdded(event);
				}
			}
		}
		for(EventViewModel event : this.events){
			this.elements.remove(event);
			for(GraphItemListener l : this.elementListener) l.elementRemoved(event);
		}
		this.interactions.clear();
		this.events.clear();


	}

	private InteractionViewModel generateInteractionViewModel(Interaction interaction){
		List<ElementViewModel> elementViewModels = new ArrayList<>();
		List<EventViewModel> eventViewModels = new ArrayList<>();
		Hashtable<String, EventViewModel> eventLookup = new Hashtable<>();
		ElementViewModel triggerElementViewModel = generateElementNodes(interaction.getTriggerElement());
		EventViewModel triggerEventViewModel = new EventViewModel(interaction.getTriggerEvent());
		for(Object el : interaction.getElements()){
			elementViewModels.add(generateElementNodes(el));			
		}
		if(interaction.getLinks() != null){
			for(Link l : interaction.getLinks()){
				Event[] events = interaction.getEvents(l).toArray(new Event[0]);
				for(Event e : events){
					EventViewModel eventViewModel = eventLookup.get(e.getType().getName());
					if(eventViewModel == null) {
						eventViewModel = new EventViewModel(e);
						eventLookup.put(e.getType().getName(), eventViewModel);
						eventViewModels.add(eventViewModel);
					}
					ElementViewModel source = elementLookupTable.get(l.source);
					ElementViewModel target = elementLookupTable.get(l.target);
					if(!elementViewModels.contains(source)) elementViewModels.add(source);
					if(!elementViewModels.contains(target)) elementViewModels.add(target);

					eventViewModel.addReference(source);
					eventViewModel.addReference(target);

				}
			}
		}

		return new InteractionViewModel(interaction, elementViewModels, eventViewModels, triggerElementViewModel, triggerEventViewModel, interaction.getLabel());
	}

	public synchronized void calculateInteractions(){
		String intents1 = "\t";
		String intents2 = intents1 + "\t";
		String intents3 = intents2 + "\t";
		String intents4 = intents3 + "\t";
		long started = System.currentTimeMillis();
		System.err.println("calculating interactions");
		clearInteractionsAndRelatedItems();
		List<EventViewModel> eventViewModelsToAdd = new ArrayList<>();
		List<ObjectViewModel> copyElements = new ArrayList<>();
		copyElements.addAll(elements);
//		System.out.println("copied elements: " + copyElements.size());
		ElementHelper.resetNames();

		//		engine.getBehaviours().iterator().next().
		for(ObjectViewModel oViewModel : copyElements){
			Object element = oViewModel.getNode();
			Set<IEventType> eventTypesSet =  engine.getEventTypes(element);
//			System.out.println("element: " + element);
			if(eventTypesSet == null) continue;

			Iterator<IEventType> eventTypes = eventTypesSet.iterator();
			while(eventTypes.hasNext()){
				IEventType eventType = eventTypes.next();
				InteractionIterator interactionIterator = engine.getInteractions(element, eventType);
//				System.out.println(intents1 + "event type: " + eventType.getName());
				try {
					while(interactionIterator.hasNext()){
						Interaction interaction = interactionIterator.next();


						Collection<Link> linksSet = interaction.getLinks();

						List<EventViewModel> interactionEventViewModels = new ArrayList<>();
						List<ElementViewModel> interactionElementViewModels = new ArrayList<>();

//						System.out.println(intents2 + "interaction: " + interaction.getLabel());
						Collection<Object> interactionElements = interaction.getElements();


						if(interactionElements != null){
							for(Object o : interactionElements){
								ObjectViewModel ovm = elementLookupTable.get(o);

								//								for(ObjectViewModel ovm : this.elements){
								//									if(ovm.getNode().equals(o)){
								//										interactionElementViewModels.add((ElementViewModel) ovm);
								//
								//										break;
								//									}
								//								}
								if(ovm != null){
//									System.out.println(intents3 + "element: " + o);
									interactionElementViewModels.add((ElementViewModel) ovm);
								}else{
									System.err.println("element lookup failed");
								}

							}
						}
						if(linksSet != null){
							Iterator<Link> links = linksSet.iterator();
							while(links.hasNext()){
								Link link = links.next();

								if(!elementLinks.containsKey(link.source)){
									elementLinks.put(link.source, new ArrayList<>());									
								}
								if(!elementLinks.get(link.source).contains(link.target)){
									elementLinks.get(link.source).add(link.target);
								}

								Collection<Event> eventsSet = interaction.getEvents(link);
								if(eventsSet != null){
									Iterator<Event> events = eventsSet.iterator();
//									System.out.println(intents3 + "link: " + link.source + "-->" + link.target);
									while(events.hasNext()){
										Event event = events.next();

										EventViewModel eventViewModel = new EventViewModel(event);

										eventViewModel.addReference(elementLookupTable.get(link.source));
										eventViewModel.addReference(elementLookupTable.get(link.target));
										//										interaction.get
										//										engine.getev


										//										this.elements.add(eventViewModel);
										this.events.add(eventViewModel);
										eventViewModelsToAdd.add(eventViewModel);
										interactionEventViewModels.add(eventViewModel);
//										System.out.println(intents4 + "event: " + event);
									}
								}
							}
						}

						InteractionViewModel interactionViewModel = generateInteractionViewModel(interaction);
//								new InteractionViewModel(interaction, 
//										interactionElementViewModels, 
//										interactionEventViewModels,
//										generateElementNodes(interaction.getTriggerElement()),
//										interaction.getTriggerEvent(),
//										ElementHelper.getName(interaction));
						this.interactions.add(interactionViewModel);
					}
				} catch (InvalidStateException e) {
					System.err.println("invalid state exception: " + e.getMessage());
				}

			}
		}
		//		for(EventViewModel evm : eventViewModelsToAdd){
		//			this.elements.add(evm);
		//			for(GraphItemListener l : this.elementListener){
		//				l.eventAdded(evm);
		//			}
		//		}


		for(InteractionListener l : this.interactionListeners){
			l.interactionsUpdated(Collections.unmodifiableList(interactions));
		}

		for(ObjectViewModel elem : this.elements){
			if(elem instanceof ElementViewModel){
				((ElementViewModel) elem).setReferences(elementLinks.get(elem.getNode()), this.elements);
			}
		}


		System.err.println("finished calculating: " + (System.currentTimeMillis()-started) + "ms");

	}

	//	private void updateReferences(Interaction interaction){
	//		Collection<Link> links = interaction.getLinks();
	//		Collection<Object> elements = interaction.getElements();
	//		String label = interaction.getLabel();
	//
	//	}

//	}

	//	private List<Event> getEvents(Interaction interaction){
	//		Collection<Link> links = interaction.getLinks();
	//		for(Link l : links){
	//			Object obj = l.source;
	//		}
	//	}
	
	private Hashtable<ElementViewModel, InvalidChoice> invalidChoices = new Hashtable<>();
	@Override
	public void invalidChoice(IChoice choice, Interaction interaction, InvalidChoiceType type) {
		Object element = choice.getOwner().getElement();
		ElementViewModel key = elementLookupTable.get(element);
		invalidChoices.put(key, new InvalidChoice(key, choice.getEventTypes(), type));
		
//		choice.cond
		
//		choice.getEventTypes()
	}

	public synchronized void addElement(Object element) {
		ElementViewModel node = generateElementNodes(element);
		addedElements.add(node);
		elements.add(node);

		for(GraphItemListener l : this.elementListener){
			l.elementAdded(node);
		}
	}

	public synchronized void removeElement(Object element) {
		ObjectViewModel toRemove = elementLookupTable.remove(element);
		if(toRemove == null) return;


		if(addedElements.remove(toRemove))
			System.out.println("added element removed: " + toRemove);
		if(encounteredElements.remove(toRemove))
			System.out.println("encountered element removed: " + toRemove);

		elements.remove(toRemove);

		//		this.graphView.update();
	}

	public synchronized void elementEncountered(Object element) {
		ElementViewModel node = generateElementNodes(element);
		encounteredElements.add(node);
		elements.add(node);
		for(GraphItemListener l : elementListener){
			l.elementEncountered(node);
		}

	}

	public List<ObjectViewModel> getElements(){
		return Collections.unmodifiableList(this.elements);
	}

	//	private InteractionViewModel getInteractionViewModel(Interaction interaction){
	//		Collection<Link> links = interaction.getLinks();
	//		List<IEventType> eventTypes = new ArrayList<>();
	//		for(Link l : links){
	//			Collection<Event> events = interaction.getEvents(l);
	//			for(Event e : events){
	//				e.getType().getFormalParametersList();
	////				e.get
	//				if(!eventTypes.contains(e.getType())){
	//					eventTypes.add(e.getType());
	//					engine.
	//				}
	//			}
	//		}
	//
	////		for(Object e : interaction.getElements()){
	////			List<Object> refs = ElementHelper.getReferences(engine, eventTypes, e);
	////			new ElementViewModel(e).setReferences(references, items);
	////			new EventVi
	////		}
	//		
	//		return null;
	//	}

	public void dispose() {
		System.err.println("disposing engine listener");
		this.engine.removeController(this);

	}

	public void setEngine(ExecutionEngine engine){
		if(this.engine != null) {
			destroy();
		}
		this.engine = engine;
		this.engine.addController(this);
		this.engine.attachDebugger(this);
		changeState(DebuggerState.STARTED);

		//TODO Find out if this is necessary and if result is the same
		//		Iterator<IElementBehaviour> it = this.engine.getBehaviours().iterator();
		//		while(it.hasNext()){
		//			Object el = it.next().getElement();
		//			addElement(el);
		//		}
	}

	private void changeState(DebuggerState state){
		this.state = state;
		for(DebuggerStateListener l : this.debuggerStateListeners){
			l.stateChanged(state);
		}
	}
	
	public List<Interaction> getInteractions(Object element, IEventType eventType){
		List<Interaction> list = new ArrayList<>();
		InteractionIterator iterator = engine.getInteractions(element, eventType);
		try {
		while(iterator.hasNext())list.add(iterator.next());
			} catch (InvalidStateException e) {
				System.err.println("invalid state exception");
			}

		return 	iterator.getDebugInteractions();
		
	}

	public synchronized void debug(final Interaction interaction){
		try {
			System.err.println("breaking...");
			if(latch != null)
				latch.await();
			System.err.println("continue from breakpoint");
		} catch (InterruptedException e) {
			System.err.println("latch error: " + e.getMessage());
		}
		//		new Thread(new Runnable() {
		//			
		//			@Override
		//			public void run() {

		boolean isBreakpoint = false;
		for(IBreakpoint breakpoint : breakpoints){
			if(breakpoint.isBreakpoint(interaction)){
				isBreakpoint = true;
				break;
			}
		}

		if(isBreakpoint){
			if(breakpointListener != null){
				for(IBreakpointListener l : breakpointListener){

					l.breakpointHit(generateInteractionViewModel(interaction));
				}
			}

			latch = new CountDownLatch(1);
		}
		//			}
		//		}).start();
	}


	public void addBreakpoint(IBreakpoint breakpoint){
		this.breakpoints.add(breakpoint);
	}

	public void removeBreakpoint(IBreakpoint breakpoint){
		this.breakpoints.remove(breakpoint);
	}

	/**
	 * if on a breakpoint use this method to go to next interaction execution.
	 */
	public void continueFromBreakpoint(){
		latch.countDown();
	}

	/*
	 * debugger methods
	 */

	public List<IEventType> getEventTypes(){
		List<IEventType> eventTypes = new ArrayList<>();
		for(IElementType e : getElementTypes()){
			Set<IEventType> types = this.engine.getEventTypes(e);
			Iterator<IEventType> iterator = types.iterator();
			while(iterator.hasNext()){
				eventTypes.add(iterator.next());
			}
		}
		return eventTypes;
	}
	
	public IEventType[] getEventTypes(Object element){
		IElementType elType = engine.getElementType(element);
		Set<IEventType> set = engine.getEventTypes(elType);
		return set.toArray(new IEventType[0]);
		
	}

	public List<IElementType> getElementTypes(){
		Collection<IElementBehaviour> behaviours = engine.getBehaviours();
		Iterator<IElementBehaviour> iterator = behaviours.iterator();
		List<IElementType> items = new ArrayList<>();
		while(iterator.hasNext()){
			IElementType type = iterator.next().getElementType();
			items.add(type);
		}
		return items;
	}

	public boolean isElementType(Object element, IElementType elementType){
		IElementType typeToMatch = engine.getElementType(element);
		if(typeToMatch == null) return false;
		if(elementType == null) return false;
		if(typeToMatch.getName().equals(elementType.getName())) return true;
		return false;
	}

	@Override
	public void addElementListener(GraphItemListener listener) {
		this.elementListener.add(listener);
		for(ObjectViewModel o : this.encounteredElements) listener.elementEncountered(o);
		for(ObjectViewModel o : this.addedElements) listener.elementAdded(o);
		//		for(ObjectViewModel o : this.events) listener.eventAdded(o);
	}

	@Override
	public void addInteractionListener(InteractionListener listener) {
		this.interactionListeners.add(listener);
		listener.interactionsUpdated(interactions);
	}

	@Override
	public void addElementTypeListener(IElementTypeListener listener) {
		Assert.isNotNull(listener, "IElementTypeListener in " + this.getClass().getName() + " should not be null");
		this.elementTypeListeners.add(listener);
		if(!this.elementTypes.isEmpty()){
			listener.elementTypesChanged(elementTypes.toArray(new IElementType[0]));
		}
	}

	@Override
	public void addEventTypeListener(IEventTypeListener listener) {
		Assert.isNotNull(listener, "IEventTypeListener in " + this.getClass().getName() + " should not be null");
		this.eventTypeListeners.add(listener);
		if(!this.eventTypes.isEmpty()){
			listener.eventTypesChanged(eventTypes.toArray(new IEventType[0]));
		}
	}

	public void addBreakpointListener(IBreakpointListener listener){
		Assert.isNotNull(listener);
		breakpointListener.add(listener);
	}

	public void addDebuggerStateListener(DebuggerStateListener listener) {
		this.debuggerStateListeners.add(listener);
		listener.stateChanged(state);
	};

	@Override
	public void removeEventTypeListener(IEventTypeListener listener) {
		this.eventTypeListeners.remove(listener);
	}

	@Override
	public void removeElementListener(GraphItemListener listener) {
		this.elementListener.remove(listener);
	}

	@Override
	public void removeInteractionListener(InteractionListener listener) {
		this.interactionListeners.remove(listener);
	}

	public void removeBreakpointListener(IBreakpointListener listener){
		this.breakpointListener.remove(listener);
	}

	public void removeElementTypeListener(IElementTypeListener listener){
		this.elementTypeListeners.remove(listener);
	}

	@Override
	public void removeDebuggerStateListener(DebuggerStateListener listener) {
		this.debuggerStateListeners.add(listener);
	}


}