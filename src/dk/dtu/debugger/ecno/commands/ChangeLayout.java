package dk.dtu.debugger.ecno.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

import dk.dtu.debugger.ecno.views.DebugView;
import dk.dtu.debugger.ecno.views.DebugView.LayoutType;

public class ChangeLayout extends AbstractHandler {

	private String currentValue;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String parm = event.getParameter(RadioState.PARAMETER_ID); 
		if (parm.equals(currentValue)) {
			return null;
		}
		HandlerUtil.updateRadioState(event.getCommand(), parm);

		currentValue = parm;
		LayoutType type = LayoutType.valueOf(currentValue);
		if(type != null){
			IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(DebugView.ID);//.findView(DebugView.ID);
			if(part instanceof DebugView){
				((DebugView) part).applyLayout(type);
			}

		}
			
		// update our radio button states ... get the service from
		// a place that's most appropriate
		ICommandService service = (ICommandService) HandlerUtil
				.getActiveWorkbenchWindowChecked(event).getService(
						ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		return null;
	}

}
