package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;


/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomSchoolClassCourseProfilewType extends DomSchoolClassCourseAndProfile{
    private CourseType type;

    public DomSchoolClassCourseProfilewType() {
    }

    /**
     * @return the type
     */
    public CourseType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(CourseType type) {
        this.type = type;
    }

}
