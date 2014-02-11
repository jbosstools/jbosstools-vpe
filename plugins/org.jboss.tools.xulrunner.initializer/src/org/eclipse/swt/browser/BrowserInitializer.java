package org.eclipse.swt.browser;

import org.eclipse.core.runtime.Platform;

public class BrowserInitializer {

	private static final String PROPERTY_DEFAULTTYPE = "org.eclipse.swt.browser.DefaultType"; //$NON-NLS-1$
	private static final String SWT_GTK3 = "SWT_GTK3"; //$NON-NLS-1$

	public static boolean isGTK3() {
		if (Platform.WS_GTK.equals(Platform.getWS())) {
			String gtk3 = System.getProperty(SWT_GTK3);
			if (gtk3 == null) {
				gtk3 = System.getenv(SWT_GTK3);
			}
			return !"0".equals(gtk3); //$NON-NLS-1$
		}
		return false;
	}

	static {
		/* Under Linux instantiation of WebKit should be avoided,
		 * because WebKit and XULRunner running simultaneously
		 * may cause native errors.
		 * 
		 * Also see JBIDE-9144, JBIDE-10185 and JBDS-2900. 	 */
		if (Platform.OS_LINUX.equals(Platform.getOS())) {
			String defaultType = System.getProperty(PROPERTY_DEFAULTTYPE);
			if (defaultType == null) {
				defaultType = isGTK3() ? "webkit" : "mozila"; //$NON-NLS-1$ //$NON-NLS-2$
				System.setProperty(PROPERTY_DEFAULTTYPE, defaultType);
			}
		}
	}
}
