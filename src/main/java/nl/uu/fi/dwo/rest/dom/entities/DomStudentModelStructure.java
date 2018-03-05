package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A StudentModelContext node.
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelStructure {

    private static final Logger LOG = Logger.getLogger(DomStudentModelStructure.class.getName());
    private DomStudentModelContextInfo info;
    private List<DomStudentModelCategory> categories;

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
     * Generates a DomStudentModelStructureScore matching the structure.
     * 
     * @return 
     */
    @Transient
    public DomStudentModelStructureScore generateStudentModelStructureScore() {
        DomStudentModelStructureScore result = new DomStudentModelStructureScore();
        for (DomStudentModelCategory cat : categories) {
            result.getCategories().add(cat.buildDomStudentModelCategoryScore());
        }
        return result;
    }
}
