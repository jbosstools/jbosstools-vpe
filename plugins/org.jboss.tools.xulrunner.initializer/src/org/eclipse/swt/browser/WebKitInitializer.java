package org.eclipse.swt.browser;


public class WebKitInitializer {

	private static final String USE_WEB_KIT_GTK = "org.eclipse.swt.browser.UseWebKitGTK"; //$NON-NLS-1$
	public static final String WEBKIT_ENABLED_BY_VPE_USER_SYSTEM_PROPERTY = "org.jboss.tools.vpe.webkit.enabledbyuser"; //$NON-NLS-1$
	public static boolean WEBKIT_ENABLED_BY_USER = "true".equals(System.getProperty(WEBKIT_ENABLED_BY_VPE_USER_SYSTEM_PROPERTY)); //$NON-NLS-1$
	
	static {
		String useWebKitGTK = System.getProperty(USE_WEB_KIT_GTK);
		if (useWebKitGTK == null) {
			System.setProperty(USE_WEB_KIT_GTK, "false"); //$NON-NLS-1$
		}
	}

}
