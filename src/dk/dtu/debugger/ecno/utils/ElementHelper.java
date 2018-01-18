package dk.dtu.debugger.ecno.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dk.dtu.imm.se.ecno.core.ICoordinationSet;
import dk.dtu.imm.se.ecno.core.IElementBehaviour;
import dk.dtu.imm.se.ecno.core.IElementType;
import dk.dtu.imm.se.ecno.core.IEventType;
import dk.dtu.imm.se.ecno.core.IFormalParameter;
import dk.dtu.imm.se.ecno.core.IPackageAdapter;
import dk.dtu.imm.se.ecno.core.ISynchronisation;
import dk.dtu.imm.se.ecno.engine.ExecutionEngine;
import dk.dtu.imm.se.ecno.runtime.Event;
import dk.dtu.imm.se.ecno.runtime.Interaction;
import dk.dtu.imm.se.ecno.runtime.Parameter;

public class ElementHelper {

	//	public static List<Element> getElements(){
	//
	//
	//	}
	public static List<Parameter> getParameters(Event event){
		List<IFormalParameter> formalParameters = event.getType().getFormalParametersList();
		List<Parameter> parameters = new ArrayList<>();
		for(IFormalParameter fp : formalParameters){
			parameters.add(event.getParameter(fp));
		}
		return parameters;
	}

	public static List<IEventType> getEventTypes(ExecutionEngine engine, Object element){
		Iterator<IEventType> eventTypesIterator;
		Set<IEventType> eventTypeSet = engine.getEventTypes(element);
		List<IEventType> eventTypesList = new ArrayList<>();
		if(eventTypeSet != null){
			eventTypesIterator = engine.getEventTypes(element).iterator();

			// get all events for element
			while(eventTypesIterator.hasNext()){
				IEventType eventType = eventTypesIterator.next();
				//				boolean exist = false;
				//				for(IEventType e : eventTypesList){
				//					if(e.g)
				//				}
				if(!eventTypesList.contains(eventType))eventTypesList.add(eventType);
//				else System.out.println("duplicate found");
			}
		}
		return eventTypesList;
	}

	public static List<IEventType> getAllEventTypes(ExecutionEngine engine){
		List<IEventType> eventTypes = new ArrayList<>();
		Iterator<IElementBehaviour> behaviours = engine.getBehaviours().iterator();
		List<IElementType> elementTypes = new ArrayList<>();
		while(behaviours.hasNext()){
			IElementType type = behaviours.next().getElementType();
			elementTypes.add(type);
		}
//		System.out.println("start");
		for(IElementType t : elementTypes){
//			System.out.println("element type: " + t.getName() + " hash: " + t);
			Set<IEventType> set = engine.getEventTypes(t);
			eventTypes.addAll(set);
		}
//		System.out.println("startin verification");
//		for(IEventType e : eventTypes){
//			System.out.println("event type: " + e.getName() + " hash: " + e);
//		}
//		System.out.println("ending verification");
//		System.out.println("finish");
		return eventTypes;
	}
	
	private static Hashtable<String, Integer> nameCounters = new Hashtable<>();
	public static String getName(Object element){
		String name = null;
		try {
			Method m = element.getClass().getMethod("getName");
			name = m.invoke(element).toString();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			name = element.getClass().getSimpleName();
		}
		Integer no = nameCounters.get(name);
		no = no == null ? 0 : no+1;
		nameCounters.put(name, no);
		return name + " " + no;
	}
	
	public boolean validateName(String name){
		return !nameCounters.containsKey(name);
	}
	
	public static String getName(Interaction interaction){
		String name = interaction.getLabel();
		Integer no = nameCounters.get(name);
		no = no == null ? 0 : no+1;
		nameCounters.put(name, no);
		return name + " " + no;
	}
	
	public static void resetNames(){
		nameCounters.clear();
	}
	
	public static List<Object> getReferences(ExecutionEngine engine, List<IEventType> eventTypes, Object element){
		//		engine.getElementType(element)
		//		.getCoordinationSetsList()
		//		.get(0).getSynchonisationsList()
		//		.get(0).getReference()
		//		.getSourceElementType();
		if(eventTypes == null || eventTypes.isEmpty()) return new ArrayList<>();
		List<Object> references = new ArrayList<>();

		for(IEventType eventType : eventTypes){
			IPackageAdapter eventPackageAdapter = engine.getPackageAdapter(eventType);
			if(eventPackageAdapter == null) continue;
			IElementType elementType = eventPackageAdapter.getElementType(element);
			if(elementType == null) continue;
			List<ICoordinationSet> coSets = elementType.getCoordinationSetsList();
			//			List<Object> syncSets = engine.getPackageAdapter(eventType).getLinks(obj, coSets.get(0).getSynchonisationsList().get(0));
			IPackageAdapter adapter = engine.getPackageAdapter(element);

			if(adapter != null){
				for(ICoordinationSet cs : coSets){
					List<ISynchronisation> syncs = cs.getSynchonisationsList();
					
					for(ISynchronisation s : syncs){
//						s.getEvent().get
						
						if(s.getEvent().getName().equals(eventType.getName())){
							List<Object> links = adapter.getLinks(element, s);
							if(links != null){
								for(Object o : links){
									if(o != null)
										references.add(o);
								}
							}
						}
					}
				}	
			}
		}
//		if(references.size() > 0) System.out.println("found " + references.size() + " references");
		return references;
	}
	
//	public static List<IElementType> getReferenceElementTypes(ExecutionEngine engine, List<IEventType> eventTypes, Object element){
//		if(eventTypes == null || eventTypes.isEmpty()) return new ArrayList<>();
//		List<IReference> references = new ArrayList<>();
//
//		for(IEventType eventType : eventTypes){
//			IPackageAdapter eventPackageAdapter = engine.getPackageAdapter(eventType);
//			if(eventPackageAdapter == null) continue;
//			IElementType elementType = eventPackageAdapter.getElementType(element);
//			if(elementType == null) continue;
//			List<ICoordinationSet> coSets = elementType.getCoordinationSetsList();
//			//			List<Object> syncSets = engine.getPackageAdapter(eventType).getLinks(obj, coSets.get(0).getSynchonisationsList().get(0));
//			IPackageAdapter adapter = engine.getPackageAdapter(element);
//
//			if(adapter != null){
//				for(ICoordinationSet cs : coSets){
//					List<ISynchronisation> syncs = cs.getSynchonisationsList();
//					
//					for(ISynchronisation s : syncs){
//						references.add(s.getReference());
//						
//						if(s.getEvent().getName().equals(eventType.getName())){
//							List<Object> links = adapter.getLinks(element, s);
//							if(links != null){
//								for(Object o : links){
//									if(o != null)
//										references.add(o);
//								}
//							}
//						}
//					}
//				}	
//			}
//		}
//		if(references.size() > 0) System.out.println("found " + references.size() + " references");
//		return references;
//	}
}
