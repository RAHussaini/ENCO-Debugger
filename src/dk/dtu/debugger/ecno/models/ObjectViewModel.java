package dk.dtu.debugger.ecno.models;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import dk.dtu.debugger.ecno.figures.EFigure;
import dk.dtu.debugger.ecno.figures.Shapeable;


public abstract class ObjectViewModel implements Shapeable, IWorkbenchAdapter,IAdaptable{
	
	protected boolean highlight = false;
	protected Object node = null;
	protected Object[] children = null;
	protected EFigure shape;
	protected String name = null;
	
	
	public String getListName(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		int size = (Integer) obj.getClass().getMethod("size").invoke(obj);
		String listName = "";
		for(int i = 0; i < size; i++){
			Object element = obj.getClass().getMethod("get", new Class[]{int.class}).invoke(obj, i);
			listName += element.getClass().getMethod("getName").invoke(element) + ";";
		}
		return listName;
	}


	public String getName(){
		return this.name;
	}
	
	public Object getNode(){
		return this.node;
	}
	
	public void setChildren(List<ObjectViewModel> children){
		this.children = children.toArray();
		
	}
	
	public Object[] getChildren(){
		return children;
	}
	
	public void highlight(boolean highlight){
		if(this.getShape() == null) {
			System.err.println("shape is null for: " + this.getName());
			return;
		}
		if(highlight)getShape().highlight();
		else getShape().unHighlight();
		this.highlight = highlight;
	}
	
	public boolean isHighlighted(){
		return this.highlight;
	}

	@Override
	public EFigure getShape() {
		if(this.shape == null) 
		System.err.println("shape is null: " + name);
		return this.shape;
	}

	@Override
	public void setShape(EFigure figure) {
//		System.out.println("setting shape: " + figure);
		
		System.out.println("Test");
		this.shape = figure;
	}
	
	@Override
	public String getLabel(Object o) {
		return this.name;
	}
	
	// ignore these
	@Override
	public Object[] getChildren(Object o) {
		return null;
	}
	@Override
	public Object getParent(Object o) {
		return null;
	}
	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}
	
	@Override
	public String toString() {
		return getName() + ";isHighlighted: " + isHighlighted() + ";shape:" + shape;
	}
}
