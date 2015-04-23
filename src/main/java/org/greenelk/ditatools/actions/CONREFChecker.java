/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.actions;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.greenelk.ditatools.common.Utils;
import org.greenelk.ditatools.eclipse.DITAToolsPlugin;
import org.greenelk.ditatools.exceptions.RefFileNotFoundException;
import org.greenelk.ditatools.exceptions.RefPathNotFoundException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CONREFChecker extends ResourceChecker {

	public static final String rootDirectory = ".\\";
	public static final String attrCONREF = "conref";
	public static final String attrFORMAT = "format";

	/**
	 * Constructor for Action2.
	 */
	public CONREFChecker() {
		super();
	}

	@Override
	void initialise(List<?> resources) {
		// Nothing to do
	}

	@Override
	String getMarkerType() {
		return DITAToolsPlugin.PLUGIN_ID + ".conrefproblem";
	}

	@Override
	String getCheckerType() {
		return "CONREF";
	}

	@Override
	String getFiletypeFilter() {
		return "dita";
	}

	protected void processNode(IFile ifile, File f, Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element el = (Element) node;

			if (!el.getAttribute(attrCONREF).isEmpty()) {
				String formatValue = "dita";
				if (!el.getAttribute(attrFORMAT).isEmpty()) {
					formatValue = el.getAttribute(attrFORMAT);
				}
				String orighrefValue = el.getAttribute(attrCONREF);
				String hrefValue = orighrefValue;
				int lineNumber = -1;
				try {
					lineNumber = Integer.parseInt(node
							.getUserData("lineNumber").toString());
				} catch (Exception e) {
				}
				try {
					if (formatValue.compareTo("mailto") == 0
							|| hrefValue.startsWith("mailto")) {
						createWarningMarker(ifile, lineNumber,
								"Unable to check email " + hrefValue);
					} else {
						if (formatValue.compareTo("html") == 0
								&& !hrefValue.startsWith("http")) {
							hrefValue = hrefValue.replace(".html", ".dita");
						}
						String resolvedRef = resolveRelativeRef(f, hrefValue,
								knownGoodREFS, Utils.SAVE_TEXT);
						createInfoMarker(ifile, lineNumber, "Resolved "
								+ orighrefValue + " to '" + resolvedRef + "'");
					}
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

		throw (e);
	}

	@Override
	void displaySummary() {

		Iterable<String> vals = knownGoodREFS.values();
		System.out.println("Constants referred to in the selected files:");
		for (String val : vals) {
			System.out.println("\t" + val);
		}
	}

}
