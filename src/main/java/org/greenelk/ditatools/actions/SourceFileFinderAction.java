/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.actions;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.greenelk.ditatools.common.Utils;
import org.greenelk.ditatools.eclipse.DITAToolsPlugin;

public class SourceFileFinderAction implements IWorkbenchWindowActionDelegate {

	String filetypes = "dita|html|htm|gif|png|jpeg|jpg";
	String navigators[] = { "org.eclipse.ui.navigator.ProjectExplorer",
			"org.eclipse.jdt.ui.PackageExplorer",
			"org.eclipse.ui.views.ResourceNavigator" };

	Map<String, IFile> allfiles = null;

	@Override
	public void run(IAction action) {

		String textData = getClipboardText();

		if (textData != null) {

			String fileName = null;
			String[] tokens = textData.split("/|\\?");

			for (int t = 0; t < tokens.length; t++) {
				if (tokens[t].matches("([^\\s]+(\\.(?i)(" + filetypes + "))$)")) {
					fileName = tokens[t];
					break;
				}
			}
			if (fileName != null) {

				IFile ff = findFile(fileName);

				if (ff != null) {
					IWorkbenchPage page = DITAToolsPlugin.getDefault()
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();

					for (int i = 0; i < navigators.length; i++) {
						IViewPart view = page.findView(navigators[i]);
						if (view != null) {
							ISetSelectionTarget target = (ISetSelectionTarget) view;
							target.selectReveal(new StructuredSelection(ff));
						}
					}

				} else {
					createMarker(IMarker.SEVERITY_ERROR,
							"File not found from clipboard text : " + textData);
				}
			} else {
				createMarker(IMarker.SEVERITY_ERROR,
						"File not found from clipboard text : " + textData);
			}

		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		List<?> projects = Arrays.asList(ResourcesPlugin.getWorkspace()
				.getRoot().getProjects());
		// Collect map of all files
		allfiles = Utils.getFileMap(projects, filetypes);
	}

	protected void createMarker(final int severity, final String message) {

		Map<String, Object> attribs = new HashMap<String, Object>();
		attribs.put(IMarker.SEVERITY, severity);
		attribs.put(IMarker.MESSAGE, message);
		attribs.put(IMarker.SEVERITY, severity);

		Utils.createMarker(DITAToolsPlugin.PLUGIN_ID + ".filefinderproblem",
				ResourcesPlugin.getWorkspace().getRoot(), attribs);
	}

	private IFile findFile(String fileName) {
		IFile ff = allfiles.get(fileName);
		if (ff == null) {
			ff = allfiles.get(fileName.replace(".html", ".dita"));
			if (ff == null) {
				ff = allfiles.get(fileName.replace(".htm", ".dita"));
			}
		}
		return ff;
	}

	private String getClipboardText() {
		Display display = DITAToolsPlugin.getDefault().getWorkbench()
				.getDisplay();
		Clipboard clipboard = new Clipboard(display);
		TextTransfer textTransfer = TextTransfer.getInstance();
		String textData = (String) clipboard.getContents(textTransfer);
		try {
			textData = java.net.URLDecoder.decode(textData, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}
		return textData;

	}

}
