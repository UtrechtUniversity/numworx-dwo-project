/**
 * Copyrighted Oct 5, 2017
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.DomCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * JPA/EclipseLink entity for the ClassCourses.
 * <p>
 * ClassCourses. Each classCourse links a class to a tree or sub tree in the
 * Courses data set.
 * <p>
 * &lt; courseId, classId, type, notBefore, notAfter &gt;
 * <p>
 * <b>classCourseId</b> : Unique, non-negative and not null<br>
 * <b>courseId</b> : Unique, non-negative and not null.<br>
 * <b>classId</b> : Unique, non-negative and not null.<br>
 * <b>type</b> : Declares under which conditions a school may access it.<br>
 * <b>notBefore</b> : The referenced course module tree is not visible before
 * notBefore. <br>
 * <b>notAfter</b> : The referenced course module tree is not visible after
 * notAfter.<br>
 * <p>
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblclasscourse", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ClassID", "CourseID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentCourseInClass.findAll", query = "SELECT p FROM PersistentCourseInClass p"),
    @NamedQuery(name = "PersistentCourseInClass.findByClassCourseID", query = "SELECT p FROM PersistentCourseInClass p WHERE p.classCourseID = :classCourseID"),
    @NamedQuery(name = "PersistentCourseInClass.findByClassID", query = "SELECT p FROM PersistentCourseInClass p WHERE p.classID = :classID"),
    @NamedQuery(name = "PersistentCourseInClass.findVisibleByClassID", query = "SELECT p FROM PersistentCourseInClass p WHERE p.classID = :classID and p.viewState = :viewState"),
    @NamedQuery(name = "PersistentCourseInClass.findByClassIDAndCourseID", query = "SELECT p FROM PersistentCourseInClass p WHERE p.classID = :classID and p.course = :course"),
    @NamedQuery(name = "PersistentCourseInClass.findByType", query = "SELECT p FROM PersistentCourseInClass p WHERE p.type = :type"),
    @NamedQuery(name = "PersistentCourseInClass.findByNotBefore", query = "SELECT p FROM PersistentCourseInClass p WHERE p.notBefore = :notBefore"),
    @NamedQuery(name = "PersistentCourseInClass.findByNotAfter", query = "SELECT p FROM PersistentCourseInClass p WHERE p.notAfter = :notAfter"),
    @NamedQuery(name = "PersistentCourseInClass.findByCourseID", query = "SELECT p FROM PersistentCourseInClass p WHERE p.course = :course")})
public class PersistentCourseInClass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ClassCourseID", nullable = false)
    private Long classCourseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ClassID", nullable = false)
    private long classID;
    @Column(name = "type") //enum afschermd, normaal, todo beveiligd (safeexamebrowser), chrome
    private Integer type;
    @Column(name = "viewState") //enum afschermd, normaal, todo beveiligd (safeexamebrowser), chrome
    private ViewState viewState;
    @Column(name = "notBefore")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notBefore;
    @Column(name = "notAfter")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notAfter;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CourseID", nullable = false)
//    private long courseID;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "tblcourse", joinColumns=@JoinColumn(name="courseID"),
    inverseJoinColumns=@JoinColumn(name="courseID"))
    PersistentCourse course;

    public PersistentCourse getCourse() {
        return course;
    }
    
    public void setCourse(PersistentCourse aCourse) {
        course = aCourse;
    }

    public PersistentCourseInClass() {
    }

    public PersistentCourseInClass(long classCourseID) {
        this.classCourseID = classCourseID;
    }

    public PersistentCourseInClass(long classCourseID, long classID, PersistentCourse course) {
        this.classCourseID = classCourseID;
        this.classID = classID;
        //this.courseID = courseID;
        this.course = course;
    }

    public long getClassCourseID() {
        return classCourseID;
    }

    public void setClassCourseID(long classCourseID) {
        this.classCourseID = classCourseID;
    }

    public long getClassID() {
        return classID;
    }

    public void setClassID(long classID) {
        this.classID = classID;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public Long getCourseID() {
        return course.getCourseID();
    }

 //   public void setCourseID(long courseID) {
 //       this.courseID = courseID;
 //   }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += Long.hashCode(classCourseID);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PersistentCourseInClass)) {
            return false;
        }
        PersistentCourseInClass other = (PersistentCourseInClass) object;
        if ((this.classCourseID == null && other.classCourseID != null) || (this.classCourseID != null && !this.classCourseID.equals(other.classCourseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentCourseInClass[ classCourseID=" + classCourseID + " ]";
    }

    /**
     * Builds a PersistentCourseInClass
     *
     * @return
     */
    public static PersistentCourseInClass buildEmptyPersistentCourseInClass() {
        PersistentCourseInClass result = new PersistentCourseInClass();
        result.setViewState(ViewState.invisible);

        return result;
    }

//    /**
//     * Builds a PersistentCourseInClass
//     *
//     * @return
//     */
//    public static PersistentCourseInClass buildFilledPersistentCourseInClass() {
//        PersistentCourseInClass result = new PersistentCourseInClass();
//        result.setClassCourseID(01);
//        result.setClassID(02);
//        result.setCourseID(03);
//        result.setNotBefore(DwoDateUtilities.getStartOfDay());
//        result.setNotAfter(DwoDateUtilities.getEndOfDay());
//        result.setType(CourseType.normal.ordinal());
//        result.setViewState(ViewState.invisible);
//        return result;
//    }
//
//    public DomCourseInClass4Teacher buildDomClassCourse4Teacher() {
//        DomClassCourse4Teacher classCourse = new DomClassCourse4Teacher();
//        PersistentCourseInClass.this.fillDomClassCourse4Teacher(classCourse);
//        return classCourse;
//    }
//
//    private void fillDomCourseInClass4Teacher(DomCourseInClass4Teacher classCourse) {
//        fillDomCourseInClass(classCourse);
//        classCourse.setViewState(this.viewState);
//    }

    public DomCourseInClass buildDomCourseInClass() {
        DomCourseInClass classCourse = new DomCourseInClass();
        PersistentCourseInClass.this.fillDomCourseInClass(classCourse);
        return classCourse;
    }

    private void fillDomCourseInClass(DomCourseInClass courseInClass) {
        courseInClass.setId(buildPersistenceId());
        courseInClass.setClassId(PersistentSchoolClass.buildPersistenceId(this.classID));
        courseInClass.setCourse(this.getCourse().buildDomCourse());
        courseInClass.setNotAfter(this.notAfter);
        courseInClass.setNotBefore(this.notBefore);
        courseInClass.setCourseType(CourseType.values()[this.type]);
    }

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(classCourseID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aClassCourseId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aClassCourseId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentCourseInClass.name(), aClassCourseId));
        return id;
    }

    /**
     * @return the viewState
     */
    public ViewState getViewState() {
        return viewState;
    }

    /**
     * @param viewState the viewState to set
     */
    public void setViewState(ViewState viewState) {
        this.viewState = viewState;
    }
}
