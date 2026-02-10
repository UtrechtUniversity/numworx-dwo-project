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
public class PersistentLogDataPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "timeStamp", nullable = false)
    private long utcTimeStamp;
    @Basic(optional = false)
    @NotNull
    @Column(name = "userID", nullable = false)
    private Long userId;

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
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
