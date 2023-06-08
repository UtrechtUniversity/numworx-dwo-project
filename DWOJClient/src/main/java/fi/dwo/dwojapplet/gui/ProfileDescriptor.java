package fi.dwo.dwojapplet.gui;

import java.util.Set;

import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.Descriptor;

/**
 * A profile as a Descriptor
 * @author velth101
 * @see Descriptor
 * @see nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile
 *
 */
final public class ProfileDescriptor implements Descriptor, CourseMap {
	@Override
	public String getText() {
		return DWO.getDwoProfile().getDwoProfileText();
	}

	@Override
	public String getHeader() {
		return DWO.getDwoProfile().getDwoProfileDescription();
	}

	@Override
	public CourseMap[] getChildren() {
		return GuiCreator.instance().getCourseList();
	}

    public void setDescription(String text) {
      DWO.getDwoProfile().setDwoProfileText(text);
    }
    public void setHeader(String header) {
      DWO.getDwoProfile().setDwoProfileDescription(header);
    }

    @Override
    public void addChild(Course c) {
      
    }

    @Override
    public void removeChild(int i) {
      
    }

    @Override
    public void setChildren(CourseMap[] courses) {
      
    }

    @Override
    public Object getUserObject() {
      return this;
    }

    @Override
    public Set getChildNames() {
      return null;
    }

    @Override
    public CourseMap getParentMap() {
      return null;
    }
    
    public String toString() {
      return ModuleTreePanel.ALLE_MODULES;
    }
}