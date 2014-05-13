/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.ISources;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.jboss.tools.jst.web.ui.internal.editor.editor.IVisualEditor;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;

/**
 * Handler which disables buttons when VPE part not visible
 * 
 * @author mareshkau
 * 
 */
public abstract class VisualPartAbstractHandler extends AbstractHandler
		implements IElementUpdater {

	public static final String VPE_CATEGORY_ID = "org.jboss.tools.vpe.category"; //$NON-NLS-1$

	@Override
	public void setEnabled(Object evalCtx) {
		boolean enabled = false;
		if (evalCtx instanceof IEvaluationContext) {
			IEvaluationContext context = (IEvaluationContext) evalCtx;
			Object activeEditor = context
					.getVariable(ISources.ACTIVE_EDITOR_NAME);
			if (activeEditor instanceof JSPMultiPageEditor) {
				IVisualEditor ve = ((JSPMultiPageEditor) activeEditor)
						.getVisualEditor();
				if (ve != null && ve.getController() != null) {
					enabled = ve.getController().isVisualEditorVisible();
				}
			}
		}
		setBaseEnabled(enabled);
	}

	public void updateElement(UIElement element, Map parameters) {
		fireHandlerChanged(new HandlerEvent(this, true, false));
	}
}
