package dk.dtu.debugger.ecno;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "dk.dtu.debugger.ecno"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 * The start method has been a convenient place to implement initialization and registration behavior for a plug-in
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//If you create and schedule a simple job, its run method will be called outside the UI thread.
		//If you want to schedule a job that accesses UI widgets, you should subclass org.eclipse.ui.progress.UIJob instead of the base Job class.
		
		UIJob job = new UIJob("InitCommandsWorkaround") {			
			//Run the job in the UI Thread.
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ICommandService.class);
				Command command = commandService.getCommand("dk.dtu.debugger.ecno.commands.changeLayout.layouts");
				command.isEnabled();
				return new Status(IStatus.OK, PLUGIN_ID, "Init commands doing job performed succesfully" );
			}
			};
			job.schedule();
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 * @author Rus
	 */
	public void stop(BundleContext context) throws Exception {
			
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
