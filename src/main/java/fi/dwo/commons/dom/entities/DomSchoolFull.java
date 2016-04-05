/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

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
    private Date expire;

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
    
}
