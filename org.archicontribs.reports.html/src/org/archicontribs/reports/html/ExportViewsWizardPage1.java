package org.archicontribs.reports.html;

import java.io.File;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.archimatetool.editor.ui.UIUtils;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.impl.ArchimateDiagramModel;

public class ExportViewsWizardPage1 extends WizardPage {

	private IArchimateModel fModel;

	private Text fFolderTextField;
	
	private Folder folderTree;
	private CheckedTreeSelectionDialog foldersDialog;

	public CheckedTreeSelectionDialog getFoldersDialog() {
		return foldersDialog;
	}

	public ExportViewsWizardPage1(IArchimateModel fModel) {
		super("First Page");
		setTitle(Messages.HTMLViewReportExporter_5);
		setDescription(Messages.HTMLViewReportExporter_6);
		this.fModel = fModel;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		setControl(container);

		// Grupo para seleccion de vista
		Group viewGroup = new Group(container, SWT.NULL);
		viewGroup.setLayout(new GridLayout(2, false));
		viewGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(viewGroup, SWT.NULL);
		label.setText(Messages.HTMLViewReportExporter_2);

		Button viewsButton = new Button(viewGroup, SWT.PUSH);
		viewsButton.setText(Messages.HTMLViewReportExporter_3);
		viewsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openCheckedTreeSelectionDialog();
			}
		});

		Group filesGroup = new Group(container, SWT.NULL);
		filesGroup.setLayout(new GridLayout(3, false));
		filesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(filesGroup, SWT.NULL);
		label.setText(Messages.HTMLViewReportExporter_4);

		fFolderTextField = new Text(filesGroup, SWT.BORDER | SWT.SINGLE);
		fFolderTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Single text control so strip CRLFs
		UIUtils.conformSingleTextControl(fFolderTextField);

		Button folderButton = new Button(filesGroup, SWT.PUSH);
		folderButton.setText(Messages.HTMLViewReportExporter_7);
		folderButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String folderPath = chooseFolderPath();
				if (folderPath != null) {
					fFolderTextField.setText(folderPath);
				}
			}
		});
	}

	private int openCheckedTreeSelectionDialog() {
		foldersDialog = new CheckedTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new FolderBaseWorkbenchContentProvider());

		foldersDialog.setTitle(Messages.HTMLViewReportExporter_8);

		foldersDialog.setMessage(Messages.HTMLViewReportExporter_9);

		fillTree();
		foldersDialog.setInput(folderTree);
		return foldersDialog.open();
	}
	
	private void fillTree() {
		if(!fModel.getArchimateModel().getFolders().isEmpty()) {
			folderTree = new Folder();
			folderTree.setName( fModel.getArchimateModel().getName() );
			folderTree.setId( fModel.getArchimateModel().getId() );
			for (IFolder folder : fModel.getArchimateModel().getFolders()) {
				folderTree.getFolders().add( getFolders(folder) );
			}
		}
	}

	private Folder getFolders(IFolder folder) {
		Folder estructura = new Folder(folder.getName(), folder.getId());
		if (!folder.getFolders().isEmpty()) {
			for (IFolder subfolder : folder.getFolders()) {
				estructura.getFolders().add(getFolders(subfolder));
			}
		}
		if(!folder.getElements().isEmpty()) {
			for(EObject object : folder.getElements()) {
				if(object instanceof IArchimateConcept) {
					estructura.getViews().add( new View( ((IArchimateConcept)object).getName() ,  ((IArchimateConcept)object).getId() ) );
				} else if( object instanceof ArchimateDiagramModel ) {
					estructura.getViews().add( new View( ((ArchimateDiagramModel)object).getName() ,  ((ArchimateDiagramModel)object).getId() ) );
				}
				
			}
		}
		return estructura;
	}

	public File getExportFolder() {
		return new File(fFolderTextField.getText());
	}

	String getExportFolderPath() {
		return fFolderTextField.getText();
	}

	private String chooseFolderPath() {
		DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
		dialog.setText(Messages.HTMLViewReportExporter_5);
		dialog.setMessage(Messages.HTMLViewReportExporter_10);
		return dialog.open();
	}

}
