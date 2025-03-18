package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

@Entity
@Table(name = "tblmfa")
public class PersistentMFA implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @Column(name = "userID", nullable = false)
  private Long userID;
  
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "secret", nullable = false, length = 50)
  private String secret;
  
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "recovery", nullable = false)
  private ArrayList<String> lines = new ArrayList<>();
 
  @Basic(optional = false)
  @Column(name = "lastChangeTimeStamp", nullable = true)
  private Long lastChangeTimeStamp;

  @Column(name = "optlock")
  @Version 
  private Long optlock;

  @NotNull
  @Column(name= "del")
  private DelState delState = DelState.not;
  
  @PrePersist
  @PreUpdate
  private void now() {
    lastChangeTimeStamp = System.currentTimeMillis();
  }
    
  public Long getLastChangeTimeStamp() {
    return lastChangeTimeStamp;
  }
  public void setLastChangeTimeStamp(Long lastChangeTimeStamp) {
    this.lastChangeTimeStamp = lastChangeTimeStamp;
  }
  public Long getOptlock() {
    return optlock;
  }
  public void setOptlock(Long optlock) {
    this.optlock = optlock;
  }
  
  public List<String> getRecovery() {
	  return lines;
  }
  
  public void setRecovery(List<String> recovery) {
	  lines = new ArrayList<>(recovery);
  }

/**
 * @return the userID
 */
public Long getUserID() {
	return userID;
}

/**
 * @param userID the userID to set
 */
public void setUserID(Long userID) {
	this.userID = userID;
}

/**
 * @return the secret
 */
public String getSecret() {
	return secret;
}

/**
 * @param secret the secret to set
 */
public void setSecret(String secret) {
	this.secret = secret;
}
  
  
}
