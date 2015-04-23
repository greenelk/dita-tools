/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.actions;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.greenelk.ditatools.common.Utils;
import org.greenelk.ditatools.eclipse.DITAToolsPlugin;
import org.greenelk.ditatools.eclipse.HREFPreferencePage;
import org.greenelk.ditatools.eclipse.WFFPreferencePage;
import org.greenelk.ditatools.eclipse.WUPreferencePage;

public class WebFileFinderAction extends ContributionItem {

	private static final String MENU_TITLE = "DITA Web File Finder";
	protected ISelection selection;
	protected String currentPrefix;

	public static final String URL_EXAMPLE_KC = "http://www.company.com/v123";
	public static final String URL_EXAMPLE_STAGING = "http://intranet.company.com:9080/v124/index.jsp?topic=";

	public WebFileFinderAction() {
	}

	public WebFileFinderAction(String id) {
		super(id);
	}

	@Override
	public void fill(Menu menu, int index) {
		// Here you could get selection and decide what to do
		// You can also simply return if you do not want to show a menu

		String[] prefixes = DITAToolsPlugin.getDefault().getWFFPreference();
		// create the menu based on the number of prefixes specified in the
		// preferences
		if (prefixes.length > 1) {

			MenuItem subMenuItem = new MenuItem(menu, SWT.CASCADE, index);
			subMenuItem.setText(MENU_TITLE);
			Menu submenu = new Menu(Display.getDefault().getActiveShell(),
					SWT.DROP_DOWN);
			subMenuItem.setMenu(submenu);
			for (int i = 0; i < prefixes.length; i++) {
				MenuItem prefixItem = new MenuItem(submenu, SWT.CHECK);
				prefixItem.setText(prefixes[i]);
				prefixItem.setData(prefixes[i]);
				prefixItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						wffSelected(e);
					}
				});
			}
		} else {
			// create single item
			MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
			menuItem.setText(MENU_TITLE);
			if (prefixes.length == 1) {
				menuItem.setData(prefixes[0]);
			} else {
				menuItem.setData(null);
			}
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					wffSelected(e);
				}
			});
		}
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	private void wffSelected(SelectionEvent e) {

		MenuItem m = (MenuItem) e.getSource();
		if (m.getData() instanceof String) {
			currentPrefix = (String) m.getData();
		} else {
			String[] prefixes = DITAToolsPlugin.getDefault().getWFFPreference();
			if (prefixes.length == 0) {
				currentPrefix = null;
			} else {
				currentPrefix = prefixes[0];
			}

		}
		if (currentPrefix == null) {
			showPrefixPreferences();
		} else {
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			selection = window.getActivePage().getSelection();
			Job job = new Job("Running web file finder") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					return openBrowser(monitor);
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	private IStatus openBrowser(IProgressMonitor monitor) {

		final IWorkbenchBrowserSupport browserSupport = PlatformUI
				.getWorkbench().getBrowserSupport();
		List<?> resources = (selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection)
				.toList() : Collections.EMPTY_LIST;
		String browserId = null;

		Vector<IFile> ifiles = Utils.getFileResources(resources);
		monitor.beginTask(
				"Attempting to open " + ifiles.size() + " DITA files",
				ifiles.size());
		for (IFile ifile : ifiles) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			String urlstring = ifile.getFullPath().toString();
			urlstring = urlstring.replace(".dita", ".html");
			urlstring = currentPrefix + urlstring;
			try {
				URL url = new URL(urlstring);
				browserSupport.createBrowser(browserId).openURL(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			monitor.worked(1);
		}
		return Status.OK_STATUS;
	}

	private void showPrefixPreferences() {
		String question = "This action needs a url prefix to be set up in the preferences panel.\nExamples of url prefixes are:\n\n"

				+ "Knowledge Centre - "
				+ URL_EXAMPLE_KC
				+ "\n"
				+ "Staging server - "
				+ URL_EXAMPLE_STAGING
				+ "\n\n"
				+ "Do you want to set the preference now?";

		JTextArea textArea = new JTextArea();
		textArea.setText(question); // A string of ~100 words
									// "Lorem ipsum...\nFin."
		textArea.setColumns(50);
		textArea.setOpaque(false);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setSize(textArea.getPreferredSize().width, 1);

		int selectedOption = JOptionPane.showConfirmDialog(null, textArea,
				"URL prefix preference not set", JOptionPane.YES_NO_OPTION);

		if (selectedOption == JOptionPane.YES_OPTION) {
			PreferencesUtil
					.createPreferenceDialogOn(
							Display.getDefault().getActiveShell(),
							WFFPreferencePage.PAGE_ID,
							new String[] { HREFPreferencePage.PAGE_ID,
									WFFPreferencePage.PAGE_ID,
									WUPreferencePage.PAGE_ID }, null).open();
		} else {

		}
	}
}
