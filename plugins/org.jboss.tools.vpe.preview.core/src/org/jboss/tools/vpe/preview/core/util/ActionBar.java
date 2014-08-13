package org.jboss.tools.vpe.preview.core.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.jboss.tools.vpe.preview.core.Activator;

public class ActionBar {
	private static final String GROUP_REFRESH = "org.jboss.tools.vpv.refresh"; //$NON-NLS-1$
	private static final String REFRESH_ON_SAVE_ENABLEMENT = "org.jboss.tools.vpe.enableRefreshOnSave"; //$NON-NLS-1$

	private IAction refreshAction;
	private IAction openInDefaultBrowserAction;
	private IAction enableAutomaticRefreshAction;
	private IAction enableRefreshOnSaveAction;
	
	private IExecutionListener saveListener;
	private boolean enableAutomaticRefresh;
	private Browser browser;
	
	private IPreferenceStore preferences;
	
	private Command saveCommand;
	private Command saveAllCommand;
	
	public ActionBar(Browser browser1, IPreferenceStore preferences) {
		this.browser = browser1;
		this.preferences = preferences;
		enableAutomaticRefresh = !preferences.getBoolean(REFRESH_ON_SAVE_ENABLEMENT);
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		saveCommand = commandService.getCommand("org.eclipse.ui.file.save"); //$NON-NLS-1$
		saveAllCommand = commandService.getCommand("org.eclipse.ui.file.saveAll"); //$NON-NLS-1$
		saveListener = new IExecutionListener() {

			@Override
			public void postExecuteSuccess(String arg0, Object arg1) {
				refresh(browser);
			}

			@Override
			public void notHandled(String arg0, NotHandledException arg1) {
			}

			@Override
			public void postExecuteFailure(String arg0, ExecutionException arg1) {
			}

			@Override
			public void preExecute(String arg0, ExecutionEvent arg1) {
			}

		};
	}
	
	public void fillLocalToolBar(IToolBarManager manager) {
		makeActions();
		
		manager.add(refreshAction);
		manager.add(openInDefaultBrowserAction);
		manager.add(new Separator(GROUP_REFRESH));
		manager.appendToGroup(GROUP_REFRESH, enableAutomaticRefreshAction);
		manager.appendToGroup(GROUP_REFRESH, enableRefreshOnSaveAction);
	}
	
	private void makeActions() {
		makeRefreshAction();
		makeOpenInDefaultBrowserAction();
		makeEnableAutomaticRefreshAction();
		makeEnableRefreshOnSaveAction();
	}
	
	protected void refresh(Browser browser) {
		String url = browser.getUrl();
		if (PlatformUtil.isWindows()) {
			url = NavigationUtil.removeAnchor(url);
		}
		browser.setUrl(url);
	}

	private void makeEnableAutomaticRefreshAction() {
		enableAutomaticRefreshAction = new Action(Messages.VpvView_ENABLE_AUTOMATIC_REFRESH, IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				if (enableAutomaticRefreshAction.isChecked()) {
					enableAutomaticRefresh = true;

					preferences.setValue(REFRESH_ON_SAVE_ENABLEMENT, false);
					enableRefreshOnSaveAction.setChecked(false);
					saveCommand.removeExecutionListener(saveListener);
					saveAllCommand.removeExecutionListener(saveListener);
				} else {
					enableAutomaticRefresh = false;
				}
			}
		};

		enableAutomaticRefreshAction.setChecked(!preferences.getBoolean(REFRESH_ON_SAVE_ENABLEMENT));
		enableAutomaticRefreshAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh_on_change.png")); //$NON-NLS-1$
	}

	private void makeEnableRefreshOnSaveAction() {
		enableRefreshOnSaveAction = new Action(Messages.VpvView_ENABLE_REFRESH_ON_SAVE, IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				if (enableRefreshOnSaveAction.isChecked()) {
					saveCommand.addExecutionListener(saveListener);
					saveAllCommand.addExecutionListener(saveListener);
					
					preferences.setValue(REFRESH_ON_SAVE_ENABLEMENT, true);
					enableAutomaticRefreshAction.setChecked(false);
					enableAutomaticRefresh = false;
				} else {
					saveCommand.removeExecutionListener(saveListener);
					saveAllCommand.removeExecutionListener(saveListener);
				}
			}
		};

		enableRefreshOnSaveAction.setChecked(preferences.getBoolean(REFRESH_ON_SAVE_ENABLEMENT));
		enableRefreshOnSaveAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh_on_save.png")); //$NON-NLS-1$
	}

	private void makeOpenInDefaultBrowserAction() {
		openInDefaultBrowserAction = new Action() {
			public void run() {
				URL url;
				try {
					url = new URL(browser.getUrl()); // validate URL (to do not open 'about:blank' and similar)
					Program.launch(url.toString());
				} catch (MalformedURLException e) {
					Activator.logError(e);
				}
			}
		};

		openInDefaultBrowserAction.setText(Messages.VpvView_OPEN_IN_DEFAULT_BROWSER);
		openInDefaultBrowserAction.setToolTipText(Messages.VpvView_OPEN_IN_DEFAULT_BROWSER);
		openInDefaultBrowserAction.setImageDescriptor(Activator.getImageDescriptor("icons/open_in_default_browser.gif")); //$NON-NLS-1$
	}

	private void makeRefreshAction() {
		refreshAction = new Action() {
			public void run() {
				refresh(browser);
			}
		};
		refreshAction.setText(Messages.VpvView_REFRESH);
		refreshAction.setToolTipText(Messages.VpvView_REFRESH);
		refreshAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh.gif")); //$NON-NLS-1$
	}
	
	
	public boolean isAutomaticRefreshEnabled() {
		return enableAutomaticRefresh;
	}
}
