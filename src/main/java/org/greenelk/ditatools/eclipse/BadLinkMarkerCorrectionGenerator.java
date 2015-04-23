/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.eclipse;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class BadLinkMarkerCorrectionGenerator implements
		IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {

	private static final IMarkerResolution2[] NO_RESOLUTIONS = new IMarkerResolution2[0];

	@Override
	public IMarkerResolution2[] getResolutions(IMarker marker) {
		if (!hasResolutions(marker)) {
			return NO_RESOLUTIONS;
		}
		return new IMarkerResolution2[] { new BadLinkMarkerCorrection(marker) };
	}

	@Override
	public boolean hasResolutions(IMarker marker) {
		try {
			String markerType = marker.getType();
			String originalValue = (String) marker
					.getAttribute("originalValue");
			String replacementValue = (String) marker
					.getAttribute("replacementValue");
			return (markerType.equals(DITAToolsPlugin.PLUGIN_ID
					+ ".hrefproblem")
					&& originalValue != null && replacementValue != null);
		} catch (CoreException e) {
			return false;
		}
	}

}
