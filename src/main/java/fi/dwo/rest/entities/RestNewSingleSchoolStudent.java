/**
 * Copyrighted Feb 9, 2016
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;

/**
 * Create a single SchoolStudent and put han in a SchoolClass
 * 
 * @author G.A.J. van der Plas
 */
public class RestNewSingleSchoolStudent {

    private DomContext restContext;
    private DomNewSingleSchoolStudent domNewSingleSchoolStudent;    

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

    /**
     * @return the domNewSingleSchoolStudent
     */
    public DomNewSingleSchoolStudent getDomNewSingleSchoolStudent() {
        return domNewSingleSchoolStudent;
    }

    /**
     * @param domNewSingleSchoolStudent the domNewSingleSchoolStudent to set
     */
    public void setDomNewSingleSchoolStudent(DomNewSingleSchoolStudent domNewSingleSchoolStudent) {
        this.domNewSingleSchoolStudent = domNewSingleSchoolStudent;
    }
}
