package fi.dwo.dwojapplet.gui;

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
final public class ProfileDescriptor implements Descriptor {
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
}