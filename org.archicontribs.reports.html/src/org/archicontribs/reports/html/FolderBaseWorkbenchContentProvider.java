package org.archicontribs.reports.html;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class FolderBaseWorkbenchContentProvider implements ITreeContentProvider {

    /**
     * Creates a new workbench content provider.
     *
     */
    public FolderBaseWorkbenchContentProvider() {
        super();
    }

    @Override
	public void dispose() {
        // do nothing
    }

    /**
     * Returns the implementation of IWorkbenchAdapter for the given
     * object.  Returns null if the adapter is not defined or the
     * object is not adaptable.
     * <p>
     * </p>
     *
     * @param element the element
     * @return the corresponding workbench adapter object
     */
    protected IWorkbenchAdapter getAdapter(Object element) {
        return Adapters.adapt(element, Folder.class);
    }

    @Override
	public Object[] getChildren(Object element) {
    	if(element instanceof Folder) {
    		if(((Folder) element).getFolders() != null && ((Folder) element).getFolders().size() > 0) {
    			return ((Folder) element).getFolders().toArray();
    		}
    		return new Object[0];
    	} else {
    		IWorkbenchAdapter adapter = getAdapter(element);
            if (adapter != null) {
                return adapter.getChildren(element);
            }
            return new Object[0];
    	}
    }

    @Override
	public Object[] getElements(Object element) {
        return getChildren(element);
    }

    @Override
	public Object getParent(Object element) {
    	if(element instanceof Folder) {
    		return element;
    	} else {
    		IWorkbenchAdapter adapter = getAdapter(element);
            if (adapter != null) {
                return adapter.getParent(element);
            }
            return null;
    	}        
    }

    @Override
	public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }

}