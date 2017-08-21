package nl.uu.fi.dwo.rest.dom.entities;

/**
 * RestContext contains context information for a rest call. Essentially it is the 
 * part of a RestCall that contains any required generic state information for a rest-call. 
 * For example the hasRole in which the RestCall was executed. 
 * 
 * 
 * @author G.A.J. van der Plas
 */
public class RestContext {
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
