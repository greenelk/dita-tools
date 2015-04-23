/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.validation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class SimpleEntityResolver implements EntityResolver2 {

	protected String baseDirectory;
	protected Map<String, String> map = new HashMap<String, String>();

	public SimpleEntityResolver(String baseDirectory) {
		this.baseDirectory = baseDirectory;
		map.put("strictTaskbodyConstraint.mod", "dtd\\technicalContent\\dtd\\");
		map.put("eclipsemap.dtd", "plugins\\eclipsemap\\dtd\\");
		map.put("ibm-reference.dtd", "plugins\\AuthoringTools\\dtd\\");
		map.put("ibm-topic.dtd", "plugins\\AuthoringTools\\dtd\\");
		map.put("ibm-map.dtd", "plugins\\AuthoringTools\\dtd\\");
		map.put("ibm-task.dtd", "plugins\\AuthoringTools\\dtd\\");
		map.put("ibm-concept.dtd", "plugins\\AuthoringTools\\dtd\\");
		map.put("mathmlDomain.ent",
				"plugins\\org.oasis-open.dita.mathml.doctypes\\doctypes\\dtd\\");
		map.put("equationDomain.ent",
				"plugins\\org.oasis-open.dita.mathml.doctypes\\doctypes\\dtd\\");
		map.put("mathmlDomain.mod",
				"plugins\\org.oasis-open.dita.mathml.doctypes\\doctypes\\dtd\\");
		map.put("equationDomain.mod",
				"plugins\\org.oasis-open.dita.mathml.doctypes\\doctypes\\dtd\\");
	}

	/*
	 * public InputSource resolveEntity(String publicId, String systemId) throws
	 * SAXException, IOException {
	 * 
	 * InputSource inputSource = null;
	 * 
	 * try { String fn = systemId.substring(systemId.lastIndexOf("/") + 1); if
	 * (fn.endsWith(".dtd")) { File ff = new File(baseDirectory + fn); if
	 * (ff.exists()) { inputSource = new InputSource(baseDirectory + fn); } }
	 * else { return new InputSource(fn); } } catch (Exception e) { // No
	 * action; just let the null InputSource pass through }
	 * 
	 * // If nothing found, null is returned, for normal processing return
	 * inputSource; }
	 */
	@Override
	public InputSource getExternalSubset(String arg0, String arg1)
			throws SAXException, IOException {
		// TODO Auto-generated method stub
		System.out.println("getExternalSubset " + arg0 + " a " + arg1);
		return null;
	}

	@Override
	public InputSource resolveEntity(String arg0, String arg1, String arg2,
			String arg3) throws SAXException, IOException {
		// TODO Auto-generated method stub
		InputSource is = null;
		String path = map.get(arg3);
		// System.out.println("\tresolve " + arg3 + " as " + path);
		if (path != null) {
			is = new InputSource(baseDirectory + path + arg3);
		}
		return is;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
}