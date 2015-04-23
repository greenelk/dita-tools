/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.validation;

import java.util.Hashtable;

import org.xml.sax.DTDHandler;

import com.sun.xml.internal.fastinfoset.Notation;
import com.sun.xml.internal.fastinfoset.UnparsedEntity;

public class SimpleDTDHandler implements DTDHandler {

	private Hashtable<String, Notation> notations = new Hashtable<String, Notation>();
	private Hashtable<String, UnparsedEntity> entities = new Hashtable<String, UnparsedEntity>();

	public void notationDecl(String name, String publicID, String systemID) {

		System.out.println(name);
		notations.put(name, new Notation(name, publicID, systemID));

	}

	public void unparsedEntityDecl(String name, String publicID,
			String systemID, String notationName) {

		entities.put(name, new UnparsedEntity(name, publicID, systemID,
				notationName));

	}

	public UnparsedEntity getUnparsedEntity(String name) {
		System.out.println("Getting " + name);
		return (UnparsedEntity) entities.get(name);
	}

	public Notation getNotation(String name) {
		System.out.println("Getting " + name);
		return (Notation) notations.get(name);
	}

}
