/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.eclipse;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GeneralPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.greenelk.ditatools.generalpage";

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {

		Composite entryTable = new Composite(parent, SWT.NULL);

		Label label = new Label(entryTable, SWT.NULL);
		label.setText("Expand the tree to edit preferences for DITA Tools");

		// Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		GridLayout gridLayout = new GridLayout();
		entryTable.setLayout(gridLayout);

		return entryTable;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}

	/**
	 * Performs special processing when this page's Restore Defaults button has
	 * been pressed. Sets the contents of the nameEntry field to be the default
	 */
	protected void performDefaults() {
	}

	/**
	 * Method declared on IPreferencePage. Save the author name to the
	 * preference store.
	 */
	public boolean performOk() {
		return super.performOk();
	}

}
