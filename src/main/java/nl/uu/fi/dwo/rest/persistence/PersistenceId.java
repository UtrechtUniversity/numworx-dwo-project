/*Copyrighted 2015. */
package nl.uu.fi.dwo.rest.persistence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A persistent id based on object information.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class PersistenceId implements Comparable<PersistenceId>, Cloneable {

    private String stringId;
    private PersistenceClassType type = PersistenceClassType.none;

    /**
     * This returns a unique string id. The implementing class of the
     * {@Link PersistenceId} interface should ensure that each id is an unique
     * reference to any persistent entity it maps for. No assumptions are made
     * on the order of the Id. However it is recommended that, if possible,
     * lexicographical sorting sorts objects in incremental order of class type,
     * object index.
     *
     * @return A unique string id.
     */
    public String getIdString() {
        return stringId;
    }

    /**
     * This decomposes a unique string id and sets a persistence Id. The
     * implementing class of the {@Link PersistenceId} interface should ensure
     * that each id is an unique reference to any persistent entity it maps for.
     * No assumptions are made on the order of the Id. However it is recommended
     * that, if possible, lexicographical sorting sorts objects in incremental
     * order of class type, object index.
     *
     * @param id set a PersistenceId from a String id.
     */
    public void setIdString(String id) {
        stringId = id;
    }

    ;
    
    /**
     * Returns the persistence class type.
     * 
     * @return returns the PersistenceId type.
     */
    public PersistenceClassType getType() {
        return type;
    }

    ;

    /**
     * Sets the persistence class type. Only meant for serialization.
     * 
     * @param aType 
     */
    public void setType(PersistenceClassType aType) {
        type = aType;
    }

    ;
    
     /**
     * Compare ordered state.
     *
     * @param aId
     * @return
     */
    @Override
    public int compareTo(PersistenceId aId) {
        return stringId.compareTo(aId.getIdString());
    }

    /**
     * Define equality of state.
     *
     * @param aId
     * @return
     */
    public boolean equals(PersistenceId aId) {
        return stringId.equals(aId.getIdString());
    }

    /**
     * A class that overrides equals must override hashCode for HasMaps and such
     * to work properly.
     */
    @Override
    public int hashCode() {
        return stringId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersistenceId other = (PersistenceId) obj;
        return !((this.stringId == null) ? (other.stringId != null) : !this.stringId.equals(other.stringId));
    }

    /** Clones a PersistenceId 
     * 
     * @return 
     */
    public PersistenceId duplicate() {
        PersistenceId id = new PersistenceId();
        id.setIdString(this.stringId);
        id.setType(this.type);
        return id;
    }

    @Override
    public String toString() {
        return stringId;
    }
}
