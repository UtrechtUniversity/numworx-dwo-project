package fi.dwo.commons.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transports a user exception to the server for debugging.
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestDwoExceptionMessage {
    private RestContext restContext;

    /**
     * @return the restContext
     */
    public RestContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(RestContext restContext) {
        this.restContext = restContext;
    }
    
}
