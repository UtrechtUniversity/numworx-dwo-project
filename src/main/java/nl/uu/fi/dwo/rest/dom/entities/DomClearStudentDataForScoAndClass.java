package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transmits ScoContext and SchoolClass information needed to clear the data for 
 * the given students. This object is future proof in case StudentSco data is
 * also attached to school classes and if work of individual students need to cleared
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomClearStudentDataForScoAndClass {
    private DomScoContext domScoContext;
    private DomSchoolClass domSchoolClass;
    private DomDwoProfile domProfile;
    private List<DomStudent> domStudentList;

    /**
     * @return the domScoContext
     */
    public DomScoContext getDomScoContext() {
        return domScoContext;
    }

    /**
     * @param domScoContext the domScoContext to set
     */
    public void setDomScoContext(DomScoContext domScoContext) {
        this.domScoContext = domScoContext;
    }

    /**
     * @return the domSchoolClass
     */
    public DomSchoolClass getDomSchoolClass() {
        return domSchoolClass;
    }

    /**
     * @param domSchoolClass the domSchoolClass to set
     */
    public void setDomSchoolClass(DomSchoolClass domSchoolClass) {
        this.domSchoolClass = domSchoolClass;
    }

    /**
     * @return the domStudentList
     */
    public List<DomStudent> getDomStudentList() {
        return domStudentList;
    }

    /**
     * @param domStudentList the domStudentList to set
     */
    public void setDomStudentList(List<DomStudent> domStudentList) {
        this.domStudentList = domStudentList;
    }

    /**
     * @return the domProfile
     */
    public DomDwoProfile getDomProfile() {
        return domProfile;
    }

    /**
     * @param domProfile the domProfile to set
     */
    public void setDomProfile(DomDwoProfile domProfile) {
        this.domProfile = domProfile;
    }
    
}
