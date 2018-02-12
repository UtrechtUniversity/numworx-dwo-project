package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Localized title and description of a StudentModel structure. the pay load
 * of a StudentModelContext node.
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelContextInfo {
    private String title = "";
    private String description = "";

    public DomStudentModelContextInfo(){        
       
    }
    
    public DomStudentModelContextInfo(String aTitle, String aDescription){
        title = aTitle;
        description= aDescription;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
}
