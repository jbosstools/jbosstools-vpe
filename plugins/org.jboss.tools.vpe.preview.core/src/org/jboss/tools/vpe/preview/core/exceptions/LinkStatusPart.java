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
package org.jboss.tools.vpe.preview.core.exceptions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.internal.part.StatusPart;

/**The same, as StatusPart, but it has Link inside.
 * 
 * @see StatusPart
 * 
 * @author Konstantin Marmalyukov (kmarmaliykov)
 *
 */
public class LinkStatusPart extends StatusPart {
	public static final String ID = "org.jboss.tools.vpe.editor";  //$NON-NLS-1$
	
	public LinkStatusPart(Composite parent, IStatus reason_, Composite c) {
		super(c, reason_);
		
		Color bgColor= parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		Color fgColor= parent.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
    	
		parent.setBackground(bgColor);
		parent.setForeground(fgColor);
		
		GridLayout layout = new GridLayout();
        
        layout.numColumns = 3;
        
        int spacing = 8;
        int margins = 8;
        layout.marginBottom = margins;
        layout.marginTop = margins;
        layout.marginLeft = margins;
        layout.marginRight = margins;
        layout.horizontalSpacing = spacing;
        layout.verticalSpacing = spacing;
        parent.setLayout(layout);
		
		for (Control child : c.getChildren()) {
			if (child instanceof Text) {
				child.dispose();
				Link plink = new Link(parent, SWT.MULTI | SWT.WRAP | SWT.FILL);
		        plink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			    plink.setText(reason_.getMessage());
			    plink.addSelectionListener(new SelectionAdapter(){
			        @Override
			        public void widgetSelected(SelectionEvent e) {
			        	PreferencesUtil.createPreferenceDialogOn(null, ID, new String[] {ID}, null).open();
			        }
			    });
			} else {
				child.setParent(parent);
				parent.layout(true);
			}
		}
		
		c.dispose();
		parent.layout(true);
	}

}
