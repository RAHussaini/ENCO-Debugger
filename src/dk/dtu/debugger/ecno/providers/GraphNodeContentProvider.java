package dk.dtu.debugger.ecno.providers;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import dk.dtu.debugger.ecno.models.ElementViewModel;
import dk.dtu.debugger.ecno.models.EventViewModel;


public class GraphNodeContentProvider 
implements IGraphEntityContentProvider{

	@Override
	public Object[] getConnectedTo(Object entity) {
		if(entity instanceof ElementViewModel){
			ElementViewModel node = (ElementViewModel) entity;
			Object[] references =  node.getReferences();
			return references;
		}
		if(entity instanceof EventViewModel){
			EventViewModel node = (EventViewModel) entity;
			
			Object[] references = node.getReferences();
			return references;
		}
		//                if (entity instanceof MyNode) {
			//                        MyNode node = (MyNode) entity;
		//                        return node.getConnectedTo().toArray();
		//                }
		throw new RuntimeException("Type not supported");
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

//		super.inputChanged(viewer, oldInput, newInput);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
        if (inputElement instanceof Collection) {
			return ((Collection) inputElement).toArray();
		}
        return new Object[0];
	}


}


