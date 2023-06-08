// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Course.java
package fi.dwo.dwojapplet.domain;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.CoursePanel;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import nl.uu.fi.dwo.rest.dom.entities.DomACL;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import java.awt.Image;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for the Course data.
 *
 * @author M.J.B. Kupers
 */
public class Course implements LessonGroup, Comparable<Course>, CourseMap, Descriptor {
    private static final Logger LOG = Logger.getLogger(Course.class.getName());

    private int courseID;

    private String name;

    private String description;

    private Sco scoList[];

    private Sco currentSco;

    private String imageUrl;
    private byte[] imageData;

    private CoursePanel coursePanel = null;

    private int dwoProfile;

    private int schoolID;
    private Boolean export;
    private CourseMap[] children;
    private int parentID;
    private boolean newParent;
    private boolean notVisible;

    public ClassCourse link; // optional backlink to classCourse, not unique!
    public Long sequencenr; // optional sequencenr
    
    
    public boolean parentChanged() {
        return newParent;
    }

    public void resetParent() {
        newParent = false;
    }

    /**
     * Creates a new Course object
     *
     */
    public Course() {
    }

    public Course(Course parent, Course[] children) {
        this.parentID = parent.getID();
        this.children = children;
    }

    public Course(Course parent) {
        this(parent, null);
    }

    /**
     * Returns the description of the course.
     *
     * @return The description of the course.
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the name of the course.
     *
     * @return The name of the course.
     *
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the dwoProfile of the course.
     *
     * @return The dwoProfile of the course.
     *
     */
    public int getDwoProfile() {
        return dwoProfile;
    }

    /**
     * Returns the schoolID of the course.
     *
     * @return The schoolID of the course.
     *
     */
    public int getSchoolID() {
        return schoolID;
    }

    /**
     * Returns the ID of the course.
     *
     * @return The ID of the course.
     *
     */
    @Override
    public int getID() {
        return courseID;
    }

    /**
     * Sets the ID of the course.
     *
     * @param courseID The ID of the course.
     */
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    /**
     * Sets the current sco of the course.
     *
     * @param currentSco The currentSco to set.
     */
    public void setCurrentSco(Sco currentSco) {
        this.currentSco = currentSco;
    }

    /**
     * Sets the current sco of the course.
     *
     * @return 
     */
    public Sco getCurrentSco() {
        return currentSco;
    }

    /**
     * Sets the description of the course.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        if (this.description == null || !this.description.equals(description)) {
            coursePanel = null;
        }
        this.description = description;
    }

    /**
     * Sets the name of the course.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the dwoProfile of the course.
     *
     * @param dwoProfile The dwoProfile to set.
     */
    public void setDwoProfile(int dwoProfile) {
        this.dwoProfile = dwoProfile;
    }

    /**
     * Sets the schoolID of the course.
     *
     * @param schoolID The schoolID to set.
     */
    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    /**
     * Returns the global url to the image of the Course.
     *
     * @return The global url to the image of the Course.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    private Image courseLogo;

    private CourseMap parentMap;

    /**
     * Logo van deze course. Op de grens tussen gui en domain.
     *
     * @return Image
     */
    public Image getCourseLogo() {
        if (courseLogo != null) {
            return courseLogo;
        }
        try {
            if (getImageData() != null) {
                Logo l = new Logo(getImageData());
                return courseLogo = l.getImage();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE,null,e);
        }
        if ((getImageUrl() != null)
                && (!getImageUrl().equals(""))) {
            /* Add FI logo */
            courseLogo = DwoHelper.getImage(GuiConstants.RESOURCES + getImageUrl());
        }
        if (courseLogo == null) {
            if (isWithChildren()) {
                courseLogo = DwoHelper.getResourceImage(GuiConstants.EMPTY_COURSE_MAP);
            } else {
                courseLogo = DwoHelper.getResourceImage(GuiConstants.EMPTY_COURSE_IMAGE);
            }
        }
        return courseLogo;
    }

    public void setCourseLogo(Image logo) {
        courseLogo = logo;
    }

    /**
     * Sets the global url to the image of the Course.
     *
     * @param imageUrl The global url to the image of the Course.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return the imageData
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * @param imageData the imageData to set
     */
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    /**
     * Returns the list of the sco's of this Course.
     *
     * @return The list of the sco's of this Course.
     */
    public Sco[] getScoList() {
        return scoList;
    }

    /**
     * Sets the list of the sco's of this Course.
     *
     * @param scoList The list of the sco's of this Course.
     */
    public void setScoList(Sco[] scoList) {
        this.scoList = scoList;
        if (scoList != null) {
            Arrays.sort(this.scoList);
            fixSnr();
        }
        coursePanel = null;
    }

    // FIXME fix sequence nr, database is buggy in this case.
    private void fixSnr() {
      for (int i = 0; i < scoList.length; i++) {
        if(scoList[i].getSequencenr() != (i+1)) {
          LOG.severe("FIX SEQUENCENRS FOR " + courseID + "." + i);
          scoList[i].setSequencenr(i+1);
          GuiCreator.instance().updateScoSequenceNr(scoList[i]);
        }
      }
      
    }

    /**
     * Finalizes the current sco. The sco can save the work.
     */
    public void end() {
        if (currentSco != null) {
            currentSco.end();
        }
    }

    /**
     * Indicates if this is the deepest LessonGroup.
     *
     * @return If this is the deepest LessonGroup it returns true. Otherwise it
     * returns false.
     * @see fi.dwo.client.domain.LessonGroup#isDeepestLevel()
     */
    @Override
    public boolean isDeepestLevel() {
        return false;
    }

    /**
     * Indicates if this is the highest LessonGroup.
     *
     * @return If this is the highest LessonGroup it returns true. Otherwise it
     * returns false.
     * @see fi.dwo.client.domain.LessonGroup#isHighestLevel()
     */
    @Override
    public boolean isHighestLevel() {
        return true;
    }

    /**
     * Returns the Course specific title for the LessonGroup.
     *
     * @return The Course specific title.
     * @see fi.dwo.client.domain.LessonGroup#getTitle()
     */
    @Override
    public String getTitle() {
        return TextMapper.getText(TextMapper.LG_COURSES);
    }

    /**
     * Returns a title represents the parent item.
     *
     * @return A title represents the parent item.
     * @see fi.dwo.client.domain.UserGroup#getParentTitle()
     */
    @Override
    public String getParentTitle() {
        return "";
    }

    /**
     * Returns a title represents the child item.
     *
     * @return A title represents the child item.
     * @see fi.dwo.client.domain.UserGroup#getChildTitle()
     */
    @Override
    public String getChildTitle() {
        String[] arguments = new String[1];
        arguments[0] = name;
        return TextMapper.format(TextMapper.LG_COURSE_CHILD, arguments);
    }

    /**
     * Returns a title represents the Ascending Order item.
     *
     * @return A title represents the Ascending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderAscTitle()
     */
    @Override
    public String getOrderAscTitle() {
        return TextMapper.getText(TextMapper.LG_COURSE_ORDER_ASC);
    }

    /**
     * Returns a title represents the Descending Order item.
     *
     * @return A title represents the Descending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderDescTitle()
     */
    @Override
    public String getOrderDescTitle() {
        return TextMapper.getText(TextMapper.LG_COURSE_ORDER_DESC);
    }

    /**
     * Returns a tooltip for the LessonGroup.
     *
     * @return A tooltip for the LessonGroup.
     * @see fi.dwo.client.domain.LessonGroup#getToolTip()
     */
    @Override
    public String getToolTip() {
        return name;
    }

    public void loadScos() {
        try {
            scoList = PersistenceFacade.instance().getSco(this);
            if(scoList != null) fixSnr(); // FIXME check sequencenr is correct.
            coursePanel = null;
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public CoursePanel getCoursePanel() {
        if (coursePanel == null) {
            if (scoList == null) 
            	loadScos();
            coursePanel = new CoursePanel(this);
        }

        return coursePanel;

    }

    /* (non-Javadoc)
     * @see fi.dwo.client.system.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Course o) {
        if (sequencenr == null && o.sequencenr == null)
          return getName().compareToIgnoreCase(o.getName());
        if (sequencenr == null) return +1;
        if (o.sequencenr == null) return -1;
        return sequencenr.compareTo(o.sequencenr);
    }

    /**
     * @param export the export to set
     */
    public void setExport(boolean export) {
        this.export = Boolean.valueOf(export);
    }
    public void setExport(Boolean export) {
      this.export = export;
    }
    
    /**
     * @return the export
     */
    public boolean isExport() {
        return export != null && export.booleanValue(); // default no export
    }

    public Boolean getExport() {
      return export;
    }
    
    
    @Override
    public String toString() {
        return getName();
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        if (parentID != this.parentID) {
            this.parentID = parentID;
            newParent = true;
        }
    }

    public static final CourseMap[] NO_CHILDREN = new CourseMap[0];
    public static final Sco[] NO_SCOS = new Sco[0];

    @Override
    public CourseMap[] getChildren() {
        return children;
    }

    @Override
    public void setChildren(CourseMap[] children) {
        this.children = children;
    }

    @Override
    public void addChild(Course child) {
        child.setParentID(getID());
        CourseMap[] lclChildren = getChildren();
        if (lclChildren == null) {
            lclChildren = new Course[]{child};
        } else {
            int length = lclChildren.length;
            Course[] n = new Course[length + 1];
            System.arraycopy(lclChildren, 0, n, 0, length);
            n[length] = child;
            lclChildren = n;
        }
        setChildren(lclChildren);
    }

    @Override
    public void removeChild(int index) {
        int length = children.length;
        ((Course) children[index]).setParentID(0);
        Course[] n = new Course[length - 1];
        System.arraycopy(children, 0, n, 0, index);
        System.arraycopy(children, index + 1, n, index, length - 1 - index);
        children = n;
    }

    public void removeChild(Course child) {
        CourseMap[] lclChildren = getChildren();
        for (int i = 0; i < lclChildren.length; i++) {
            if (lclChildren[i] == child) {
                removeChild(i);
                break;
            }
        }
    }

    public boolean isWithChildren() {
        return children != null;
    }

    @Override
    public Object getUserObject() {
        return this;
    }


    /*
     * (non-Javadoc)
     * @see fi.dwo.client.domain.Descriptor#getText()
     */
    @Override
    public String getText() {
        return getDescription();
    }

    /*
     * (non-Javadoc)
     * @see fi.dwo.client.domain.Descriptor#getHeader()
     */
    @Override
    public String getHeader() {
        return getName();
    }

    public Set getScoNames() {
        if (isWithChildren()) {
            return null;
        }
        Sco[] lclScoList = getScoList();
        int offset = lclScoList.length;
        Set names = new HashSet();
        for (int i = 0; i < offset; i++) {
            String lclName = lclScoList[i].getScoName();
            names.add(lclName);
        }
        return names;
    }

    @Override
    public Set getChildNames() {
        CourseMap[] lclChildren = getChildren();
        if (lclChildren == null) {
            return Collections.emptySet();
        }
        int offset = lclChildren.length;
        Set names = new HashSet();
        for (int i = 0; i < offset; i++) {
            String lclName = lclChildren[i].toString();
            names.add(lclName);
        }
        return names;
    }

    @Override
    public CourseMap getParentMap() {
        return parentMap;
    }

    public void setParentMap(CourseMap parentMap) {
        this.parentMap = parentMap;
    }

    public boolean isNotVisible() {
        return notVisible;
    }

    public void setNotVisible(boolean notVisible) {
        this.notVisible = notVisible;
    }

    public void setDomCourse(DomCourse sample) {
      setDwoProfile(DWO.getDwoProfileID());
      //setExport(null);  // by default NOT exported.
      try {
        setCourseID(MySQLPersistenceId.getNativeId(sample).intValue());
      } catch (Dwo2Exception e) {
      }
      //setDescription(null);
      //setNotVisible(false);
      setName(sample.getName());
      //setImageData(null);
      //setImageUrl(null);
      sequencenr = sample.getSequenceNr();
 
      if(sample.getWithChildren() != null && sample.getWithChildren()) {
        if(children == null)
          setChildren(NO_CHILDREN);
      } else {
        setChildren(null);
      }
      if( sample.getSchoolId() == null) {
        setSchoolID(0);
      } else {
        DomSchoolId o = new DomSchoolId(sample.getSchoolId());
        try {
          setSchoolID(MySQLPersistenceId.getNativeId(o).intValue());
        } catch (Dwo2Exception e) {
        }
      }
      if (sample.getParentID() == null) {
        setParentID(0);
      } else {
        DomCourse o = new DomCourse();o.setId(sample.getParentID());
        try {
          setParentID(MySQLPersistenceId.getNativeId(o).intValue());
        } catch (Dwo2Exception e) {
        }
      }
      
    }
    /** load a course via a DomCourseStudent.
     * missing export.
     * @param sample domcoursestudent to copy
     */
    public void setDomCourseStudent(DomCourseStudent sample) {
      setDwoProfile(DWO.getDwoProfileID());
      //setExport(null);  // by default NOT exported.
      try {
        setCourseID(MySQLPersistenceId.getNativeId(sample).intValue());
      } catch (Dwo2Exception e) {
      }
      setDescription(sample.getDescription());
      setNotVisible(sample.isNotVisible());
      setName(sample.getName());
      setImageData(sample.getImageData());
      setImageUrl(sample.getImage());
      setAcls(sample.getAcls());
      sequencenr = sample.getSequenceNr();
  
      Boolean withChildren = sample.getWithChildren();
      if(withChildren != null && withChildren.booleanValue()) {
        if (children == null)
          setChildren(NO_CHILDREN);
      } else {
        setChildren(null);
      }
      if( sample.getSchoolId() == null) {
        setSchoolID(0);
      } else {
        DomSchoolId o = new DomSchoolId(sample.getSchoolId());
        try {
          setSchoolID(MySQLPersistenceId.getNativeId(o).intValue());
        } catch (Dwo2Exception e) {
        }
      }
      if (sample.getParentID() == null) {
        setParentID(0);
      } else {
        DomCourse o = new DomCourse();o.setId(sample.getParentID());
        try {
          setParentID(MySQLPersistenceId.getNativeId(o).intValue());
        } catch (Dwo2Exception e) {
        }
      }
    }

    private List<DomACL> acls;
    public void setAcls(List<DomACL> acls) {
      this.acls = acls;
    }
    public List<DomACL> getAcls() {
      return acls;
    }
    
    
    
}
