/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.reports.html;

import java.io.IOException;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.archimatetool.editor.model.IModelExporter;
import com.archimatetool.editor.ui.components.ExtendedWizardDialog;
import com.archimatetool.model.IArchimateModel;

/**
 * Exporter
 * 
 * @author Juan Carlos Nova
 */
public class HTMLExporter implements IModelExporter {

	public HTMLExporter() {
	}

	@Override
	public void export(IArchimateModel model) throws IOException {
		WizardDialog dialog = new ExtendedWizardDialog(Display.getCurrent().getActiveShell(),
				new ExportViewsWizard(model), "ExportViewsWizard"); //$NON-NLS-1$
		dialog.open();
	}
}
