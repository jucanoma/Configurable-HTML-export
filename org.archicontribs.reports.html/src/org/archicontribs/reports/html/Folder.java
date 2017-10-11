package org.archicontribs.reports.html;

import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;


/**
 * Class who have the information of the folders to exclude
 * @author juan
 *
 */
public class Folder implements org.eclipse.ui.model.IWorkbenchAdapter{

        private String name;
        private String id;
        private ArrayList<Folder> folders;
        private ArrayList<View> views;
        
        public Folder() {
        	folders = new ArrayList<Folder>(); 
        }
        
        public Folder(String name, String id) {
            this.name = name;
            this.id = id;
            folders=new ArrayList<Folder>();
            views=new ArrayList<View>();
        }
 
        public ArrayList<View> getViews() {
			return views;
		}

		public void setViews(ArrayList<View> views) {
			this.views = views;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public ArrayList<Folder> getFolders() {
			return folders;
		}

		public void setFolders(ArrayList<Folder> elementos) {
			this.folders = elementos;
		}		

		@Override
		public String toString() {
			return "Estructura [name=" + name + "]";
		}

		@Override
		public Object[] getChildren(Object o) {
			return this.getFolders().toArray();
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		@Override
		public String getLabel(Object o) {
			return this.getName();
		}

		@Override
		public Object getParent(Object o) {
			return this;
		}
        
        
    

}
