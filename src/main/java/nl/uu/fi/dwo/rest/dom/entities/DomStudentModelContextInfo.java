package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
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
    private Double slip, learn, init;
    private List<String> voorkennis;

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

	/**
	 * @return the slip
	 */
	public Double getSlip() {
		return slip;
	}

	/**
	 * @param slip the slip to set
	 */
	public void setSlip(Double slip) {
		this.slip = slip;
	}

	/**
	 * @return the learn
	 */
	public Double getLearn() {
		return learn;
	}

	/**
	 * @param learn the learn to set
	 */
	public void setLearn(Double learn) {
		this.learn = learn;
	}

	/**
	 * @return the init
	 */
	public Double getInit() {
		return init;
	}

	/**
	 * @param init the init to set
	 */
	public void setInit(Double init) {
		this.init = init;
	}

  /**
   * @return the voorkennis
   */
  public List<String> getVoorkennis() {
    return voorkennis;
  }

  /**
   * @param voorkennis the voorkennis to set
   */
  public void setVoorkennis(List<String> voorkennis) {
    this.voorkennis = voorkennis;
  }
    
}
