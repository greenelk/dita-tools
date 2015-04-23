/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleErrorHandler implements ErrorHandler {
	public void warning(SAXParseException e) throws SAXException {
		System.err.println("warning: " + e.getMessage());
	}

	public void error(SAXParseException e) throws SAXException {
		System.err.println("error: " + e.getMessage() + "-" + e.getSystemId()
				+ "-" + e.getColumnNumber() + "-" + e.getLineNumber());
	}

	public void fatalError(SAXParseException e) throws SAXException {
		System.err.println("fatalError: " + e.getMessage());
	}
}