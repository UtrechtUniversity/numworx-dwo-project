package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolGroup extends DomId {

  private String password;
  private RoleType role;

  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public RoleType getRole() {
    return role;
  }
  public void setRole(RoleType role) {
    this.role = role;
  }

}
