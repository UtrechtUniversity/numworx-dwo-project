package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.Date;
import java.util.List;

/**
 * ClassCourseItem expresses the join of a ClassCourse and Course object.
 * 
 * @author G.A.J. van der Plas
 */
public class ClassCourseItem extends CourseItem {
        private Boolean hasStudentData;
        private String type;
        private Date from;
        private Date to;

   public ClassCourseItem (){
        
    }
    
    public ClassCourseItem (String aKey, CourseItem aParent, List<CourseItem> myChildren, String aName, Boolean hasData, String aType, Date aFrom, Date aTo){
        hasStudentData = hasData;
        type = aType;
        from = aFrom;
        to = aTo;
    }        
        
    /**
     * @return the hasStudentData
     */
    public Boolean getHasStudentData() {
        return hasStudentData;
    }

    /**
     * @param hasStudentData the hasStudentData to set
     */
    public void setHasStudentData(Boolean hasStudentData) {
        this.hasStudentData = hasStudentData;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
