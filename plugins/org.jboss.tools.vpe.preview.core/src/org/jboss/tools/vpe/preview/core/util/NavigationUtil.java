/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.preview.core.util;

import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.vpe.preview.core.Activator;
import org.jboss.tools.vpe.preview.core.transform.DomUtil;
import org.jboss.tools.vpe.preview.core.transform.TransformUtil;
import org.jboss.tools.vpe.preview.core.transform.VpvDomBuilder;
import org.jboss.tools.vpe.preview.core.transform.VpvVisualModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
@SuppressWarnings("restriction")
public final class NavigationUtil {
	private static final String VPV_SELECTION_STYLE_ID = "VPV_STYLESHEET_ID";  //$NON-NLS-1$
	
	private NavigationUtil() {
	}
		
	public static void disableLinks(Browser browser) {
		if (browser != null && !browser.isDisposed()) {
			browser.execute("(setTimeout(function() { " +  //$NON-NLS-1$
								"var anchors = document.getElementsByTagName('a');" + //$NON-NLS-1$
								"for (var i = 0; i < anchors.length; i++) {" + //$NON-NLS-1$
									"anchors[i].href = 'javascript: void(0);';" + //$NON-NLS-1$
									"anchors[i].target = '';" + //$NON-NLS-1$
								"};" + //$NON-NLS-1$
					  		"}, 10))();"); //$NON-NLS-1$
		}
	}
	
	public static void disableInputs(Browser browser) {
		if (browser != null && !browser.isDisposed()) {
			String disableInputs = "function() {" + //$NON-NLS-1$
					                  "var inputs = document.getElementsByTagName('INPUT');" + //$NON-NLS-1$
					        	      "for (var i = 0; i < inputs.length; i++) {" + //$NON-NLS-1$
		                                 "inputs[i].blur();" + //$NON-NLS-1$ Disabling autofocus
		                                 "inputs[i].disabled = true;" + //$NON-NLS-1$
		                              "}" + //$NON-NLS-1$
		                           "}"; //$NON-NLS-1$
			OS platform = PlatformUtil.getOs();
			if (OS.WINDOWS.equals(platform)) {
				browser.execute("(" + disableInputs + ")();"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				int timeout = 30;
				if(OS.LINUX.equals(platform)) {
					timeout = 150;// timeout increased for old xulrunner 
				}
				browser.execute("(setTimeout(" + disableInputs + ", "+ timeout +"))();"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}

	public static void outlineSelectedElement(Browser browser, Long currentSelectionId) {
		if (browser != null && !browser.isDisposed()) {
			String styleAttributeSelector;
			if (currentSelectionId == null) {
				styleAttributeSelector = ""; //$NON-NLS-1$
			} else {
				styleAttributeSelector = "'[" + VpvDomBuilder.ATTR_VPV_ID + "=\"" + currentSelectionId + "\"] {outline: 1px solid blue; border: 1px solid blue;  z-index: 2147483638;}'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			String outlineJsFunction = "function() {" + //$NON-NLS-1$
					                       "var style=document.getElementById('" + VPV_SELECTION_STYLE_ID + "');" + //$NON-NLS-1$ //$NON-NLS-2$
					                       "if (!style) {" + //$NON-NLS-1$
					                            "style = document.createElement('STYLE');" + //$NON-NLS-1$
					                            "style.type = 'text/css';" + //$NON-NLS-1$
					                       "}" + //$NON-NLS-1$
					                       "style.id = '" + VPV_SELECTION_STYLE_ID + "';" + //$NON-NLS-1$ //$NON-NLS-2$
					                       "style.innerHTML = " + styleAttributeSelector + ";" + //$NON-NLS-1$ //$NON-NLS-2$
					                       "var head = document.head || document.getElementsByTagName('head')[0] ;"+ //$NON-NLS-1$
					                       "head.appendChild(style);" + //$NON-NLS-1$
					                   "}"; //$NON-NLS-1$
			
			if (OS.WINDOWS.equals(PlatformUtil.getOs())) {
				browser.execute("(" + outlineJsFunction + ")();"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				// JBIDE-17155 Visual Preview: no selection after element changed on Mac Os and Linux
				browser.execute("(setTimeout(" + outlineJsFunction + ", 10))();"); //$NON-NLS-1$//$NON-NLS-2$
			}
		}
	}

	public static void disableAlert(Browser browser) {
		browser.execute("window.alert = function() {};"); //$NON-NLS-1$
	}
	
	public static String removeAnchor(String url) {
		int index = url.lastIndexOf('#');
		if (index != -1) {
		    url = url.substring(0, index);
		} 
		return url;
	}
	
	public static void navigateToVisual(IEditorPart currentEditor, Browser browser, VpvVisualModel visualModel, int x, int y) {
		String stringToEvaluate = ""; //$NON-NLS-1$
		if (OS.LINUX.equals(PlatformUtil.getOs())) {
			/* outerHTML is not available with XulRunner we shipping, so <code>result</code> variable will be null
			 * because we make it default browser on Linux this workaround is used
			 * @see JBIDE-17454
			 */
			stringToEvaluate = "var selected = document.elementFromPoint(" + x + ", " + y + ");"+ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"var temp = document.createElement('div');"+ //$NON-NLS-1$
			"temp.appendChild(selected.cloneNode(true));"+ //$NON-NLS-1$
			"return temp.innerHTML;"; //$NON-NLS-1$
		} else {
			stringToEvaluate = "return document.elementFromPoint(" + x + ", " + y + ").outerHTML;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		String result = (String) browser.evaluate(stringToEvaluate);
		if (result != null) {
			String selectedElementId = TransformUtil.getSelectedElementId(result, "(?<=data-vpvid=\").*?(?=\")"); //$NON-NLS-1$
	
			Long id = (selectedElementId != null && !selectedElementId.isEmpty()) //avoiding NumberFormatException
					  ? Long.parseLong(selectedElementId)
					  : null;
			NavigationUtil.outlineSelectedElement(browser, id);
	
			String fileExtension = EditorUtil.getFileExtensionFromEditor(currentEditor);
	
			if (SuitableFileExtensions.isHTML(fileExtension)) {
				try {
					Node visualNode = TransformUtil.getVisualNodeByVpvId(visualModel, selectedElementId);
					Node sourseNode = TransformUtil.getSourseNodeByVisualNode(visualModel, visualNode);
	
					if (sourseNode != null && sourseNode instanceof IDOMNode) {
						int startOffset = ((IDOMNode) sourseNode).getStartOffset();
						int endOffset = ((IDOMNode) sourseNode).getEndOffset();
	
						StructuredTextEditor editor = (StructuredTextEditor) currentEditor.getAdapter(StructuredTextEditor.class);
						editor.selectAndReveal(startOffset, endOffset - startOffset);
	
					}
	
				} catch (XPathExpressionException e) {
					Activator.logError(e);
				}
			}
		}
	}
	
	public static void updateSelectionAndScrollToIt(ISelection currentSelection, Browser browser, VpvVisualModel visualModel) {
		if (currentSelection instanceof IStructuredSelection) {
			Node sourceNode = EditorUtil.getNodeFromSelection((IStructuredSelection) currentSelection);
			Long currentSelectionId = getIdForSelection(sourceNode, visualModel);
			NavigationUtil.scrollToId(browser, currentSelectionId);
			NavigationUtil.outlineSelectedElement(browser, currentSelectionId);
		}
	}
	
	private static void scrollToId(Browser browser, Long currentSelectionId) {
		if (browser != null && !browser.isDisposed()) {
			if (currentSelectionId != null) {
				browser.execute("(setTimeout(function() {" + //$NON-NLS-1$								
									"var selectedElement = document.querySelector('[" + VpvDomBuilder.ATTR_VPV_ID + "=\"" + currentSelectionId + "\"]');" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									"selectedElement.scrollIntoView(true);" + //$NON-NLS-1$
								"}, 300))();");   //$NON-NLS-1$;
			}
		}
	}
	
	private static Long getIdForSelection(Node selectedSourceNode, VpvVisualModel visualModel) {
		Long id = null;
		if (selectedSourceNode != null && visualModel != null) {
			Map<Node, Node> sourceVisuaMapping = visualModel.getSourceVisualMapping();

			Node visualNode = null;
			Node sourceNode = selectedSourceNode;
			do {
				visualNode = sourceVisuaMapping.get(sourceNode);
				sourceNode = DomUtil.getParentNode(sourceNode);
			} while (visualNode == null && sourceNode != null);

			if (!(visualNode instanceof Element)) { // text node, comment, etc
				visualNode = DomUtil.getParentNode(visualNode); // should be element now or null
			}

			String idString = null;
			if (visualNode instanceof Element) {
				Element elementNode = (Element) visualNode;
				idString = elementNode.getAttribute(VpvDomBuilder.ATTR_VPV_ID);
			}

			if (idString != null && !idString.isEmpty()) {
				try {
					id = Long.parseLong(idString);
				} catch (NumberFormatException e) {
					Activator.logError(e);
				}
			}
		}
		return id;
	}

}
