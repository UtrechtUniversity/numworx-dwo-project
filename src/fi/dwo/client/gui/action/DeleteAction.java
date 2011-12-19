package fi.dwo.client.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import javax.swing.AbstractAction;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.system.TextMapper;

public class DeleteAction extends GuiAction  {

		private CourseMap map;
		private CourseMap parent;
		Course course;
		int row = 0;
		Sco sco;
		
		public DeleteAction() {
			setMap(null);
			Clipboard.addPropertyChangeListener("selection", this);
		}
		
		public DeleteAction(CourseMap map) {
			super("Delete");
			setMap(map);
		}

		void setMap(CourseMap map) {
			this.map = map;
			Object o = map == null ? null : map.getUserObject();
			if(o instanceof Sco)
			{	sco = (Sco) o;
				parent = sco.getCourse();
				String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCO);
				Object[] arguments = { sco.toString() };
				putValue(NAME, MessageFormat.format(format, arguments));
				setEnabled(true);
			}
			else if(o instanceof Course)
			{
				
				course = (Course) o;
				parent = map.getParentMap();
				Course[] courses = parent.getChildren();
				for (row = 0; row < courses.length; row++) {
					if(courses[row] == course)
						break;
				}
				String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_COURSE);
				Object[] arguments = { o.toString() };
				putValue(NAME, MessageFormat.format(format, arguments));
				setEnabled(true);
			} else 
			{
				setEnabled(false);
				putValue(NAME, TextMapper.getText("delete"));
			}
			
		}

		public void actionPerformed(ActionEvent e) {
			if(map == null)
				setMap(Clipboard.getSelection());
// verwijder clipboard als die wordt verwijdert
			if (Clipboard.getClipboard() == map)
			{	Clipboard.setClipboard(null);
				Clipboard.cmd = null;
			}
	
			if(course != null) 
			{
                if (instance.deleteCourse(course)) {
                	Clipboard.setSelection(null);
                    parent.removeChild(row);
                    
                 }

			} else if (sco != null)
			{
				instance.deleteSco(sco);
				Clipboard.setSelection(null);
				getCenter().updateCourse((Course) parent);
				return;
			}
			getCenter().updateMap(parent);
		}

	}