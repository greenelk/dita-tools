/*
 * This file is part of the greenelk/dita-tools GitHub project.
 * See the accompanying license.txt file for applicable licenses.
 */
package org.greenelk.ditatools.eclipse;

import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DITAToolsPlugin extends AbstractUIPlugin {

	public static final String COPYRIGHT = "Copyright GreenElk 2015";

	public static final String PREFERENCE_DELIMITER = "|";
	// The plug-in ID
	public static final String PLUGIN_ID = "org.greenelk.ditatools"; //$NON-NLS-1$

	public static final String WEB_FILE_FINDER_PREFERENCE = PLUGIN_ID
			+ "webfilefinder";
	public static final String HREF_FILE_FILTER_PREFERENCE = PLUGIN_ID
			+ "href.filefilter";
	public static final String WHERE_USED_FILE_FILTER_PREFERENCE = PLUGIN_ID
			+ "where.used.filefilter";
	public static final String WHERE_USED_XREF_FILTER_PREFERENCE = PLUGIN_ID
			+ "where.used.xreffilter";

	public static final String DEFAULT_WFF_PREFERENCE = "http://intranet.company.com:9080/v124/index.jsp?topic=";
	public static final String DEFAULT_HREF_FILE_FILTER_PREFERENCE = "ditamap|dita|html|htm";
	public static final String DEFAULT_WU_FILE_FILTER_PREFERENCE = "jpg|gif|png|dita";
	public static final String DEFAULT_WU_XREF_FILTER_PREFERENCE = "ditamap|dita|html|htm";

	// The shared instance
	private static DITAToolsPlugin plugin;

	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(HREF_FILE_FILTER_PREFERENCE,
				DEFAULT_HREF_FILE_FILTER_PREFERENCE);
		store.setDefault(WHERE_USED_FILE_FILTER_PREFERENCE,
				DEFAULT_WU_FILE_FILTER_PREFERENCE);
		store.setDefault(WHERE_USED_XREF_FILTER_PREFERENCE,
				DEFAULT_WU_XREF_FILTER_PREFERENCE);
		store.setDefault(WEB_FILE_FINDER_PREFERENCE, DEFAULT_WFF_PREFERENCE);
	}

	/**
	 * Return the HREF preference default as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getDefaultHREFFileFilterPreference() {
		return convert(getPreferenceStore().getDefaultString(
				HREF_FILE_FILTER_PREFERENCE));
	}

	/**
	 * Return the HREF preference as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getHREFFileFilterPreference() {
		return convert(getPreferenceStore().getString(
				HREF_FILE_FILTER_PREFERENCE));
	}

	public String getHREFFileFilterPreferenceAsRegExp() {
		String regexp = getPreferenceStore().getString(
				HREF_FILE_FILTER_PREFERENCE);
		return regexp.substring(0, regexp.length() - 1);
	}

	/**
	 * Set the HREF preference
	 * 
	 * @param String
	 *            [] elements - the Strings to be converted to the preference
	 *            value
	 */
	public void setHREFFileFilterPreference(String[] elements) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(PREFERENCE_DELIMITER);
		}

		getPreferenceStore().setValue(HREF_FILE_FILTER_PREFERENCE,
				buffer.toString());
	}

	/**
	 * Return the WFF preference default as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getDefaultWFFPreference() {
		return convert(getPreferenceStore().getDefaultString(
				WEB_FILE_FINDER_PREFERENCE));
	}

	/**
	 * Return the WFF preference as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getWFFPreference() {
		return convert(getPreferenceStore().getString(
				WEB_FILE_FINDER_PREFERENCE));
	}

	/**
	 * Set the WFF preference
	 * 
	 * @param String
	 *            [] elements - the Strings to be converted to the preference
	 *            value
	 */
	public void setWFFPreference(String[] elements) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(PREFERENCE_DELIMITER);
		}

		getPreferenceStore().setValue(WEB_FILE_FINDER_PREFERENCE,
				buffer.toString());
	}

	/**
	 * Return the WU preference default as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getDefaultWUFileFilterPreference() {
		return convert(getPreferenceStore().getDefaultString(
				WHERE_USED_FILE_FILTER_PREFERENCE));
	}

	/**
	 * Return the WU preference as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getWUFileFilterPreference() {
		return convert(getPreferenceStore().getString(
				WHERE_USED_FILE_FILTER_PREFERENCE));
	}

	public String getWUFileFilterPreferenceAsRegExp() {
		String regexp = getPreferenceStore().getString(
				WHERE_USED_FILE_FILTER_PREFERENCE);
		return regexp.substring(0, regexp.length() - 1);
	}

	/**
	 * Set the HREF preference
	 * 
	 * @param String
	 *            [] elements - the Strings to be converted to the preference
	 *            value
	 */
	public void setWUFileFilterPreference(String[] elements) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(PREFERENCE_DELIMITER);
		}

		getPreferenceStore().setValue(WHERE_USED_FILE_FILTER_PREFERENCE,
				buffer.toString());
	}

	/**
	 * Return the WU preference default as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getDefaultWUXREFFilterPreference() {
		return convert(getPreferenceStore().getDefaultString(
				WHERE_USED_XREF_FILTER_PREFERENCE));
	}

	/**
	 * Return the WU preference as an array of Strings.
	 * 
	 * @return String[]
	 */
	public String[] getWUXREFFilterPreference() {
		return convert(getPreferenceStore().getString(
				WHERE_USED_XREF_FILTER_PREFERENCE));
	}

	public String getWUXREFFilterPreferenceAsRegExp() {
		String regexp = getPreferenceStore().getString(
				WHERE_USED_XREF_FILTER_PREFERENCE);
		return regexp.substring(0, regexp.length() - 1);
	}

	/**
	 * Set the HREF preference
	 * 
	 * @param String
	 *            [] elements - the Strings to be converted to the preference
	 *            value
	 */
	public void setWUXREFFilterPreference(String[] elements) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(PREFERENCE_DELIMITER);
		}

		getPreferenceStore().setValue(WHERE_USED_XREF_FILTER_PREFERENCE,
				buffer.toString());
	}

	/**
	 * Convert the supplied PREFERENCE_DELIMITER delimited String to a String
	 * array.
	 * 
	 * @return String[]
	 */
	private String[] convert(String preferenceValue) {
		StringTokenizer tokenizer = new StringTokenizer(preferenceValue,
				PREFERENCE_DELIMITER);
		int tokenCount = tokenizer.countTokens();
		String[] elements = new String[tokenCount];
		for (int i = 0; i < tokenCount; i++) {
			elements[i] = tokenizer.nextToken();
		}

		return elements;
	}

	/**
	 * The constructor
	 */
	public DITAToolsPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static DITAToolsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
