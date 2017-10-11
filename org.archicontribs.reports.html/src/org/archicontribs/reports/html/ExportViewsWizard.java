package org.archicontribs.reports.html;

import java.io.IOException;

import org.eclipse.jface.wizard.Wizard;

import com.archimatetool.model.IArchimateModel;

public class ExportViewsWizard extends Wizard {

	private IArchimateModel fModel;

	private ExportViewsWizardPage1 fPage1;

	public ExportViewsWizard(IArchimateModel model) {
		setWindowTitle(Messages.HTMLViewReportExporter_1);
		fModel = model;
	}

	@Override
	public void addPages() {
		fPage1 = new ExportViewsWizardPage1(fModel);
		addPage(fPage1);
	}

	@Override
	public boolean performFinish() {

		try {
			HTMLFolderReporter exporter = new HTMLFolderReporter(fModel);
			fillHashMaps(exporter);
			// Export the HTML
			exporter.export(fPage1.getExportFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void fillHashMaps(HTMLFolderReporter exporter) {
		if (fPage1.getFoldersDialog() != null && fPage1.getFoldersDialog().getResult() != null) {
			for (Object objectFolder : fPage1.getFoldersDialog().getResult()) {
				recursiveFill(objectFolder, exporter);
			}
		}
	}

	private void recursiveFill(Object objectFolder, HTMLFolderReporter exporter) {
		exporter.getNotIncludeFolders().put(((Folder) objectFolder).getId(), ((Folder) objectFolder).getName());
		// validation if the folder have subfolders
		if (!((Folder) objectFolder).getFolders().isEmpty()) {
			for (Object object : ((Folder) objectFolder).getFolders()) {
				recursiveFill(object, exporter);
			}
		}
		// validation if the folder have views
		if (!((Folder) objectFolder).getViews().isEmpty()) {
			for (Object objectView : ((Folder) objectFolder).getViews()) {
				exporter.getNotIncludeViews().put(((View) objectView).getId(), ((View) objectView).getName());
			}
		}
	}

}
