/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomNewSchool {

    private DomSchoolFull domSchoolFull;
    private Map<RoleType, String> roleTypePasswords = new HashMap<RoleType, String>();
    {
      roleTypePasswords.put(RoleType.STUDENT,"student");
      roleTypePasswords.put(RoleType.TEACHER,"teacher");
      roleTypePasswords.put(RoleType.SCHOOLADMIN,"schooladmin");
    };

    /**
     * @return the domSchoolFull
     */
    public DomSchoolFull getDomSchoolFull() {
        return domSchoolFull;
    }

    /**
     * @param domSchoolFull the domSchoolFull to set
     */
    public void setDomSchoolFull(DomSchoolFull domSchoolFull) {
        this.domSchoolFull = domSchoolFull;
    }

    /**
     * @return the roleTypePasswords
     */
    public Map<RoleType, String> getRoleTypePasswords() {
        return roleTypePasswords;
    }

    /**
     * @param roleTypePasswords the roleTypePasswords to set
     */
    public void setRoleTypePasswords(Map<RoleType, String> roleTypePasswords) {
        this.roleTypePasswords = roleTypePasswords;
    }
}
