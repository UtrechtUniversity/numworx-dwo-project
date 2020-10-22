package nl.uu.fi.dwo.rest.dom.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
    private Map<String, Map<String, Set<Integer>>> methods;
    private Integer x,y;

    public DomStudentModelContextInfo(DomStudentModelContextInfo info) {
    	title = info.getTitle(); if(title != null) title = new TreeMap<>(title);
    	description = info.getDescription(); if (description != null) description = new TreeMap<>(description);
    	id = info.getId();
    	slip = info.getSlip();
    	learn = info.getLearn();
    	init = info.getInit();
    	x = info.getX();
    	y = info.getY();
    	
    	voorkennis = info.getVoorkennis(); if (voorkennis != null) voorkennis = new ArrayList<>(voorkennis);
    	methods = info.getMethods();
    	if (methods != null) {
    		methods = new TreeMap<>(methods);
    		methods.entrySet().forEach((e) -> e.setValue(copyOf(e.getValue())));
    	}
    	
    }
    
    private static Map<String, Set<Integer>> copyOf(Map<String, Set<Integer>> map) {
    	if (map != null) {
    		map  = new TreeMap<>(map);
    		map.entrySet().forEach(e -> e.setValue(copyOf(e.getValue())));
    	}
		return map;
	}

	private static Set<Integer> copyOf(Set<Integer> value) {
		if (value == null)
			return null;
		return new TreeSet<>(value);
	}


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

	public Map<String, Map<String, Set<Integer>>> getMethods() {
		return methods;
	}
	
	public void setMethods(Map<String, Map<String, Set<Integer>>> methods) {
		this.methods = methods;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, init, learn, methods, slip, title, voorkennis);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DomStudentModelContextInfo)) {
			return false;
		}
		DomStudentModelContextInfo other = (DomStudentModelContextInfo) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(init, other.init) && Objects.equals(learn, other.learn)
				&& Objects.equals(methods, other.methods) && Objects.equals(slip, other.slip)
				&& Objects.equals(title, other.title) && Objects.equals(voorkennis, other.voorkennis);
	}

	/**
	 * @return the x
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Integer x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Integer getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Integer y) {
		this.y = y;
	}
    
}
