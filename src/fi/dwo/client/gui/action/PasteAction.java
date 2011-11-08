package fi.dwo.client.gui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import fi.dwo.client.domain.AppletConfig;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.CenterPanel;
import fi.dwo.client.gui.CourseManagementPanel;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ModuleTreePanel;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

public class PasteAction extends GuiAction
	{
		CourseMap map;

		public void actionPerformed(ActionEvent e) {
			if(map == null)
				map = Clipboard.getSelection();
			if(map == null)
				return;
			Object object = map.getUserObject();
			Object clip = Clipboard.getClipboard().getUserObject();
			System.out.println( Clipboard.cmd  + " " + clip + " into " + object);
			instance = GuiCreator.instance();

			if("cut".equals(Clipboard.cmd))
			{
				if(clip instanceof Course)
				{
					cutCourse((Course)clip, object);
				} else if(clip instanceof Sco && object instanceof Course)
				{
					Course course = (Course) object;
					Sco sco = (Sco)clip;
					if (course.isWithChildren() ||
						  sco.getCourse() == course && sco.getSequencenr()==course.getScoList().length)
						return;
					cutSco( sco, course);
				} else if(clip instanceof Sco && object instanceof Sco)
				{
					Sco before = (Sco)object;
					Sco sco = (Sco) clip;
					if(sco.getID() != before.getID())
						cutSco(sco, before);
				}
			} else if("copy".equals(Clipboard.cmd))
			{
				if(clip instanceof Sco && object instanceof Course)
				{
					Course course = (Course)object;
					if(course.isWithChildren())
						return;
					Sco sco = (Sco)clip;
					// copy eigen activiteiten
					copySco(course, sco);
				} else
				if(clip instanceof Course && object instanceof String)
				{
					copyCourseTop( (Course) clip, object == ModuleTreePanel.STANDAARD_DWO_MODULES);
				} else if(clip instanceof Course && object instanceof Course)
				{
					Course source = (Course) clip;
					Course dest   = (Course) object;
					if(!dest.isWithChildren())
						return;
					if(dest.getSchoolID() == 0 && hasAdminRight) return;
					// TODO check copy parent into child.
					copyCourseMap(dest, source);
				}
				
			}
		}
		private void copySco(Course course, Sco sco) {
			AppletConfig config = instance.getAppletConfigFromSco(sco);
			String name = config.getName();
			name = CourseManagementPanel.replaceDuplicate(name, course.getScoNames());
			instance.addSco(course, config, name, sco.getDescription(), sco.isShowScore());
		}
/**
 * 
 * @param course
 * @param b true if standaard modules.
 */
		private void copyCourseTop(Course course, boolean b) {
			CourseMap oldmap = getParentMap(course);
			if(oldmap.getUserObject() == map.getUserObject()) // copy/paste in zelfde map?
				return;
			if(b && !hasAdminRight)
				return;
			String name = course.getName();
			name = CourseManagementPanel.replaceDuplicate(name, map.getChildNames());
			boolean isMap = course.isWithChildren();
			Course parent = b?new Course():null;
			String description = course.getDescription();
			Course c = instance.addCourse(name, description, parent, isMap);
			map.addChild(c);
			getCenter().updateMap(map);
			if(isMap) {
				copyCourseMap(c, course.getChildren());
			} else {
				copySco(c, course);
			}
			
			// recurse copyCourseMap, copySco
		}


		private void copyCourseMap(Course c, Course[] children) {
			for (int i = 0; i < children.length; i++) {
				copyCourseMap(c, children[i]);
			}
		}
		
		private void copyCourseMap(Course dest, Course course) {
			String name = course.getName();
			name = CourseManagementPanel.replaceDuplicate(name, dest.getChildNames());
			boolean isMap = course.isWithChildren();
			String description = course.getDescription();
			Course c = instance.addCourse(name, description, dest, isMap);
			if(c == null)
			{
				System.err.println("copyCourseMap failed: "+course + ", " + dest + ", " + isMap);
				return;
			}
			map.addChild(c);
			getCenter().updateMap(map);
			if(isMap) {
				copyCourseMap(c, course.getChildren());
			} else {
				copySco(c, course);
			}
				
			
		}
		private void copySco(Course dest, Course course) {
			Sco[] list = course.getScoList();
			for (int i = 0; i < list.length; i++) {
				copySco(dest, list[i]);
			}
	
}
		private void cutSco(Sco sco, Course course) {
			if(course.getScoList() == null) course.loadScos();
			sco.setSequencenr(course.getScoList().length+1); // to the end.
			cutSco_1(sco, course);			
		}

		private void cutSco_1(Sco sco, Course course) {
			Course old = sco.getCourse();
			sco.setCourse(course);
			if(old.getID() != course.getID())
			{
				String name = sco.getScoName();
				name = CourseManagementPanel.replaceDuplicate(name, course.getScoNames());
				sco.setName(name);
			}
			instance.updateSco(sco);
//			old.loadScos(); course.loadScos(); // refresh sco's (zonder dbaccess mogelijk?)
			getCenter().updateCourse(old);
			getCenter().updateCourse(course);
		}

		private void cutSco(Sco sco, Sco before) {
			Course course = before.getCourse();
			sco.setSequencenr(before.getSequencenr()); // before that sco.
			cutSco_1(sco, course);
		}

		private void cutCourse(Course course, Object object) {
			CourseMap oldmap = getParentMap(course);
			if(oldmap.getUserObject() == object) // cut/paste in zelfde map?
				return;
			int id = course.getID();
			if(object instanceof Course)
			{
				Course p = (Course)object;
				int pid = p.getParentID();
				while(pid != 0)
				{
					if(pid == id)
						return;			// course move into course
					try {
						pid = ((Course) PersistenceFacade.instance().get(pid, Course.class)).getParentID();
					} catch (PersistenceException e1) {
						e1.printStackTrace();
						return; 
					}
				}
			}
			String name = course.getName();
			name = CourseManagementPanel.replaceDuplicate(name, map.getChildNames());
			if( object instanceof String ) // toplevel
			{
				removeChild(oldmap, course);
				course.setParentID(0);
				course.setName(name);
				if(object.equals(ModuleTreePanel.STANDAARD_DWO_MODULES))
					course.setSchoolID(0);
				else // School Modules.
					course.setSchoolID(instance.getUser().getSchool().getSchoolID());
			} else if( object instanceof Course)
			{
				Course map = (Course)object;
				if(map.isWithChildren())
				{
					course.setSchoolID(map.getSchoolID());
					course.setName(name);
					removeChild(oldmap, course);
					map.addChild(course);
					
				} else
					return;
			}
			instance.updateCourse(course);
			getCenter().updateMap(map);
			getCenter().updateMap(oldmap);
			//cmd = "copy"; // 2x paste wordt altijd copy
		}

		private void removeChild(CourseMap oldmap, Course course) {
			Course[] children = oldmap.getChildren();
			for (int i = 0; i < children.length; i++) {
				if(children[i] == course)
				{
					oldmap.removeChild(i);
					break;
				}
			}
		}

		private CourseMap getParentMap(Course course) {
			int id = course.getParentID();
			if(id == 0)
			{
				id = course.getSchoolID();
				if(id == 0)
					return ModuleTreePanel.STANDAARD_DWO_MAP;
				else
					return ModuleTreePanel.SCHOOL_MAP;
			}
			try {
				return (CourseMap) PersistenceFacade.instance().get(id, Course.class);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(), e);
			} 
		}

		public PasteAction(CourseMap object) {
			super(TextMapper.getText("paste"));
			this.map = object;
		}
		
		public PasteAction() {
			this(null);
			setEnabled(false);
			Clipboard.addPropertyChangeListener("selection", this);
			
		}
		void setMap(CourseMap map) {
			setEnabled(
					Clipboard.getClipboard() != null &&
					canModify(map)
			);
			
			
		}
		
	}