package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.CourseNameDialog;
import fi.dwo.dwojapplet.gui.ScoNameDialog;

public class RenameAction extends GuiAction {

	private Sco sco;
	private Course course;
	private CourseMap map;
	
	
	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.action.GuiAction#setMap(fi.dwo.client.domain.CourseMap)
	 */
        @Override
	void setMap(CourseMap map) {
		setEnabled(canModify(map));
		this.map = map;
		this.sco = null;
		this.course = null;
		if(map != null) {
			Object o = map.getUserObject();
			if(o instanceof Sco)
				sco = (Sco) o;
			else if(o instanceof Course) {
				course = (Course) o;
			} else
				setEnabled(false);
		}
	}

	public RenameAction() {
		super(TextMapper.getText("rename"));
		Clipboard.addPropertyChangeListener("selection", this);
	}

	public RenameAction(CourseMap map) {
		super(TextMapper.getText("rename"));
		setMap(map);
	}
	
	public RenameAction(Sco sco) {
		super(TextMapper.getText("rename"));
		this.sco = sco;
	}
	
        @Override
	public void actionPerformed(ActionEvent event) {
		if(course != null) {
			if(CourseNameDialog.editCourse(course)) {
				getCenter().updateCourse(course);
			}
			return;
		}
		if(sco != null) {
            if (ScoNameDialog.editSco(sco)) {
            	getCenter().updateCourse(sco.getCourse());
            }
		}
	}
	
	

}
