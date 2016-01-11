package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transports a user exception to the server for debugging.
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestDwoExceptionMessage {
    private DomContext restContext;

    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }
    
}
