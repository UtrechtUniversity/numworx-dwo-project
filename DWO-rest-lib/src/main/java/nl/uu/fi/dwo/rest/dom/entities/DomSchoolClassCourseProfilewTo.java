package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSchoolClassCourseProfilewTo extends DomSchoolClassCourseAndProfile{
    private Date to;

    public DomSchoolClassCourseProfilewTo() {
    }

    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }


}
