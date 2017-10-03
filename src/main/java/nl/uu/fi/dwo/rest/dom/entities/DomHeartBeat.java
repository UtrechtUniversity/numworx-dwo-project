package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A simple heartbeat entity class. Containing current server time and version. 
 * An extended version may yield the latest versions for our client suite allowing
 * a user to select when to update to a newer version between work. If possible 
 * a relogin should be avoided. Server time stamp may be signed in the future.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomHeartBeat {
     private Long serverTimeStamp;
     private String serverVersion;
     private String clientVersion;

    /**
     * @return the serverTimeStamp
     */
    public Long getServerTimeStamp() {
        return serverTimeStamp;
    }

    /**
     * @param serverTimeStamp the serverTimeStamp to set
     */
    public void setServerTimeStamp(Long serverTimeStamp) {
        this.serverTimeStamp = serverTimeStamp;
    }

    /**
     * @return the serverVersion
     */
    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * @param serverVersion the serverVersion to set
     */
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    /**
     * @return the clientVersion
     */
    public String getClientVersion() {
        return clientVersion;
    }

    /**
     * @param clientVersion the clientVersion to set
     */
    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
}
