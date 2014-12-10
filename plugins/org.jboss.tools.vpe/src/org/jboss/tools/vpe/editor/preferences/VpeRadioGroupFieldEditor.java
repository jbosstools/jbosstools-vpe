package org.jboss.tools.vpe.editor.preferences;

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
