package fi.dwo.client.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.gui.AddScoDialog;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.CourseNameDialog;
import fi.dwo.client.gui.ModuleTreePanel;
import fi.dwo.client.system.TextMapper;

public class NewAction extends GuiAction {

		private final static Course STANDARD_MAP = new Course();
		private CourseMap map;
		boolean ismap, submap;
		Course course;
		
		
		public NewAction(CourseMap map, boolean submap) {
			super();
			this.submap = submap;
			setMap(map);
			
		}

		void setMap(CourseMap map) {
			setEnabled(canModify(map));
			this.map = map;
			if(map instanceof Course)
			{ course = (Course) map.getUserObject();
			  ismap = course.isWithChildren();
			  
			} else
			if (map == ModuleTreePanel.SCHOOL_MAP)
			{
				course = null;
				ismap = true;
			} else {
				course = STANDARD_MAP;
				ismap = true;
			}
/* vier gevallen: 
 * ismap submap
 * true  true     addmap enabled
 * false true     addmap disabled
 * true  false    addcourse
 * false false    addsco			
 */
			if(ismap)
			{
				if(submap)
					putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_MAP));
				else 
					putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_COURSE));
			} else
			if(submap)					
					setEnabled(false);
			else 
					putValue(NAME, TextMapper.getText(TextMapper.GUIS_ADD_SCO));
		}


		public NewAction(boolean ismap, boolean submap)
		{
			this.ismap = ismap;
			this.submap = submap;
			if(submap && ismap)
				putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_MAP));
			else if(ismap)
				putValue(NAME, TextMapper.getText(TextMapper.GUIC_ADD_COURSE));
			else 
			{
				putValue(NAME, TextMapper.getText(TextMapper.GUIS_ADD_SCO));
			}
				Clipboard.addPropertyChangeListener("selection", this);
		}
		
		
		public void actionPerformed(ActionEvent e) {
//			if(map == null)
//				map = Clipboard.getSelection(); // FIXME en course dan?
			if(map == null)
				return;
// FIXME werkt niet goed, als updateMap werkt op een toplevel map.
			if(submap)
			{
				Course child = CourseNameDialog.addMap(DwoHelper.getApplet(), course);
				if(child != null) 
				{
					map.addChild(child);
					getCenter().updateMap(map);
				}
			}
			else if(ismap)
			{
				Course child = CourseNameDialog.addCourse(DwoHelper.getApplet(), course);
				if(child != null)
				{
					map.addChild(child);
					getCenter().updateMap(map);
				}
			}
			else
				AddScoDialog.addSco(DwoHelper.getApplet(), course);

		}

	}