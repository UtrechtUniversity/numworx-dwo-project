package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Localized title and description of a StudentModel structure. the pay load
 * of a StudentModelContext node.
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelContextInfo {
    private Map<String,String> title;
    private Map<String,String> description;
    private String id;

    public DomStudentModelContextInfo(){        
       
    }
    
    public DomStudentModelContextInfo(Map<String,String> aTitle, Map<String,String> aDescription){
        title = aTitle;
        description= aDescription;
    }
    /**
     * @return the title
     */
    public Map<String,String> getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(Map<String,String> title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public Map<String,String> getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(Map<String,String> description) {
        this.description = description;
    }

    /**
     * @return the id
     */
    public String getId() {
      return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
      this.id = id;
    }
    
}
