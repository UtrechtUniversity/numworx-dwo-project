package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSchoolClassCourseProfilewFrom extends DomSchoolClassCourseAndProfile{
    private Date from;

    public DomSchoolClassCourseProfilewFrom() {
    }

    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }


}
