/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.actions;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.greenelk.ditatools.common.Utils;
import org.greenelk.ditatools.eclipse.DITAToolsPlugin;
import org.greenelk.ditatools.exceptions.RefFileExistsElsewhereException;
import org.greenelk.ditatools.exceptions.RefFileNotFoundException;
import org.greenelk.ditatools.exceptions.RefPathNotFoundException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HREFChecker extends ResourceChecker {

	public static final String rootDirectory = ".\\";
	public static final String attrHREF = "href";
	public static final String attrFORMAT = "format";
	public static final String attrSCOPE = "scope";
	public static final String SCOPE_PEER = "peer";
	public static final String SCOPE_EXTERNAL = "external";

	Map<String, IFile> allfiles = null;

	/**
	 * Constructor for Action1.
	 */
	public HREFChecker() {
		super();
	}

	@Override
	void initialise(List<?> resources) {
		List<?> projects = Arrays.asList(ResourcesPlugin.getWorkspace()
				.getRoot().getProjects());
		// Collect map of all files
		allfiles = Utils.getFileMap(projects, null);
	}

	@Override
	String getMarkerType() {
		return DITAToolsPlugin.PLUGIN_ID + ".hrefproblem";
	}

	@Override
	String getCheckerType() {
		return "HREF";
	}

	@Override
	String getFiletypeFilter() {
		return DITAToolsPlugin.getDefault()
				.getHREFFileFilterPreferenceAsRegExp();
	}

	protected void processNode(IFile ifile, File f, Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element el = (Element) node;

			if (!el.getAttribute(attrHREF).isEmpty()) {
				String formatValue = ifile.getFileExtension();

				if (!el.getAttribute(attrFORMAT).isEmpty()) {
					formatValue = el.getAttribute(attrFORMAT);
				}
				if (formatValue == null) {
					formatValue = "dita";
				}
				String orighrefValue = el.getAttribute(attrHREF);
				String hrefValue = orighrefValue;
				int lineNumber = -1;
				try {
					lineNumber = Integer.parseInt(node
							.getUserData("lineNumber").toString());
				} catch (Exception e) {
				}
				try {
					if (ifile.getFileExtension().compareTo("dita") == 0
							&& !el.getAttribute(attrSCOPE).isEmpty()) {
						String scope = el.getAttribute(attrSCOPE);
						String title = el.getTextContent().trim()
								.replaceAll("(\r\n|\r|\n|\n\r)", " ");
						if (title.isEmpty()) {
							if (scope.compareTo(SCOPE_PEER) == 0) {
								createErrorMarker(ifile, lineNumber,
										"No title text set for scope='" + scope
												+ "' ");
							}

						} else {
							createInfoMarker(ifile, lineNumber,
									"Title text (scope='" + scope + "') is '"
											+ title + "'");
						}
					}
					if (formatValue.compareTo("mailto") == 0
							|| hrefValue.startsWith("mailto")) {
						createWarningMarker(ifile, lineNumber,
								"Unable to check email " + hrefValue);
					} else {
						String resolvedRef = "";
						try {
							resolvedRef = resolveRelativeRef(f, hrefValue,
									knownGoodREFS, Utils.SAVE_LOCATION);
						} catch (RefFileNotFoundException e) {
							if (formatValue.compareTo("html") == 0
									&& !hrefValue.startsWith("http")) {
								hrefValue = hrefValue.replace(".html", ".dita");
								resolvedRef = resolveRelativeRef(f, hrefValue,
										knownGoodREFS, Utils.SAVE_LOCATION);
							} else {
								throw (e);
							}

						}
						createInfoMarker(ifile, lineNumber, "Resolved "
								+ orighrefValue + " to '" + resolvedRef + "'");
					}
				} catch (RefFileExistsElsewhereException e) {
					HashMap<String, String> attribs = new HashMap<String, String>();
					attribs.put("originalValue", orighrefValue);
					attribs.put("replacementValue", e.getMessage());
					String errorMessage = "Can't find '" + orighrefValue
							+ "', though file exists here : '" + e.getMessage()
							+ "'";
					createFixableErrorMarker(ifile, lineNumber, errorMessage,
							attribs);
				} catch (RefPathNotFoundException e) {
					createWarningMarker(ifile, lineNumber, orighrefValue, e);
				} catch (RefFileNotFoundException e) {
					createErrorMarker(ifile, lineNumber, orighrefValue, e);
				} catch (Exception e) {
					createErrorMarker(ifile, lineNumber, orighrefValue, e);
				}
			}
		}
	}

	void handleFnF(URL href, Exception e) throws Exception {

		File f = new File(href.getFile());
		IFile existingFile = allfiles.get(f.getName());

		if (existingFile != null) {
			throw new RefFileExistsElsewhereException(existingFile
					.getFullPath().toString(), e);
		}

		if (e.getMessage().contains("cannot find the path")) {
			throw new RefPathNotFoundException("Folder '"
					+ f.getParentFile().getPath() + "' not found", e);

		} else if (e.getMessage().contains("cannot find the file")) {

			throw new RefFileNotFoundException("File not found in folder '"
					+ f.getParentFile().getPath() + "'", e);
		} else {
			throw (e);
		}

	}

	@Override
	void displaySummary() {

	}
}
