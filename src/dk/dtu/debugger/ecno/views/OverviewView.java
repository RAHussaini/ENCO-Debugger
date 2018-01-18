package dk.dtu.debugger.ecno.views;

import java.util.ArrayList;



import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import dk.dtu.debugger.ecno.listeners.InteractionListener;
import dk.dtu.debugger.ecno.models.InteractionViewModel;
import dk.dtu.debugger.ecno.models.ObjectViewModel;
import dk.dtu.debugger.ecno.models.ParameterViewModel;
import dk.dtu.debugger.ecno.utils.ComboExt;
import dk.dtu.debugger.ecno.controllers.ECNODebuggerEngineController;
//************************************
import static dk.dtu.debugger.ecno.utils.ViewsUtil.*;
import static dk.dtu.debugger.ecno.utils.ViewsUtil.applyGridLayout;
//******************************************



public class OverviewView extends ViewPart implements SelectionListener, InteractionListener {
	
	public static final String ID = "dk.dtu.debugger.ecno.views.overviewView";

	private Group parameterGroup;
	private List<ParameterViewModel> parameters = new ArrayList<>();
	private final List<Control> parameterControls = new ArrayList<>();

	private ComboExt<InteractionViewModel> interactionsCombo;
	private ComboExt<ObjectViewModel> graphItemsCombo;

	public void createPartControl(Composite parent) {

		FillLayout parentLayout = new FillLayout();
		parentLayout.type= SWT.VERTICAL;
		parentLayout.marginHeight = 10;
		parentLayout.marginWidth = 5;
		parent.setLayout(parentLayout);

		Composite grid = new Composite(parent, SWT.ALL);
		applyGridLayout(grid, 2);

		final Label interactionsLabel = new Label(grid, SWT.LEFT);
		applyGridLayoutData(interactionsLabel, 1);


		interactionsCombo = new ComboExt<>(grid,SWT.READ_ONLY);
		applyGridLayoutData(interactionsCombo, 1);
		interactionsCombo.setLayout(new FillLayout());

		final Label graphItemsLabel = new Label(grid, SWT.CENTER);
		applyGridLayoutData(graphItemsLabel, 1);


		graphItemsCombo = new ComboExt<>(grid,  SWT.READ_ONLY);
		applyGridLayoutData(graphItemsCombo, 1);
		graphItemsCombo.setLayout(new FillLayout());

		parameterGroup = new Group(grid, SWT.SHADOW_IN);
		applyGridLayout(parameterGroup, 2);
		parameterGroup.setText("Parameters");
		applyGridLayoutData(parameterGroup, 2);
		parameterGroup.setVisible(false);

		interactionsCombo.addSelectionListener(this);
		graphItemsCombo.addSelectionListener(this);


		interactionsCombo.setText("(none)");
		graphItemsCombo.setText("(none)");
		interactionsLabel.setText("Select interaction: ");
		graphItemsLabel.setText("select graphitem:");

		Button refreshGraphItemsBtn = new Button(grid, SWT.PUSH);
		refreshGraphItemsBtn.setText("Refresh graph item list");
		applyGridLayoutData(refreshGraphItemsBtn, 2);
		refreshGraphItemsBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				System.out.println("refresh graphitems...");
				new Thread(new Runnable() {
					@Override
					public void run() {
						updateGraphItemValues();
					}
				}).start();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
//				System.out.println("default refresh graphitems selected...");
				// TODO Auto-generated method stub

			}
		});

		Button continueBtn = new Button(grid, SWT.PUSH);
		applyGridLayoutData(continueBtn, 2);
		continueBtn.setText("Continue");
		continueBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						ECNODebuggerEngineController.getInstance().continueFromBreakpoint();
					}
				}).start();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		Button calculateInteractionsBtn = new Button(grid, SWT.NONE);
		applyGridLayoutData(calculateInteractionsBtn, 2);
		calculateInteractionsBtn.setText("Calculate Interactions");
		calculateInteractionsBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						ECNODebuggerEngineController.getInstance().calculateInteractions();	
					}
				}).start();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		parent.layout();
		grid.layout();
		
		ECNODebuggerEngineController.getInstance().addInteractionListener(this);
	}
	
	private synchronized void updateGraphItemValues(){
		List<ObjectViewModel> elements = ECNODebuggerEngineController.getInstance().getElements();
			String[] keys = new String[elements.size()];
			List<ObjectViewModel> data = new ArrayList<>();
			for(int i = 0; i < keys.length; i++){
				keys[i] = elements.get(i).getName();
				data.add(elements.get(i));
			}
			setGraphItemValues(data, keys);
		}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		this.interactionsCombo.setFocus();
	}

	public void setInteractionValues(final List<InteractionViewModel> data, final String[] keys){
		Display.getDefault().asyncExec(new Runnable() {			
			@Override
			public void run() {
				interactionsCombo.setComboValues(data, keys);
			}
		});
	}

	public void setGraphItemValues(final List<ObjectViewModel> data,final String[] keys){
		Display.getDefault().asyncExec(new Runnable() {	
			@Override
			public void run() {
				graphItemsCombo.setComboValues(data, keys);
			}
		});
		
	}

	public void addParameters(List<ParameterViewModel> parameters, SelectionListener submitListener){

		clearParameters();

		if(parameters.size() > 0){
			parameterGroup.setVisible(true);
		}else{
			parameterGroup.setVisible(false);
			return;
		}

		this.parameters = parameters;

		for(ParameterViewModel parameter : parameters){
//			if(parameter.getOptionKeys() != null)
//				System.out.println("size: " + parameter.getOptionKeys().size());
			//adding label for parameter
			Label label = new Label(parameterGroup, SWT.NONE);
			parameterControls.add(label);
			applyGridLayoutData(label, 1);
			label.setText(parameter.getName());

			// assume we want a String argument if no values
			if(parameter.getSelectionOptions() == null){
				System.out.println("adding textbox");
				Text text = new Text(parameterGroup, SWT.SINGLE);
				applyGridLayoutData(text, 1);
				text.addModifyListener(parameter);
				parameterControls.add(text);
			}else{
				ComboExt<Object> options = new ComboExt<>(parameterGroup, SWT.READ_ONLY);
				applyGridLayoutData(options, 1);
				options.setComboValues(parameter.getSelectionOptions(), parameter.getOptionKeys().toArray(new String[0]));
				parameterControls.add(options);
				options.addSelectionListener(parameter);
			}
		}

		Button button = new Button(parameterGroup, SWT.PUSH);
		applyGridLayoutData(button, 2);
		button.setText("Select");
		button.addSelectionListener(submitListener);
		parameterControls.add(button);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				parameterGroup.layout();
				parameterGroup.getParent().layout();
			}
		});
	}

	public List<ParameterViewModel> getParameters(){
		return this.parameters;
	}
	public void clearParameters(){
		for(Control c : this.parameterControls){
			c.dispose();
		}
		parameterGroup.setVisible(false);
	}



	private DebugView getGraphView(){
		IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(DebugView.ID);
		if (part != null && part instanceof DebugView) {
			DebugView debugView = (DebugView) part;		
			return debugView;
		}
		return null;
	}
	@Override
	public void widgetSelected(SelectionEvent e) {
//		System.out.println("widget selected: " + e.getSource());
		InteractionViewModel selectedInteraction = this.interactionsCombo.getComboValue(this.interactionsCombo.getText());
		
		if(selectedInteraction != null && getGraphView() != null)
			getGraphView().itemSelected(selectedInteraction, interactionsCombo);
//		ObjectViewModel selectedGraphItem = this.graphItemsCombo.getComboValue(this.graphItemsCombo.getText());
//		if(selectedGraphItem != null && getGraphView() != null)
//			getGraphView().itemSelected(selectedGraphItem, this.graphItemsCombo);
//		
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
//		System.out.println("widget default selected: " + e.getSource());
		InteractionViewModel selected = this.interactionsCombo.getComboValue(this.interactionsCombo.getText());
		if(selected != null && getGraphView() != null)
			getGraphView().itemSelected(selected, interactionsCombo);

//		ObjectViewModel selectedGraphItem = this.graphItemsCombo.getComboValue(this.graphItemsCombo.getText());
//		if(selectedGraphItem != null){
//			for(IComboListener<ObjectViewModel, ComboExt<ObjectViewModel>> c : this.graphItemsListener) c.itemSelected(selectedGraphItem, this.graphItemsCombo);
//		}
	}

	@Override
	public void interactionsUpdated(List<InteractionViewModel> interactions) {
		String[] keys = new String[interactions.size()];
		int index = 0;
		for(InteractionViewModel interaction : interactions){
			keys[index++] = interaction.getName();
		}
		
		setInteractionValues(interactions, keys);
	}
}


