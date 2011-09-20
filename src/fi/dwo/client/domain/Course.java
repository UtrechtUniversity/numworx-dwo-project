// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Course.java

package fi.dwo.client.domain;

import java.awt.Image;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.gui.CenterSubPanel;
import fi.dwo.client.gui.CoursePanel;
//import fi.dwo.client.gui.DwoMessageDialog;
import fi.dwo.client.gui.GuiConstants;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

/**
 * This class is responsible for the Course data.
 * @author M.J.B. Kupers
 */
public class Course implements LessonGroup, Comparable, CourseMap, Descriptor {
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
    private boolean export;
    private Course  children[];
    private int parentID;
    private boolean newParent;
    
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

    public Course(Course parent, Course[] children)
    {
    	this.parentID = parent.getID();
    	this.children = children;
    }
    
    public Course(Course parent)
    {
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
     * @param currentSco The currentSco to set.
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
     * @param schoolID  The schoolID to set.
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
     * Logo van deze course.
     * Op de grens tussen gui en domain.
     * @return Image
     */
    public Image getCourseLogo() {
    	if(courseLogo != null)
    		return courseLogo;
    	try {
    		if(getImageData() != null)
    		{
    			Logo l = new Logo(getImageData());
    			return courseLogo = l.getImage();
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if ((getImageUrl() != null)
                && (!getImageUrl().equals(""))) {
            /* Add FI logo */
            courseLogo = DwoHelper.getImage(GuiConstants.RESOURCES + getImageUrl());
        } 
        if(courseLogo == null)
        {
            if(isWithChildren())
            	courseLogo = DwoHelper.getResourceImage(GuiConstants.EMPTY_COURSE_MAP);
            else
            	courseLogo = DwoHelper.getResourceImage(GuiConstants.EMPTY_COURSE_IMAGE);
        }
        return courseLogo;
    }
    
    
    public void setCourseLogo(Image logo)
    {
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
        Arrays.sort(this.scoList);
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
     *         returns false.
     * @see fi.dwo.client.domain.LessonGroup#isDeepestLevel()
     */
    public boolean isDeepestLevel() {
        return false;
    }

    /**
     * Indicates if this is the highest LessonGroup.
     * 
     * @return If this is the highest LessonGroup it returns true. Otherwise it
     *         returns false.
     * @see fi.dwo.client.domain.LessonGroup#isHighestLevel()
     */
    public boolean isHighestLevel() {
        return true;
    }

    /**
     * Returns the Course specific title for the LessonGroup.
     * 
     * @return The Course specific title.
     * @see fi.dwo.client.domain.LessonGroup#getTitle()
     */
    public String getTitle() {
        return TextMapper.getText(TextMapper.LG_COURSES);
    }

    /**
     * Returns a title represents the parent item.
     * @return A title represents the parent item.
     * @see fi.dwo.client.domain.UserGroup#getParentTitle()
     */
    public String getParentTitle() {
        return "";
    }

    /**
     * Returns a title represents the child item.
     * @return A title represents the child item.
     * @see fi.dwo.client.domain.UserGroup#getChildTitle()
     */
    public String getChildTitle() {
        String[] arguments = new String[1];
            arguments[0] = name;
        String s = TextMapper.getText(TextMapper.LG_COURSE_CHILD);
        return MessageFormat.format(s, arguments);
    }

    /**
     * Returns a title represents the Ascending Order item.
     * @return A title represents the Ascending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderAscTitle()
     */
    public String getOrderAscTitle() {
        return TextMapper.getText(TextMapper.LG_COURSE_ORDER_ASC);
    }

    /**
     * Returns a title represents the Descending Order item.
     * @return A title represents the Descending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderDescTitle()
     */
    public String getOrderDescTitle() {
        return TextMapper.getText(TextMapper.LG_COURSE_ORDER_DESC);
    }

    /**
     * Returns a tooltip for the LessonGroup.
     * @return A tooltip for the LessonGroup.
     * @see fi.dwo.client.domain.LessonGroup#getToolTip()
     */
    public String getToolTip() {
        return name;
    }
    
    public void loadScos() {
        try {
            scoList = (Sco[]) PersistenceFacade.instance().get(Sco.class, this);
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }        
    }
    
    public CoursePanel getCoursePanel() {
        if(coursePanel == null) {
            loadScos();
            coursePanel = new CoursePanel(this);
        } 
        
        return coursePanel;
        
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.system.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        Course c = (Course) o;
        return getName().toLowerCase().compareTo(c.getName().toLowerCase());
    }

	/**
	 * @param export the export to set
	 */
	public void setExport(boolean export) {
		this.export = export;
	}

	/**
	 * @return the export
	 */
	public boolean isExport() {
		return export;
	}
	
	public String toString() {
		return getName();
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		if(parentID != this.parentID)
		{
			this.parentID = parentID;
			newParent = true;
		}
	}


	public static final Course[] NO_CHILDREN = new Course[0];
	public static final Sco[] NO_SCOS = new Sco[0];
	
	public Course[] getChildren() {
		return children;
	}

	public void setChildren(Course[] children) {
		this.children = children;
	}

	public void addChild(Course child)
	{
		child.setParentID(getID());
		if(children == null)
		{
			children = new Course[] { child };
		} else {
			int length = children.length;
			Course[] n = new Course[length+1];
			System.arraycopy(children, 0, n, 0, length);
			n[length]=child;
			children = n;
		}
	}
	
	public void removeChild(int index)
	{
		int length = children.length;
		children[index].setParentID(0);
		Course[] n = new Course[length-1];
		System.arraycopy(children, 0, n, 0, index);
		System.arraycopy(children, index+1, n, index, length-1-index);
		children = n;
	}
	
	public void removeChild(Course child)
	{
		for (int i = 0; i < children.length; i++) {
			if(children[i] == child)
			{
				removeChild(i);
				break;
			}
		}
	}

	public boolean isWithChildren() {
		return children != null;
	}

	public Object getUserObject() {
		return this;
	}


	/*
	 * (non-Javadoc)
	 * @see fi.dwo.client.domain.Descriptor#getText()
	 */
	public String getText() {
		return getDescription();
	}
	
	/*
	 * (non-Javadoc)
	 * @see fi.dwo.client.domain.Descriptor#getHeader()
	 */
	public String getHeader() {
		return getName();
	}

	public Set getScoNames() {
		if(isWithChildren())
			return null;
		Sco[] scoList = getScoList();
		int offset = scoList.length;
		Set names = new HashSet();
		for (int i = 0; i < offset; i++) {
			String name = scoList[i].getScoName();
			names.add(name);
		}
		return names;
	}
	
	public Set getChildNames() {
		Course[] children = getChildren();
		if(children == null)
			return null;
		int offset = children.length;
		Set names = new HashSet();
		for(int i = 0; i < offset; i++)
		{
			String name = children[i].getName();
			names.add(name);
		}
		return names;
	}

	public CourseMap getParentMap() {
		return parentMap;
	}

	public void setParentMap(CourseMap parentMap) {
		this.parentMap = parentMap;
	}
	
	
	
	
}