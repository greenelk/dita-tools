/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.greenelk.ditatools.validation.SimpleEntityResolver;
import org.greenelk.ditatools.validation.SimpleErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class Utils {

	public static final int SAVE_LOCATION = 0;
	public static final int SAVE_TEXT = 1;

	public static InputStream openConnectionCheckRedirects(URLConnection c)
			throws IOException {
		boolean redir;
		int redirects = 0;
		InputStream in = null;
		do {
			if (c instanceof HttpURLConnection) {
				((HttpURLConnection) c).setInstanceFollowRedirects(false);
			}
			// We want to open the input stream before getting headers
			// because getHeaderField() et al swallow IOExceptions.
			in = c.getInputStream();
			redir = false;
			if (c instanceof HttpURLConnection) {
				HttpURLConnection http = (HttpURLConnection) c;
				int stat = http.getResponseCode();
				if (stat >= 300 && stat <= 307 && stat != 306
						&& stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
					URL base = http.getURL();
					String loc = http.getHeaderField("Location");
					URL target = null;
					if (loc != null) {
						target = new URL(base, loc);
					}
					http.disconnect();
					// Redirection should be allowed only for HTTP and HTTPS
					// and should be limited to 5 redirections at most.
					if (target == null
							|| !(target.getProtocol().equals("http") || target
									.getProtocol().equals("https"))
							|| redirects >= 5) {
						throw new SecurityException("illegal URL redirect");
					}
					redir = true;
					c = target.openConnection();
					redirects++;
				}
			}
		} while (redir);
		return in;
	}

	public static String contextFromStream(InputStream is, URL href,
			String ref, int saveMethod) throws Exception {

		Document doc = PositionalXMLReader.readXML(is);
		XPath xPath = XPathFactory.newInstance().newXPath();

		// Loop through validating for all IDs
		String[] ids = ref.split("/");
		for (int i = 0; i < ids.length; i++) {
			String expression = "//*[@id='" + ids[i] + "']";
			NodeList nodes = (NodeList) xPath.evaluate(expression,
					doc.getDocumentElement(), XPathConstants.NODESET);
			if (nodes.getLength() == 0) {
				if (!ids[i].isEmpty()) {
					throw new Exception("id '" + ids[i] + "' not found");
				}
			} else if (nodes.getLength() > 1) {
				throw new Exception("id '" + ids[i] + "' found "
						+ nodes.getLength() + " times in the file");
			}
		}

		// get a value from the last one
		String lastref = ref.substring(ref.lastIndexOf("/") + 1);
		String expression = "//*[@id='" + lastref + "']";
		NodeList nodes = (NodeList) xPath.evaluate(expression,
				doc.getDocumentElement(), XPathConstants.NODESET);

		Element e = (Element) nodes.item(0);
		String context = null;
		switch (saveMethod) {
		case SAVE_TEXT:
			context = e.getTextContent().replaceAll("(\r\n|\r|\n|\n\r)", " ");
			break;
		case SAVE_LOCATION:
		default:
			context = href.toString();
			break;
		}
		return context;
	}

	public static void validateDITAFile(File f) {
		if (f.getAbsolutePath().contains(".dita")) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			try {
				SAXParser parser = factory.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				reader.setEntityResolver(new SimpleEntityResolver(
						"C:\\DITATools\\IDWB\\dita\\"));
				reader.setErrorHandler(new SimpleErrorHandler());
				// System.out.println("start validation");
				reader.parse(new InputSource(f.toString()));
				// System.out.println("end validation");
			} catch (Exception e) {
				System.err.println("\t\t" + e);
			}
		}
	}

	public static Vector<IFile> getFileResources(List<?> resources) {
		return getFileResources(resources, null);
	}

	public static Vector<IFile> getFileResources(List<?> resources,
			String fileFilter) {
		// Collect together all the files that need processing
		Vector<IFile> ifiles = new Vector<IFile>();

		for (Iterator<?> i = resources.iterator(); i.hasNext();) {
			IResource resource = (IResource) i.next();
			int type = resource.getType();
			try {
				switch (type) {
				case IResource.ROOT:
				case IResource.PROJECT:
				case IResource.FOLDER:
					addFilesToCollection(ifiles, resource, fileFilter);
					break;
				case IResource.FILE:
					addFileToCollection(ifiles, (IFile) resource, fileFilter);
					break;
				default:
					// System.err.println("unknown resource type = " + type);
					break;
				}
			} catch (Exception e) {
				// System.err.println("oops" + e);
			}
		}
		return ifiles;
	}

	protected static void addFileToCollection(Vector<IFile> ifiles,
			IFile ifile, String fileFilter) throws Exception {
		if (fileFilter == null || ifile.getFileExtension().matches(fileFilter)) {
			ifiles.add(ifile);
		}
	}

	protected static void addFilesToCollection(Vector<IFile> ifiles,
			IResource iresource, String fileFilter) throws Exception {

		IResource[] members = null;

		int type = iresource.getType();

		if (type == IResource.PROJECT) {
			IProject iproject = (IProject) iresource;
			members = iproject.members();
		} else if (type == IResource.FOLDER) {
			IFolder ifolder = (IFolder) iresource;
			members = ifolder.members();
		}
		if (members != null) {
			for (int i = 0; i < members.length; i++) {
				if (members[i].getType() == IResource.FILE) {
					addFileToCollection(ifiles, (IFile) members[i], fileFilter);
				} else {
					addFilesToCollection(ifiles, members[i], fileFilter);
				}
			}
		}
	}

	public static Map<String, IFile> getFileMap(List<?> resources,
			String fileFilter) {
		// Collect together all the files that need processing
		Map<String, IFile> ifilesMap = new HashMap<String, IFile>();

		for (Iterator<?> i = resources.iterator(); i.hasNext();) {
			IResource resource = (IResource) i.next();
			int type = resource.getType();
			try {
				switch (type) {
				case IResource.ROOT:
				case IResource.PROJECT:
				case IResource.FOLDER:
					addFilesToMap(ifilesMap, resource, fileFilter);
					break;
				case IResource.FILE:
					addFilesToMap(ifilesMap, (IFile) resource, fileFilter);
					break;
				default:
					System.err.println("unknown resource type = " + type);
					break;
				}
			} catch (Exception e) {
				System.err.println("oops - " + e);
			}
		}
		return ifilesMap;
	}

	protected static void addFileToMap(Map<String, IFile> ifilesMap,
			IFile ifile, String fileFilter) throws Exception {
		if (fileFilter == null || ifile.getFileExtension().matches(fileFilter)) {
			IFile previousFile = ifilesMap.put(ifile.getName(), ifile);
			if (previousFile != null) {
				// System.out.println(ifile.getName() + " found in...");
				// System.out.println("\t" + ifile.getParent().getFullPath());
				// System.out.println("\t"
				// + previousFile.getParent().getFullPath());
			}
		}
	}

	// protected void createDuplicateMarker(final int severity,
	// final String fileName, final IFile file1, final IFile file2) {
	//
	// Map<String, Object> attribs = new HashMap<String, Object>();
	// attribs.put(IMarker.SEVERITY, severity);
	// attribs.put(IMarker.MESSAGE, fileName);
	// attribs.put(IMarker.SEVERITY, severity);

	// Utils.createMarker(IMarker.SEVERITY_WARNING, file1, attribs);
	// }

	protected static void addFilesToMap(Map<String, IFile> ifilesMap,
			IResource iresource, String fileFilter) throws Exception {

		IResource[] members = null;

		int type = iresource.getType();

		if (type == IResource.PROJECT) {
			IProject iproject = (IProject) iresource;
			members = iproject.members();
		} else if (type == IResource.FOLDER) {
			IFolder ifolder = (IFolder) iresource;
			members = ifolder.members();
		}
		if (members != null) {
			for (int i = 0; i < members.length; i++) {
				if (members[i].getType() == IResource.FILE) {
					addFileToMap(ifilesMap, (IFile) members[i], fileFilter);
				} else {
					addFilesToMap(ifilesMap, members[i], fileFilter);
				}
			}
		}
	}

	public static void createMarker(final String markerType,
			final IResource resource, final Map<String, Object> attribs) {

		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(markerType);
				marker.setAttributes(attribs);
			}
		};

		try {
			resource.getWorkspace().run(r, null, IWorkspace.AVOID_UPDATE, null);
		} catch (CoreException e) {
			System.err.println("Unable to create marker type=" + markerType);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
