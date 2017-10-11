package org.archicontribs.reports.html;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;

import com.archimatetool.editor.actions.AbstractModelSelectionHandler;
import com.archimatetool.editor.ui.components.ExtendedWizardDialog;
import com.archimatetool.model.IArchimateModel;

/**
 * 
 * @author Juan Carlos Nova
 *
 */
public class FolderReportHandler extends AbstractModelSelectionHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IArchimateModel model = getActiveArchimateModel();
        if(model != null) {
            WizardDialog dialog = new ExtendedWizardDialog(workbenchWindow.getShell(),
                    new ExportViewsWizard(model),
                    "ExportViewsWizard"); //$NON-NLS-1$
            dialog.open();
        }

        return null;
    }
    
}
