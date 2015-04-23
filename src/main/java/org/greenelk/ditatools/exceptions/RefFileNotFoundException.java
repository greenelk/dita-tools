/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.exceptions;

public class RefFileNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefFileNotFoundException(String message, Exception e) {
		super(message, e);
	}

}
