package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
*
* @author Wim van Velthoven
*/
@XmlRootElement
public class DomSchoolAdminAndHasRole {
    private DomSchoolAdmin schoolAdmin;
    private DomHasRole hasRole;

    /**
     * @return the schoolAdmin
     */
    public DomSchoolAdmin getSchoolAdmin() {
        return schoolAdmin;
    }

    /**
     * @param schoolAdmin the schoolAdmin to set
     */
    public void setSchoolAdmin(DomSchoolAdmin schoolAdmin) {
        this.schoolAdmin = schoolAdmin;
    }

    /**
     * @return the hasRole
     */
    public DomHasRole getHasRole() {
        return hasRole;
    }

    /**
     * @param hasRole the hasRole to set
     */
    public void setHasRole(DomHasRole hasRole) {
        this.hasRole = hasRole;
    }

}
