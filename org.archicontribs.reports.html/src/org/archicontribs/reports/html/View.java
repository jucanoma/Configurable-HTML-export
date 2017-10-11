package org.archicontribs.reports.html;

/**
 * Class who represents information of views who are not included in report
 * @author Juan Carlos Nova
 *
 */
public class View {
	
	private String name;
    private String id;
    
    public View(String name, String id) {
		super();
		this.name = name;
		this.id = id;
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
    
    

}
