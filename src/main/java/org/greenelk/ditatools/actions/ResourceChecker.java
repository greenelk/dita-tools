/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.greenelk.ditatools.common.PositionalXMLReader;
import org.greenelk.ditatools.common.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ResourceChecker implements IObjectActionDelegate {

	protected ISelection selection;
	String workspaceDirectory;
	HashMap<String, String> knownGoodREFS = new HashMap<String, String>();

	abstract void initialise(List<?> resources);

	abstract String getMarkerType();

	abstract String getCheckerType();

	abstract String getFiletypeFilter();

	abstract void displaySummary();

	abstract void processNode(IFile ifile, File f, Node node);

	public void run(IAction action) {

		// Get directory name of workspace root
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspaceDirectory = workspace.getRoot().getLocation().toString();

		Job job = new Job("Running " + getCheckerType() + " Checker") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return processResources(monitor);
			}
		};
		job.setUser(true);
		job.schedule();

	}

	protected void processNodes(IFile ifile, File f, NodeList nl) {

		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				processNode(ifile, f, node);
				if (node.hasChildNodes()) {
					processNodes(ifile, f, node.getChildNodes());
				}
			}
		}
	}

	protected void processFile(IFile file) {

		String fileName = workspaceDirectory + file.getFullPath().toString();

		try {

			file.deleteMarkers(getMarkerType(), true, IResource.DEPTH_INFINITE);

			Document doc = PositionalXMLReader.readXML(new FileInputStream(
					new File(fileName)));
			File f = new File(fileName);
			Utils.validateDITAFile(f);
			processNodes(file, f, doc.getChildNodes());

		} catch (Exception e) {
			createErrorMarker(file, 1, "FILE FORMAT ERROR", e);
			// System.err.println(fileName + " = " + e.getMessage());
		}
	}

	protected IStatus processResources(IProgressMonitor monitor) {

		knownGoodREFS.clear();
		List<?> resources = (selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection)
				.toList() : Collections.EMPTY_LIST;

		initialise(resources);

		CookieHandler.setDefault(new CookieManager(null,
				CookiePolicy.ACCEPT_ALL));

		// Collect all selected files, based on filter
		Vector<IFile> ifiles = Utils.getFileResources(resources,
				getFiletypeFilter());

		// Run link check against all the files found
		monitor.beginTask(
				"Checking " + getCheckerType() + "s in " + ifiles.size()
						+ " DITA files", ifiles.size());
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

	public String resolveRelativeRef(File f, String refValue,
			HashMap<String, String> knownGoodREFS, int saveMethod)
			throws Exception {

		URL href = null;
		String resolvedref = null;

		try {
			href = f.toURI().resolve(refValue).toURL();

			if (!knownGoodREFS.containsKey(href.toString())) {
				URLConnection conn = href.openConnection();
				InputStream is = Utils.openConnectionCheckRedirects(conn);
				// check if resolved reference has an anchor point
				String ref = href.getRef();
				if (ref == null) {
					// No anchor points, so no need to read contents
					knownGoodREFS.put(href.toString(), href.toString());
					resolvedref = href.toString();
				} else {
					// anchor point specified. Lets read the stream and look for
					// it, but only
					// look in local files as remote html is often poorly formed
					if ("file".compareTo(href.getProtocol()) == 0) {
						String context = Utils.contextFromStream(is, href, ref,
								saveMethod);
						knownGoodREFS.put(href.toString(), context);
					} else {
						knownGoodREFS.put(href.toString(), href.toString());
					}
					resolvedref = knownGoodREFS.get(href.toString());
				}
			} else {
				resolvedref = knownGoodREFS.get(href.toString());
			}

		} catch (MalformedURLException e) {
			throw new Exception("MalformedURLException - " + e);
		} catch (FileNotFoundException e) {
			handleFnF(href, e);
		} catch (IOException e) {
			throw new Exception("IOException - " + e);
		}

		return resolvedref;
	}

	abstract void handleFnF(URL href, Exception e) throws Exception;

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		action.setEnabled(true);

	}

	protected void createInfoMarker(IFile file, int lineNumber, String message) {

		createMarker(IMarker.SEVERITY_INFO, file, lineNumber, message);

	}

	protected void createWarningMarker(IFile file, int lineNumber,
			String message) {

		createMarker(IMarker.SEVERITY_WARNING, file, lineNumber, message);
	}

	protected void createWarningMarker(IFile file, int lineNumber,
			String message, Exception e) {

		createMarker(IMarker.SEVERITY_WARNING, file, lineNumber, e.getMessage()
				+ " : " + message);
	}

	protected void createFixableErrorMarker(IFile file, int lineNumber,
			String message, Map<String, String> attributes) {

		createMarker(IMarker.SEVERITY_ERROR, file, lineNumber, message,
				attributes);
	}

	protected void createErrorMarker(IFile file, int lineNumber,
			String message, Exception e) {

		createMarker(IMarker.SEVERITY_ERROR, file, lineNumber, message + " : "
				+ e.getMessage());
	}

	protected void createErrorMarker(IFile file, int lineNumber, String message) {

		createMarker(IMarker.SEVERITY_ERROR, file, lineNumber, message);
	}

	protected void createMarker(final int severity, final IResource resource,
			final int lineNumber, final String message) {

		createMarker(severity, resource, lineNumber, message, null);
	}

	protected void createMarker(final int severity, final IResource resource,
			final int lineNumber, final String message,
			Map<String, String> additionalAttribs) {

		Map<String, Object> attribs;
		if (additionalAttribs != null) {
			attribs = new HashMap<String, Object>(additionalAttribs);
		} else {
			attribs = new HashMap<String, Object>();
		}
		attribs.put(IMarker.SEVERITY, severity);
		attribs.put(IMarker.LINE_NUMBER, lineNumber);
		attribs.put(IMarker.MESSAGE, message);
		attribs.put(IMarker.SEVERITY, severity);

		Utils.createMarker(getMarkerType(), resource, attribs);
	}

}
