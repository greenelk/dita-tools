/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.eclipse;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WFFPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	// The list that displays the current urls list
	private List urlList;
	private Text urlEntryText;
	public static final String PAGE_ID = "org.greenelk.ditatools.wffpage";

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {

		Composite entryTable = new Composite(parent, SWT.NULL);

		// Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		GridLayout gridLayout = new GridLayout();
		entryTable.setLayout(gridLayout);

		// ************************************
		// URL list
		// ************************************
		Group group1 = new Group(entryTable, SWT.SHADOW_ETCHED_IN);
		group1.setText("URL Prefixes");
		group1.setLayout(gridLayout);
		group1.setLayoutData(data);
		// Add in a dummy label for spacing
		new Label(group1, SWT.NONE);

		urlList = new List(group1, SWT.BORDER | SWT.V_SCROLL);
		urlList.setItems(DITAToolsPlugin.getDefault().getWFFPreference());

		// Create a data that takes up the extra space in the dialog and spans
		// both columns.
		data = new GridData(GridData.FILL_BOTH);
		urlList.setLayoutData(data);

		Composite buttonComposite = new Composite(group1, SWT.NULL);

		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 2;
		buttonComposite.setLayout(buttonLayout);

		// Create a data that takes up the extra space in the dialog and spans
		// both columns.
		data = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);
		buttonComposite.setLayoutData(data);

		Button addButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

		addButton.setText("Add to List"); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				urlList.add(urlEntryText.getText(), urlList.getItemCount());
			}
		});

		urlEntryText = new Text(buttonComposite, SWT.BORDER);
		// Create a data that takes up the extra space in the dialog .
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		urlEntryText.setLayoutData(data);

		Button removeButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

		removeButton.setText("Remove Selection"); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				urlList.remove(urlList.getSelectionIndex());
			}
		});

		data = new GridData();
		data.horizontalSpan = 2;
		removeButton.setLayoutData(data);

		return entryTable;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference store we wish to use
		setPreferenceStore(DITAToolsPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Performs special processing when this page's Restore Defaults button has
	 * been pressed. Sets the contents of the nameEntry field to be the default
	 */
	protected void performDefaults() {
		urlList.setItems(DITAToolsPlugin.getDefault().getDefaultWFFPreference());
	}

	/**
	 * Method declared on IPreferencePage. Save the author name to the
	 * preference store.
	 */
	public boolean performOk() {
		DITAToolsPlugin.getDefault().setWFFPreference(urlList.getItems());
		return super.performOk();
	}

}
