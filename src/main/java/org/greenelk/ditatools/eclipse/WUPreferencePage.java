/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.eclipse;

import java.util.Arrays;

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

public class WUPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final String PAGE_ID = "org.greenelk.ditatools.wupage";

	private List fileFilterList;
	private Text fileEntryText;
	private List xrefFilterList;
	private Text xrefEntryText;

	private Label description;

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
		// HREF list
		// ************************************
		Group group1 = new Group(entryTable, SWT.SHADOW_ETCHED_IN);
		group1.setText("Filetypes to check");
		group1.setLayout(gridLayout);
		group1.setLayoutData(data);
		// Add in a dummy label for spacing
		new Label(group1, SWT.NONE);

		fileFilterList = new List(group1, SWT.BORDER | SWT.V_SCROLL);
		fileFilterList.setItems(DITAToolsPlugin.getDefault()
				.getWUFileFilterPreference());

		// Create a data that takes up the extra space in the dialog and spans
		// both columns.
		data = new GridData(GridData.FILL_BOTH);
		fileFilterList.setLayoutData(data);

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
				fileFilterList.add(fileEntryText.getText(),
						fileFilterList.getItemCount());
				updateDescriptionText();
			}
		});

		fileEntryText = new Text(buttonComposite, SWT.BORDER);
		// Create a data that takes up the extra space in the dialog .
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		fileEntryText.setLayoutData(data);

		Button removeButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

		removeButton.setText("Remove Selection"); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				fileFilterList.remove(fileFilterList.getSelectionIndex());
				updateDescriptionText();
			}
		});

		data = new GridData();
		data.horizontalSpan = 2;
		removeButton.setLayoutData(data);
		// ************************************
		// Crossref list
		// ************************************

		Group group2 = new Group(entryTable, SWT.SHADOW_ETCHED_IN);
		group2.setText("Crossref Filetypes");
		group2.setLayout(gridLayout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		group2.setLayoutData(data);
		// Add in a dummy label for spacing
		new Label(group2, SWT.NONE);

		xrefFilterList = new List(group2, SWT.BORDER | SWT.V_SCROLL);
		xrefFilterList.setItems(DITAToolsPlugin.getDefault()
				.getWUXREFFilterPreference());

		// Create a data that takes up the extra space in the dialog and spans
		// both columns.
		data = new GridData(GridData.FILL_BOTH);
		xrefFilterList.setLayoutData(data);

		Composite buttonComposite2 = new Composite(group2, SWT.NULL);

		GridLayout buttonLayout2 = new GridLayout();
		buttonLayout2.numColumns = 2;
		buttonComposite2.setLayout(buttonLayout2);

		// Create a data that takes up the extra space in the dialog and spans
		// both columns.
		data = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);
		buttonComposite2.setLayoutData(data);

		Button addButton2 = new Button(buttonComposite2, SWT.PUSH | SWT.CENTER);

		addButton2.setText("Add to List"); //$NON-NLS-1$
		addButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				xrefFilterList.add(xrefEntryText.getText(),
						xrefFilterList.getItemCount());
				updateDescriptionText();
			}
		});

		xrefEntryText = new Text(buttonComposite2, SWT.BORDER);
		// Create a data that takes up the extra space in the dialog .
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		xrefEntryText.setLayoutData(data);

		Button removeButton2 = new Button(buttonComposite2, SWT.PUSH
				| SWT.CENTER);

		removeButton2.setText("Remove Selection"); //$NON-NLS-1$
		removeButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				xrefFilterList.remove(xrefFilterList.getSelectionIndex());
				updateDescriptionText();
			}
		});

		data = new GridData();
		data.horizontalSpan = 2;
		removeButton2.setLayoutData(data);

		// Description
		new Label(group2, SWT.NONE);

		description = new Label(entryTable, SWT.NONE);
		updateDescriptionText();

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
		fileFilterList.setItems(DITAToolsPlugin.getDefault()
				.getDefaultWUFileFilterPreference());
		xrefFilterList.setItems(DITAToolsPlugin.getDefault()
				.getDefaultWUXREFFilterPreference());
		updateDescriptionText();
	}

	/**
	 * Method declared on IPreferencePage. Save the author name to the
	 * preference store.
	 */
	public boolean performOk() {
		DITAToolsPlugin.getDefault().setWUFileFilterPreference(
				fileFilterList.getItems());
		DITAToolsPlugin.getDefault().setWUXREFFilterPreference(
				xrefFilterList.getItems());
		return super.performOk();
	}

	private void updateDescriptionText() {
		description.setText("References to selected "
				+ (Arrays.asList(fileFilterList.getItems()).toString()
						.replace("]", "").replace("[", ""))
				+ " files will be searched for in "
				+ (Arrays.asList(xrefFilterList.getItems()).toString()
						.replace("]", "").replace("[", "")) + " files.");
	}

}
