/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.util.SchoolAttrType;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomSchoolFull extends DomSchool{
    private String schoolLogin;
    private Boolean export;
    private String schoolRights;
    private String image;
    private List<DomMapEntry<RoleType,String>> passwords;
    private List<DomMapEntry<SchoolAttrType,String>> attributes;

    /**
     * @return the schoolLogin
     */
    public String getSchoolLogin() {
        return schoolLogin;
    }

    /**
     * @param schoolLogin the schoolLogin to set
     */
    public void setSchoolLogin(String schoolLogin) {
        this.schoolLogin = schoolLogin;
    }

    /**
     * @return the export
     */
    public Boolean getExport() {
        return export;
    }

    /**
     * @param export the export to set
     */
    public void setExport(Boolean export) {
        this.export = export;
    }

    /**
     * @return the schoolRights
     */
    public String getSchoolRights() {
        return schoolRights;
    }

    /**
     * @param schoolRights the schoolRights to set
     */
    public void setSchoolRights(String schoolRights) {
        this.schoolRights = schoolRights;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    public List<DomMapEntry<RoleType,String>> getPasswords() {
      return passwords;
    }

    public void setPasswords(List<DomMapEntry<RoleType,String>> passwords) {
      this.passwords = passwords;
    }

	public List<DomMapEntry<SchoolAttrType,String>> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<DomMapEntry<SchoolAttrType,String>> attributes) {
		this.attributes = attributes;
	}

}
