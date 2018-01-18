package dk.dtu.debugger.ecno.utils;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import dk.dtu.debugger.ecno.models.ObjectViewModel;

public class HideEncounteredElementsFilter extends ViewerFilter {

//	private final List<ObjectViewModel> addedElements;
//	private final List<ObjectViewModel> encounteredElements;
	
	private final List<ObjectViewModel> elements;
	
//	public HideEncounteredElementsFilter(List<ObjectViewModel> addedElements, List<ObjectViewModel> encounteredElements) {
//		this.addedElements = addedElements;
//		this.encounteredElements = encounteredElements;
//		
//	}
	
	public HideEncounteredElementsFilter(List<ObjectViewModel> elements) {
		this.elements = elements;
	}
//	@Override
//	public boolean select(Viewer viewer, Object parentElement, Object element) {
//		return !(!addedElements.contains(element) && encounteredElements.contains(element));
//	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !elements.contains(element);
	}
	

}