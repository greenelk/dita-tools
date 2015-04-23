/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.actions;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.greenelk.ditatools.common.Utils;
import org.greenelk.ditatools.eclipse.DITAToolsPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WhereUsedChecker extends ResourceChecker {

	public static final String rootDirectory = ".\\";
	public static final String attrHREF = "href";
	public static final String attrCONREF = "conref";
	public static final String attrFORMAT = "format";

	public static final String SELECTED_FILETYPES = "dita|gif|jpg|png";

	Vector<IFile> selectedFiles = null;
	Map<String, List<IFile>> whereUsedMap = null;

	/**
	 * Constructor for Action3.
	 */
	public WhereUsedChecker() {
		super();
	}

	@Override
	void initialise(List<?> resources) {

		selectedFiles = Utils.getFileResources(resources, SELECTED_FILETYPES);
		whereUsedMap = new HashMap<String, List<IFile>>();
	}

	@Override
	String getMarkerType() {
		return DITAToolsPlugin.PLUGIN_ID + ".whereusedproblem";
	}

	@Override
	String getCheckerType() {
		return "Where Used";
	}

	@Override
	String getFiletypeFilter() {
		return DITAToolsPlugin.getDefault().getWUFileFilterPreferenceAsRegExp();
	}

	String getXREFFilter() {
		return DITAToolsPlugin.getDefault().getWUXREFFilterPreferenceAsRegExp();
	}

	protected IStatus processResources(IProgressMonitor monitor) {

		knownGoodREFS.clear();

		List<?> projects = Arrays.asList(ResourcesPlugin.getWorkspace()
				.getRoot().getProjects());

		List<?> resources = (selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection)
				.toList() : Collections.EMPTY_LIST;

		initialise(resources);

		CookieHandler.setDefault(new CookieManager(null,
				CookiePolicy.ACCEPT_ALL));

		// Collect all potential reference files
		Vector<IFile> ifiles = Utils
				.getFileResources(projects, getXREFFilter());

		// Run link check against all the files found
		monitor.beginTask("Checking where " + selectedFiles.size() + " "
				+ getFiletypeFilter().replace("|", "/") + " files are used in "
				+ ifiles.size() + " " + getXREFFilter().replace("|", "/")
				+ " reference files.", ifiles.size());
		for (IFile ifile : ifiles) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			processFile(ifile);
			monitor.worked(1);

		}
		displaySummary();
		return Status.OK_STATUS;
	}

	@Override
	void displaySummary() {

		Iterator<IFile> i = selectedFiles.iterator();
		while (i.hasNext()) {
			IFile element = i.next();
			String fullfileName = "file:/" + workspaceDirectory
					+ element.getFullPath();

			if (whereUsedMap.get(fullfileName) != null) {
				List<IFile> references = whereUsedMap.get(fullfileName);
				int ditaCount = 0;
				int ditaMapCount = 0;
				int otherCount = 0;
				String lastRef = "";
				for (IFile reference : references) {
					lastRef = reference.getFullPath().toString();
					String fileType = reference.getFileExtension();
					if ("ditamap".compareTo(fileType) == 0) {
						ditaMapCount++;
					} else if ("dita".compareTo(fileType) == 0) {
						ditaCount++;
					} else {
						otherCount++;
					}
				}
				String refDescription = "";
				if (ditaCount + ditaMapCount + otherCount == 1) {
					refDescription = lastRef;
				} else {
					if (ditaMapCount > 0) {
						refDescription += ditaMapCount + " ditamap ";
					}
					if (ditaCount > 0) {
						refDescription += ditaCount + " dita ";
					}
					if (otherCount > 0) {
						refDescription += otherCount + " other ";
					}
					refDescription += "files";
				}
				if (ditaMapCount == 0 && fullfileName.endsWith(".dita")) {
					createMarker(
							IMarker.SEVERITY_ERROR,
							element,
							1,
							"BROKEN LINKS(" + ditaCount + ") : "
									+ element.getFullPath()
									+ " is referenced by " + refDescription
									+ " but no ditamap files in this workspace");
				} else {
					createMarker(IMarker.SEVERITY_INFO, element, 1,
							element.getFullPath() + " is referenced by "
									+ refDescription);
				}
			} else {
				createMarker(
						IMarker.SEVERITY_ERROR,
						element,
						1,
						"ORPHAN FILE : "
								+ element.getFullPath()
								+ " is not referenced by anything in the workspace");
			}
		}
	}

	@Override
	void processNode(IFile ifile, File f, Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element el = (Element) node;
			String origrefValue = null;
			if (!el.getAttribute(attrHREF).isEmpty()) {
				origrefValue = el.getAttribute(attrHREF);
			} else if (!el.getAttribute(attrCONREF).isEmpty()) {
				origrefValue = el.getAttribute(attrCONREF);
			}
			if (origrefValue != null) {
				String formatValue = "dita";
				if (!el.getAttribute(attrFORMAT).isEmpty()) {
					formatValue = el.getAttribute(attrFORMAT);
				}
				String refValue = origrefValue;
				try {
					if (formatValue.compareTo("mailto") == 0
							|| refValue.startsWith("mailto")) {
						// ignore
					} else {
						if (formatValue.compareTo("html") == 0
								&& !refValue.startsWith("http")) {
							refValue = refValue.replace(".html", ".dita");
						}
						String resolvedRef = resolveRelativeRef(f, refValue,
								knownGoodREFS, Utils.SAVE_LOCATION);
						addWhereUsed(resolvedRef, ifile);

					}
				} catch (Exception e) {
					// Do nothing
				}
			}
		}
	}

	@Override
	void handleFnF(URL href, Exception e) throws Exception {

		throw (e);

	}

	private void addWhereUsed(String resolvedRef, IFile whereUsed) {
		String rr = resolvedRef.split("#")[0];
		List<IFile> wul = whereUsedMap.get(rr);
		if (wul == null) {
			wul = new Vector<IFile>();
		}
		wul.add(whereUsed);
		whereUsedMap.put(rr, wul);

	}
}
