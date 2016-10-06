/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolClass implements Cloneable {
    private static final Logger LOG = Logger.getLogger(DomSchoolClass.class.getName());

    private PersistenceId id;
    private String schoolClassName;
    private Boolean hasRegKey;
    private Boolean iconizer = Boolean.FALSE;

    public DomSchoolClass() {

    }

    public DomSchoolClass(DomSchoolClass sc) {
        DomSchoolClass clone=null;
            clone = sc.duplicate();
        this.id = clone.id;
        this.hasRegKey = clone.getHasRegKey();
        this.schoolClassName = clone.getSchoolClassName();
    }

    public DomSchoolClass duplicate() {
        DomSchoolClass sc = new DomSchoolClass();
        sc.id = (PersistenceId) this.id.duplicate();
        sc.schoolClassName = this.getSchoolClassName(); //strings are final
        sc.hasRegKey = this.getHasRegKey().equals(true); //ensuring cloned stuff
        return sc;
    }

    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param classId the id to set
     */
    public void setId(PersistenceId classId) {
        this.id = classId;
    }

    /**
     * @return the schoolClassName
     */
    public String getSchoolClassName() {
        return schoolClassName;
    }

    /**
     * @param schoolClassName the schoolClassName to set
     */
    public void setSchoolClassName(String schoolClassName) {
        this.schoolClassName = schoolClassName;
    }

    /**
     * @return the hasRegKey
     */
    public Boolean getHasRegKey() {
        return hasRegKey;
    }

    /**
     * @param hasRegKey the hasRegKey to set
     */
    public void setHasRegKey(Boolean hasRegKey) {
        this.hasRegKey = hasRegKey;
    }

    /**
     * @return the iconizer
     */
    public Boolean getIconizer() {
        return iconizer;
    }

    /**
     * @param iconizer the iconizer to set
     */
    public void setIconizer(Boolean iconizer) {
        this.iconizer = iconizer;
    }
    
    void clearSettings() {
        id = null;
        schoolClassName = "";
        hasRegKey = new Boolean(false);
    }

}
