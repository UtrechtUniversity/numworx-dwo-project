package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomStudentMapEntry  {

    private PersistenceId key;
    private DomStudent value;

    public DomStudentMapEntry() {
    }
    
    public DomStudentMapEntry(Map.Entry<PersistenceId, DomStudent> entry) {
        key = entry.getKey();
        value = entry.getValue();
    }

    public PersistenceId getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(PersistenceId key) {
        this.key = key;
    }

    public DomStudent getValue() {
        return value;
    }

    public DomStudent setValue(DomStudent aValue) {
        DomStudent oldValue;
        oldValue = value;
        value = aValue;
        return oldValue;
    }

}
