/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.eclipse;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

public class BadLinkMarkerCorrection implements IMarkerResolution,
		IMarkerResolution2 {

	String originalValue;
	String replacementValue;

	BadLinkMarkerCorrection(IMarker marker) {
		// String reviewIssueKey = ReviewMarker.ATTRIBUTE_REVIEW_ISSUE;

		try {
			originalValue = (String) marker.getAttribute("originalValue");
			replacementValue = (String) marker.getAttribute("replacementValue");
		} catch (CoreException e) {

		}

	}

	@Override
	public String getLabel() {

		return "Replace link '" + originalValue
				+ "' with a relative link to the file '" + replacementValue
				+ "'";
	}

	@Override
	public void run(IMarker marker) {
		// create dialog with ok and cancel button and info icon
		MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_WARNING
				| SWT.OK | SWT.CANCEL);
		dialog.setText("Not yet available");
		dialog.setMessage("Sorry, but this function is not working yet");
		dialog.open();
	}

	@Override
	public String getDescription() {

		return "The  link '"
				+ originalValue
				+ "' couldn't be resolved in the current workspace, though a file was found with the same name : '"
				+ replacementValue
				+ "'. Resolution will replace the current link with a link to the file that does exist";

	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
