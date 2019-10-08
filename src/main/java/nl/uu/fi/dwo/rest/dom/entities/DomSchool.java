/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;
import java.util.Date;

import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchool extends DomSchoolId {
    private String schoolName;
    private String schoolRights;
    private Date expire;
    private AboType aboType;
    
    private static String defaultRights = "cCm"; // See dwojclient School

    @XmlTransient
    public static String defaultRights() {
		return defaultRights;
	}

    @XmlTransient
    public static void defaultRights(String defaultRights) {
		DomSchool.defaultRights = defaultRights;
	}

	public DomSchool(){
        
    }
    
    /**
     * @return the schoolName
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * @param schoolName the schoolName to set
     */
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /**
     * @return the schoolRights
     */
    public String getSchoolRights() {
        return schoolRights;
    }

    /**
     * @param schoolRights the schoolRights to set. 
     */
    public void setSchoolRights(String schoolRights) {
        this.schoolRights = schoolRights;
    }
    
    public boolean studentsCanRegisterForSchoolClasses(){
        String r = schoolRights;
        if (r == null || "_".equals(r))
        	r = defaultRights;
		return r.contains("c");
    }


    /**
     * @return the expire
     */
    public Date getExpire() {
        return expire;
    }

    /**
     * @param expire the expire to set
     */
    public void setExpire(Date expire) {
        this.expire = expire;
    }
    
    @Transient
    public  boolean  licenseIsValid() {

        if (this.getExpire() == null) {
            return true;
        } else {
            Date now = new Date();
            return now.before(this.getExpire());
        }
    }

    public AboType getAboType() {
      return aboType;
    }

    public void setAboType(AboType aboType) {
      this.aboType = aboType;
    }

    public boolean teachersCanWrite() {
      String r = schoolRights;
      if (r == null || "_".equals(r))
          r = defaultRights;
      return r.contains("m");
    }  

    public boolean accessControl() {
      String r = schoolRights;
      if (r == null) return false;
      return r.contains("X");
    }
}
