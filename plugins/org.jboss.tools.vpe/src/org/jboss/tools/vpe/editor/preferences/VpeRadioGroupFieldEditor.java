/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/package org.jboss.tools.vpe.editor.preferences;

import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class VpeRadioGroupFieldEditor extends RadioGroupFieldEditor {
	public VpeRadioGroupFieldEditor(String name, String labelText, int numColumns, String[][] labelAndValues,
			Composite parent) {
		super(name, labelText, numColumns, labelAndValues, parent);
	}

	@Override
	protected void createControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = getNumberOfControls();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.horizontalSpacing = HORIZONTAL_GAP;
		parent.setLayout(layout);
		doFillIntoGrid(parent, layout.numColumns);
	}
}
