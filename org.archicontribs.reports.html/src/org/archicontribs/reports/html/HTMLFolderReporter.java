package org.archicontribs.reports.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.osgi.framework.Bundle;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.diagram.util.DiagramUtils;
import com.archimatetool.editor.diagram.util.ModelReferencedImage;

import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;


/**
 * 
 * @author Juan Carlos Nova
 *
 */
public class HTMLFolderReporter {

public static File PREVIEW_FOLDER = new File(ArchiPlugin.INSTANCE.getUserDataFolder(), "html-report-preview"); //$NON-NLS-1$
    
    private IArchimateModel fModel;
    private HashMap<String, String> notIncludeFolders; 
    private HashMap<String, String> notIncludeViews;
    
    
    
    public void export(File target) throws IOException {
    	if(target == null) {
            return;
        }
        //updateFModel();
        File file = createReport(target, "index.html"); //$NON-NLS-1$
        
        // Open it in external Browser
        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
        try {
            IWebBrowser browser = support.getExternalBrowser();
            browser.openURL(file.toURI().toURL());
        }
        catch(PartInitException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Clean up preview files
     * @throws IOException
     */
    public static void cleanPreviewFiles() throws IOException {
        FileUtils.deleteFolder(PREVIEW_FOLDER);
    }

    public File createReport(File targetFolder, String indexFileName) throws IOException {
    	// Copy HTML skeleton to target
        copyHTMLSkeleton(targetFolder);
        
        // Copy hints files from the help plug-in
        copyHintsFiles(targetFolder);
        
        // Create sub-folders
        File elementsFolder = new File(targetFolder, fModel.getId() + "/elements"); //$NON-NLS-1$
        elementsFolder.mkdirs(); // Make dir
        
        File viewsFolder = new File(targetFolder, fModel.getId() + "/views"); //$NON-NLS-1$
        viewsFolder.mkdirs(); // Make dir
        
        File imagesFolder = new File(targetFolder, fModel.getId() + "/images"); //$NON-NLS-1$
        imagesFolder.mkdirs(); // Make dir
             
        // Instantiate templates files
        File mainFile = new File(ArchiReportsPlugin.INSTANCE.getTemplatesFolder(), "st/main.stg"); //$NON-NLS-1$
        STGroupFile groupFile = new STGroupFile(mainFile.getAbsolutePath(), '^', '^');
        ST stFrame = groupFile.getInstanceOf("frame"); //$NON-NLS-1$
        
        groupFile.registerRenderer(String.class, new StringRenderer());
        
        // Write model purpose and properties html
        writeElement(new File(elementsFolder, "model.html"), stFrame, fModel); //$NON-NLS-1$
        
        // Write all folders
        writeFolders(elementsFolder, stFrame, fModel.getFolders());
        
        // Write Diagrams and images
        writeDiagrams(imagesFolder, viewsFolder, stFrame);
        
        // Write root model.html frame
        File indexFile = new File(targetFolder, indexFileName);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(indexFile), "UTF8"); //$NON-NLS-1$
        
        ST stModel = groupFile.getInstanceOf("modelreport"); //$NON-NLS-1$

        stModel.add("model", fModel); //$NON-NLS-1$
        stModel.add("strategyFolder", fModel.getFolder(FolderType.STRATEGY)); //$NON-NLS-1$
        stModel.add("businessFolder", fModel.getFolder(FolderType.BUSINESS)); //$NON-NLS-1$
        stModel.add("applicationFolder", fModel.getFolder(FolderType.APPLICATION)); //$NON-NLS-1$
        stModel.add("technologyFolder", fModel.getFolder(FolderType.TECHNOLOGY)); //$NON-NLS-1$
        stModel.add("motivationFolder", fModel.getFolder(FolderType.MOTIVATION)); //$NON-NLS-1$
        stModel.add("implementationFolder", fModel.getFolder(FolderType.IMPLEMENTATION_MIGRATION)); //$NON-NLS-1$
        stModel.add("otherFolder", fModel.getFolder(FolderType.OTHER)); //$NON-NLS-1$
        stModel.add("relationsFolder", fModel.getFolder(FolderType.RELATIONS)); //$NON-NLS-1$
        stModel.add("viewsFolder", fModel.getFolder(FolderType.DIAGRAMS)); //$NON-NLS-1$
        
        writer.write(stModel.render());
        writer.close();
        clean();
        return indexFile;
    }
    
    /**
     * Copy source HTML files to target folder
     * @throws IOException 
     */
    private void copyHTMLSkeleton(File targetFolder) throws IOException {    	
    	File srcDir = new File(ArchiReportsPlugin.INSTANCE.getTemplatesFolder(), "html"); //$NON-NLS-1$
    	FileUtils.copyFolder(srcDir, targetFolder);
    }
    
    /**
     * Copy hints files to target folder from the help plug-in
     * @throws IOException 
     */
    private void copyHintsFiles(File targetFolder) throws IOException {
        Bundle bundle = Platform.getBundle("com.archimatetool.help"); //$NON-NLS-1$
        URL url = FileLocator.resolve(bundle.getEntry("hints")); //$NON-NLS-1$
        FileUtils.copyFolder(new File(url.getPath()), new File(targetFolder, "hints")); //$NON-NLS-1$
    }
    
    private void copyConfidentialImage(File imagesFolder) throws IOException {
    	File confidential = new File(ArchiReportsPlugin.INSTANCE.getTemplatesFolder(), "confidencial.png");
    	FileUtils.copyFile(confidential, imagesFolder, false);
    	System.out.println("");
    }

    /**
     * Write all folders
     */
    private void writeFolders(File elementsFolder, ST stFrame, EList<IFolder> folders) throws IOException {
    	for(IFolder folder : folders) {
    		if( !this.notIncludeFolders.containsKey( folder.getId() ) ) {
    			writeFolder(elementsFolder, stFrame, folder);
    		}    		
    	}
    }
    
    /**
     * Write a single folder
     */
    private void writeFolder(File elementsFolder, ST stFrame, IFolder folder) throws IOException {
    	writeElements(elementsFolder, stFrame, folder.getElements());
    	writeFolders(elementsFolder, stFrame, folder.getFolders());
    }
    
    /**
     * Write all elements
     */
    private void writeElements(File elementsFolder, ST stFrame, List<EObject> list) throws IOException {
        if(!list.isEmpty()) {
            for(EObject object : list) {
                if(object instanceof IArchimateConcept) {
                	writeElement(new File(elementsFolder, ((IIdentifier) object).getId() + ".html"), stFrame, object); //$NON-NLS-1$
                }
            }
        }
    }
    
    /**
     * Write a single element
     */
    private void writeElement(File elementFile, ST stFrame, EObject component) throws IOException {
        OutputStreamWriter elementW = new OutputStreamWriter(new FileOutputStream(elementFile), "UTF8"); //$NON-NLS-1$
        stFrame.remove("element"); //$NON-NLS-1$
        //frame.remove("children");
        stFrame.add("element", component); //$NON-NLS-1$
        elementW.write(stFrame.render());
        elementW.close();
    }
    
    /**
     * Write diagrams
     */
    private void writeDiagrams(File imagesFolder, File viewsFolder, ST stFrame) throws IOException {
        if(fModel.getDiagramModels().isEmpty()) {
            return;
        }
        //copyConfidentialImage();
        Hashtable<IDiagramModel, Rectangle> offsetsTable = saveImages(imagesFolder);

        for(IDiagramModel dmOrig : fModel.getDiagramModels()) {
        	//if( !notIncludeViews.containsKey(dmOrig.getId()) ) {
        		// we need to add the necessary offsets in order to get correct absolute coordinates
                // for the elements in the generated image
                Rectangle offset = offsetsTable.get(dmOrig);
                // we create a copy of the Model: (children will not be copied!)
                IDiagramModel dmCopy = (IDiagramModel) dmOrig.getCopy();
                // FIX THE ID WHICH IS NOT COPIED
                dmCopy.setId(dmOrig.getId());
                // process the children
                for (IDiagramModelObject dmoOrig: dmOrig.getChildren() ) {
                    IDiagramModelObject dmoCopy = getOffsetCopy(dmoOrig, offset.x*-1, offset.y*-1);
                    // add copy of child to copy of model
                    dmCopy.getChildren().add(dmoCopy);
                }

                File viewF = new File(viewsFolder, dmCopy.getId() + ".html"); //$NON-NLS-1$
                OutputStreamWriter viewW = new OutputStreamWriter(new FileOutputStream(viewF), "UTF8"); //$NON-NLS-1$
                stFrame.remove("element"); //$NON-NLS-1$
                stFrame.add("element", dmCopy); //$NON-NLS-1$
                viewW.write(stFrame.render());
                viewW.close();
        	//}            
        }
    }
    
    /**
     * Save diagram images
     * return the offsets of the top-left element(s) in each image
     */
    private Hashtable<IDiagramModel, Rectangle> saveImages(File imagesFolder) {
        Hashtable<IDiagramModel, String> table = new Hashtable<IDiagramModel, String>();
        // we store the offsets of the top-left element(s) in each image
        Hashtable<IDiagramModel, Rectangle> offsetsTable = new Hashtable<>();
        int i = 1;
        
        for(IDiagramModel dm : fModel.getDiagramModels()) {
        	
        		ModelReferencedImage geoImage = DiagramUtils.createModelReferencedImage(dm, 1, 10);
                Image image = geoImage.getImage();
                String diagramName = dm.getId();
                if(StringUtils.isSet(diagramName)) {
                    // removed this because ids can have hyphens in them (when imported from TOG format)
                    // Let's hope that ids are filename friendly...
                    //diagramName = FileUtils.getValidFileName(diagramName);
                    
                    int j = 2;
                    String s = diagramName + ".png";  //$NON-NLS-1$
                    while(table.containsValue(s)) {
                        s = diagramName + "_" + j++ + ".png"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    diagramName = s;
                }
                else {
                    diagramName = Messages.HTMLReportExporter_1 + " " + i++ + ".png";  //$NON-NLS-1$//$NON-NLS-2$
                }

                table.put(dm, diagramName);

                // Get and store the offset of the top-left element in the figure
                offsetsTable.put(dm,  geoImage.getOffset());

                try {
                    ImageLoader loader = new ImageLoader();
                    loader.data = new ImageData[] { image.getImageDataAtCurrentZoom() };
                    File file = new File(imagesFolder, diagramName);
                    loader.save(file.getAbsolutePath(), SWT.IMAGE_PNG);
                    if( notIncludeViews.containsKey(dm.getId()) ) {
                    	try {
							this.copyConfidentialImage(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
                    }
                }
                finally {
                    image.dispose();
                }
        	
        }
        return offsetsTable;
    }    
    

    private IDiagramModelObject getOffsetCopy(IDiagramModelObject dmoOrig, int offsetX, int offsetY ) {
        IDiagramModelObject dmoCopy;
        // Prepare new bounds
        BoundsWithAbsolutePosition b = new BoundsWithAbsolutePosition(dmoOrig.getBounds());
        b.setOffset(offsetX, offsetY);
        // create and process copy
        if (dmoOrig instanceof IDiagramModelArchimateObject) {
            IDiagramModelArchimateObject dmaoOrig = (IDiagramModelArchimateObject) dmoOrig;
            IDiagramModelArchimateObject dmaoCopy = (IDiagramModelArchimateObject) dmaoOrig.getCopy();
            // NEED TO FIX ArchimateElement.ID WHICH IS NOT COPIED
            dmaoCopy.getArchimateElement().setId(dmaoOrig.getArchimateElement().getId());
            processChildren(dmaoOrig, dmaoCopy, b);
            dmoCopy = dmaoCopy;
        } else if (dmoOrig instanceof IDiagramModelGroup) {
            IDiagramModelGroup dmgOrig = (IDiagramModelGroup) dmoOrig;
            IDiagramModelGroup dmgCopy = (IDiagramModelGroup) dmgOrig.getCopy();
            processChildren(dmgOrig, dmgCopy, b);
            dmoCopy = dmgCopy;
        } else {
            // all other elements
            dmoCopy = (IDiagramModelObject) dmoOrig.getCopy();
        }
        // NEED TO FIX ID WHICH IS NOT COPIED!
        dmoCopy.setId(dmoOrig.getId());
        // Set the offset bounds.
        dmoCopy.setBounds(b);
        return dmoCopy;
    }

    private void processChildren(IDiagramModelContainer orig, IDiagramModelContainer copy, BoundsWithAbsolutePosition b) {
        // in case we have a ModelContainer, the contained coordinates are relative to the object container
        // -> recursively add the containers base coordinate as an offset to the children
        for (IDiagramModelObject childOrig: orig.getChildren() ) {
            IDiagramModelObject childCopy = getOffsetCopy(childOrig, b.getX1(), b.getY1());
            // add copy of child to copy of modelObject
            copy.getChildren().add(childCopy);
        }
    }
    
    public HashMap<String, String> getNotIncludeViews() {
		return notIncludeViews;
	}

	public void setNotIncludeViews(HashMap<String, String> notIncludeViews) {
		this.notIncludeViews = notIncludeViews;
	}

	public HashMap<String, String> getNotIncludeFolders() {
		return notIncludeFolders;
	}

	public void setNotIncludeFolders(HashMap<String, String> notIncludeFolders) {
		this.notIncludeFolders = notIncludeFolders;
	}

	public HTMLFolderReporter(IArchimateModel model) {
        fModel = model;
        notIncludeFolders = new HashMap<String, String>();
        notIncludeViews = new HashMap<String, String>();
    }
	
	public void clean() {
		notIncludeFolders = new HashMap<String, String>();
        notIncludeViews = new HashMap<String, String>();
	}
}
