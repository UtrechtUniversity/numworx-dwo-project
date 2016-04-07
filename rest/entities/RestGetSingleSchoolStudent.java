/**
 * Copyrighted Feb 9, 2016
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;

/**
 * Create a single SchoolStudent and put han in a SchoolClass
 * 
 * @author G.A.J. van der Plas
 */
public class RestGetSingleSchoolStudent {

    private DomContext restContext;
    private DomGetSingleSchoolStudent domGetSingleSchoolStudent;    

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
    public DomGetSingleSchoolStudent getDomGetSingleSchoolStudent() {
        return domGetSingleSchoolStudent;
    }

    /**
     * @param domNewSingleSchoolStudent the domNewSingleSchoolStudent to set
     */
    public void setDomGetSingleSchoolStudent(DomGetSingleSchoolStudent aDomGetSingleSchoolStudent) {
        domGetSingleSchoolStudent = aDomGetSingleSchoolStudent;
    }
}
