/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.ui.test.editor;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.preview.editor.VpvEditorController;
import org.jboss.tools.vpe.preview.editor.util.VpvSelectionUtil;
import org.jboss.tools.vpe.ui.test.VpeUiTests;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Junit test for JBIDE-8115
 * 
 * @author mareshkau
 *
 */
public class MultipleSelectionTest extends VpeTest{
 
	private static final String TEST_CASE="selection/jbide-8115-test-case.html"; //$NON-NLS-1$
	
	public MultipleSelectionTest() {
	}
	@Test
	public void testMultipleSelectionForSimplePage() throws CoreException, IOException{
        IFile file = (IFile) TestUtil.getComponentPath(TEST_CASE,
        		VpeUiTests.IMPORT_PROJECT_NAME);
        IEditorInput input = new FileEditorInput(file);
		JSPMultiPageEditor part = openEditor(input);
		ITextViewer viewer = part.getSourceEditor().getTextViewer();
		
		int startSelectionOffcet = TestUtil.getLinePositionOffcet(viewer, 4, 1);
		int length = TestUtil.getLinePositionOffcet(viewer, 4, 45)-startSelectionOffcet;
		viewer.setSelectedRange(startSelectionOffcet, length);
		VpvEditorController vpeController = TestUtil.getVpvController(part);
        vpeController.sourceSelectionChanged();
        Node node = VpvSelectionUtil.getSelectedNode(vpeController.getPageContext());
        NodeList selectedNodes = node.getChildNodes();
        /*
         * When Git repository is checked out on Windows OS
         * git could add window style caret return symbol,
         * after that additional text nodes appear.
         * To fix it: test page was changed.
         */
        assertEquals("Should be selected ",1,selectedNodes.getLength()); //$NON-NLS-1$
	}
}
