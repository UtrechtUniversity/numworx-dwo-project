package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * A StudentModelContext node.
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelStructure {

    private DomStudentModelContextInfo info;
    private List<DomStudentModelCategory> categories;
    private String owner;
    private Long timestamp;
    private PersistenceId activeMethod;
    private List<String> methods;
    /**
     * @return the info
     */
    public DomStudentModelContextInfo getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(DomStudentModelContextInfo info) {
        this.info = info;
    }

    /**
     * @return the categories
     */
    public List<DomStudentModelCategory> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<DomStudentModelCategory> categories) {
        this.categories = categories;
    }

    /**
     * Generates an empty DomStudentModelStructureScore matching the structure.
     * 
     * @return 
     */
    @Transient
    public DomStudentModelStructureScore generateStudentModelStructureScore() {
        DomStudentModelStructureScore result = new DomStudentModelStructureScore();
        for (DomStudentModelCategory cat : categories) {
            result.getCategories().add(cat.buildDomStudentModelCategoryScore());
        }
        if (info != null) result.setId(info.getId());
        return result;
    }

	@Override
	public int hashCode() {
		return Objects.hash(categories, info, owner, timestamp, methods, activeMethod);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DomStudentModelStructure)) {
			return false;
		}
		DomStudentModelStructure other = (DomStudentModelStructure) obj;
		return Objects.equals(owner, other.owner) 
				&& Objects.equals(timestamp, other.timestamp)
				&& Objects.equals(methods, other.methods)
				&& Objects.equals(activeMethod, other.activeMethod)
				&& Objects.equals(categories, other.categories) 
				&& Objects.equals(info, other.info);
	}

	public boolean same(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DomStudentModelStructure)) {
			return false;
		}
		DomStudentModelStructure other = (DomStudentModelStructure) obj;
		return Objects.equals(categories, other.categories) 
				&& Objects.equals(info, other.info) 
				&& Objects.equals(methods, other.methods)
				&& Objects.equals(activeMethod, other.activeMethod);
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the activeMethod
	 */
	public PersistenceId getActiveMethod() {
		return activeMethod;
	}

	/**
	 * @param activeMethod the activeMethod to set
	 */
	public void setActiveMethod(PersistenceId activeMethod) {
		this.activeMethod = activeMethod;
	}

	/**
	 * @return the methods
	 */
	public List<String> getMethods() {
		return methods;
	}

	/**
	 * @param methods the methods to set
	 */
	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

}
