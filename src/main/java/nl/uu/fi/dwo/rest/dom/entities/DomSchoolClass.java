/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolClass extends DomSchoolClassId implements Cloneable {
    private String schoolClassName;
    private Boolean hasRegKey;
    private Boolean iconizer = Boolean.FALSE;

    public DomSchoolClass() {

    }

    public DomSchoolClass(DomSchoolClass sc) {
        DomSchoolClass clone=null;
            clone = sc.duplicate();
        this.setId(clone.getId());
        this.hasRegKey = clone.getHasRegKey();
        this.schoolClassName = clone.getSchoolClassName();
        this.iconizer = clone.getIconizer();
    }

    public DomSchoolClass duplicate() {
        DomSchoolClass sc = new DomSchoolClass();
        sc.setId((PersistenceId) this.getId().duplicate());
        sc.schoolClassName = this.getSchoolClassName(); //strings are final
        sc.hasRegKey = this.getHasRegKey().equals(true); //ensuring cloned stuff
        sc.iconizer = this.getIconizer();
        return sc;
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
        setId(null);
        schoolClassName = "";
        hasRegKey = new Boolean(false);
    }

}
