package dk.dtu.debugger.ecno.models;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

import dk.dtu.debugger.ecno.properties.ElementProperties;
import dk.dtu.imm.se.ecno.core.IElementType;

public class ElementViewModel extends ObjectViewModel {

	private static Hashtable<String, Integer> counters = new Hashtable<>();
	private final List<ObjectViewModel> references;
	private IElementType elementType;
	
	public ElementViewModel(Object element, IElementType elementType){
		this.references = new ArrayList<>();
		this.elementType = elementType;
		
		try{
			Method nameMeth = element.getClass().getMethod("getName");
			if(nameMeth != null)
				this.name = nameMeth.invoke(element).toString();
		}catch(Exception e){
			System.err.println("no getName method found for: " + element.getClass().getSimpleName());
		}
		if(this.name == null){
			this.name = getDefaultElementName(element);
		}
		this.name += ":" + (elementType != null ? elementType.getName() : element.getClass().getSimpleName());
		this.node = element;
	}

	private String getDefaultElementName(Object element){
		String name = element.getClass().getSimpleName();
		int no = 0;
		if(counters.containsKey(name)){
			no = counters.get(name)+1;
		}
		counters.put(name, no);
		return name + no;
	}

	//	public void addReference(Object reference){
	//		if(!references.contains(reference)){
	//			this.references.add(reference);
	//			//			reference.addReference(this);
	//		}
	//	}
	private final List<Object> refs = new ArrayList<>();
	private final List<ObjectViewModel> items = new ArrayList<>();
	public void setReferences(List<Object> references, List<ObjectViewModel> items){
		this.refs.clear();
		this.items.clear();
		if(references != null)this.refs.addAll(references);
		if(items != null)this.items.addAll(items);
	}

	public Object[] getReferences(){
		if(this.references.size() != this.refs.size()){
			List<ObjectViewModel> newReferences = new ArrayList<>();
			if(this.items == null) return new Object[0];
			for(ObjectViewModel el : this.items){
				if(this.refs.contains(el.getNode())){
					newReferences.add(el);
				}
			}
			this.references.clear();
			this.references.addAll(newReferences);
		}
		return this.references.toArray();
	}

	public boolean hasReference(Object element){
		for(ObjectViewModel r : references){
			if(r.getNode().equals(element)) return true;
		}
		for(Object o : this.refs){
			if(o.equals(element)) return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return (T) this;
		if(adapter == IPropertySource.class){
			return (T) new ElementProperties(this);
		}
		return null;
	}

}
