package dk.dtu.debugger.ecno.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import dk.dtu.debugger.ecno.views.DebugView;

public class ShowAddedElementsCmd  extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
    		boolean show = !HandlerUtil.toggleCommandState(event.getCommand()); // returns old value, therefore negate
    		
    		DebugView view = getView();
//        	System.out.println("show added: " + show);
        	if(view != null) view.showAddedElements(show);
    		
        return null;
    }
    
    private DebugView getView(){
    	IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(DebugView.ID);
        		if(view != null && view instanceof DebugView){
        			return (DebugView) view;
        		}
        		return null;
    }
}