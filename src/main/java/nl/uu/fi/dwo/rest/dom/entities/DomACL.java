package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomACL extends DomACLId {

  private PersistenceId entity;
  private ACL access;

  public PersistenceId getEntity() {
    return entity;
  }
  public void setEntity(PersistenceId entity) {
    this.entity = entity;
  }
  public ACL getAccess() {
    return access;
  }
  public void setAccess(ACL access) {
    this.access = access;
  }
}
