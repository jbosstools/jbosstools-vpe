/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.preview.editor;

import static org.jboss.tools.vpe.messages.VpeUIMessages.ENGINE_DIALOG_CHANGE_TO;
import static org.jboss.tools.vpe.messages.VpeUIMessages.ENGINE_DIALOG_DO_NOT_SHOW_AGAIN;
import static org.jboss.tools.vpe.messages.VpeUIMessages.ENGINE_DIALOG_HEADER;
import static org.jboss.tools.vpe.messages.VpeUIMessages.ENGINE_DIALOG_HTML5;
import static org.jboss.tools.vpe.messages.VpeUIMessages.ENGINE_DIALOG_JSF;
import static org.jboss.tools.vpe.messages.VpeUIMessages.ENGINE_DIALOG_STAY_WITH;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.jboss.tools.jst.web.ui.WebUiPlugin;
import org.jboss.tools.jst.web.ui.internal.editor.preferences.IVpePreferencesPage;

/**Dialog which shown for Linux users to choose between xulrunner and webkit 
 * @see JBIDE-17818
 * 
 * @author Konstantin Marmalyukov (kmarmaliykov)
 *
 */
public class EngineDialog extends MessageDialog {
	private boolean isRemember = false;
	
	private static boolean webkitIsUsed = WebUiPlugin.getDefault().getPreferenceStore().getBoolean(IVpePreferencesPage.USE_VISUAL_EDITOR_FOR_HTML5);
	private static String webkitOption = MessageFormat.format(ENGINE_DIALOG_HTML5, webkitIsUsed ? ENGINE_DIALOG_STAY_WITH : ENGINE_DIALOG_CHANGE_TO);
	private static String xulrunnerOption = MessageFormat.format(ENGINE_DIALOG_JSF, webkitIsUsed ? ENGINE_DIALOG_CHANGE_TO : ENGINE_DIALOG_STAY_WITH);
	public static final String PREFS_ID = "org.jboss.tools.vpe.editor";  //$NON-NLS-1$
	
	private Link link;
	
	public EngineDialog(Shell parentShell) {
		super(parentShell, ENGINE_DIALOG_HEADER, null, "", INFORMATION, //$NON-NLS-1$
				new String[] {webkitOption, xulrunnerOption}, webkitIsUsed ? 0 : 1);
		
		message = Messages.EngineDialog_MESSAGE;
	}
	
	@Override
	protected Control createCustomArea(Composite parent) {
		GridLayout gridLayout = (GridLayout) parent.getLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		Button button = new Button(parent, SWT.CHECK);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				isRemember = !isRemember;
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				isRemember = !isRemember;
			}
		});
		button.setText(ENGINE_DIALOG_DO_NOT_SHOW_AGAIN);
		return parent;
	}
	
	@Override
	protected Control createMessageArea(Composite composite) {
		// create composite
		// create image
		Image image = getImage();
		if (image != null) {
			imageLabel = new Label(composite, SWT.NULL);
			image.setBackground(imageLabel.getBackground());
			imageLabel.setImage(image);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
		}
		// create message
		if (message != null) {
			link = new Link(composite, getMessageLabelStyle());
			link.setText(message);
			link.addSelectionListener(new SelectionAdapter(){
		        @Override
		        public void widgetSelected(SelectionEvent e) {
		        	PreferencesUtil.createPreferenceDialogOn(null, PREFS_ID, new String[] {PREFS_ID}, null).open();
		        }
		    });

			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
					.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
					.applyTo(link);
		}
		
		return composite;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		WebUiPlugin.getDefault().getPreferenceStore().setValue(IVpePreferencesPage.REMEMBER_VISUAL_EDITOR_ENGINE, isRemember);
		boolean useWebkit = false;
		if (buttonId == 0) {
			useWebkit = true;
		}
		if (buttonId == 1) {
			useWebkit = false;
		}
		if (useWebkit != webkitIsUsed) {
			WebUiPlugin.getDefault().getPreferenceStore().setValue(IVpePreferencesPage.USE_VISUAL_EDITOR_FOR_HTML5, useWebkit);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					PlatformUI.getWorkbench().restart();
				}
			});
		}
	}
}
