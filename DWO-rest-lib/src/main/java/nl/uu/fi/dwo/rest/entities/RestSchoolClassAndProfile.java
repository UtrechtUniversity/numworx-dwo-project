/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClassAndProfile {

    private DomContext restContext;
//    private DomSchoolClass domSchoolClass;
//    private DomDwoProfile domDwoProfile;
    private DomSchoolClassAndProfile domSchoolClassAndProfile;

    @Deprecated
    public DomDwoProfile getDomDwoProfile() {
        if (domSchoolClassAndProfile == null) {
            return null;
        } else {
            return domSchoolClassAndProfile.getDomDwoProfile();
        }
    }    

    @Deprecated
    public void setDomDwoProfile(DomDwoProfile domDwoProfile) {
        if (domSchoolClassAndProfile == null) {
            domSchoolClassAndProfile = new DomSchoolClassAndProfile();
        }
        domSchoolClassAndProfile.setDomDwoProfile(domDwoProfile);
    }

    public RestSchoolClassAndProfile() {

    }

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
     * @return the domSchoolClass
     */
    @Deprecated
    public DomSchoolClass getDomSchoolClass() {
        if (domSchoolClassAndProfile == null) {
            return null;
        } else {
            return domSchoolClassAndProfile.getDomSchoolClass();
        }
    }

    /**
     * @param domSchoolClass the domSchoolClass to set
     */
    @Deprecated
    public void setDomSchoolClass(DomSchoolClass domSchoolClass) {
        if (domSchoolClassAndProfile == null) {
            domSchoolClassAndProfile = new DomSchoolClassAndProfile();
        }
        domSchoolClassAndProfile.setDomSchoolClass(domSchoolClass);
    }

    /**
     * @return the domSchoolClassAndProfile
     */
    public DomSchoolClassAndProfile getDomSchoolClassAndProfile() {
        return domSchoolClassAndProfile;
    }

    /**
     * @param domSchoolClassAndProfile the domSchoolClassAndProfile to set
     */
    public void setDomSchoolClassAndProfile(DomSchoolClassAndProfile domSchoolClassAndProfile) {
        this.domSchoolClassAndProfile = domSchoolClassAndProfile;
    }

}
