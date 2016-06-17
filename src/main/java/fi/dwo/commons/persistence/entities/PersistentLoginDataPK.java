/**
 * Copyrighted May 26, 2016
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CompoundKey for Persistence. Note that order is important in NoSQL databases
 * for compound keys. The utcTimeStamp should be first in the index sort order
 * on those databases.
 * 
 * @author Gert van der Plas
 */
@Embeddable
public class PersistentLoginDataPK implements Serializable {
        @Id
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 128)
        @Column(name = "utctimestamp", nullable = false) 
        private long utcTimeStamp;
        @Id
        @Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 128)
        @Column(name = "username", nullable = false, length = 128)
        private String username;

    /**
     * @return the utcTimeStamp
     */
    public long getUtcTimeStamp() {
        return utcTimeStamp;
    }

    /**
     * @param utcTimeStamp the utcTimeStamp to set
     */
    public void setUtcTimeStamp(long utcTimeStamp) {
        this.utcTimeStamp = utcTimeStamp;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    }
