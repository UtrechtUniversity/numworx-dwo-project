package fi.dwo.dwojapplet.gui.action;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.CourseManagementPanel;
import fi.dwo.dwojapplet.gui.ModuleTreePanel;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.AbstractScoContextManager;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;


public class PasteAction extends GuiAction
	{
        private static final Logger LOG = Logger.getLogger(PasteAction.class.getName());
  
		CourseMap map;

		public void actionPerformed(ActionEvent e) {
			if(map == null)
				map = Clipboard.getSelection();
			if(map == null)
				return;
			Object object = map.getUserObject();
			Object clip = Clipboard.getClipboard().getUserObject();
			System.out.println( Clipboard.cmd  + " " + clip + " into " + object);

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
					getCenter().updateCourse(course);
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
					if(dest.getSchoolID() == 0 && !hasAdminRight()) return;
					// check copy parent into child.
					if(checkAncestor(dest, source)) return;
					copyCourseMap(dest, source, map);
				} else if(clip instanceof Sco && object instanceof Sco) 
				{
					Sco source = (Sco)clip;
					Sco dest   = (Sco)object;
					// TODO WAT GAAT HIER GEBEUREN
				}
				
				
				
			}
		}
		private boolean checkAncestor(CourseMap dest, Course source) {
			if(!source.isWithChildren()) return false; // source is leaf-course (module)
			do {
				if (dest == source) return true;
				dest = dest.getParentMap();
			} while (dest != null);
			return false;
		}
		private void copySco(Course course, Sco sco) {
// verify we have scos
			Sco[] list = course.getScoList();
			if(list == null) {
				course.loadScos();
				list = course.getScoList();
				if(list == null) return;
			}
			AppletConfig config = instance().getAppletConfigFromSco(sco);
			String name = config.getName();
			name = CourseManagementPanel.replaceDuplicate(name, course.getScoNames());
			AbstractScoContextManager manager = instance().getScoContextManager();
            Sco s = instance().getDWO().addSco(course, config, name, sco.getDescription(), sco.isShowScore(), sco.getImageData(),manager);
// FIXME DIT IS NIET GOED, MOET NAAR addSco van de DWO
// is een kopie van ScoMananagementPanel
			if(s == null) return;
			Sco[] as = course.getScoList();
            /* Create a larger array and add the item */
            Sco[] tmp = new Sco[as.length + 1];
            System.arraycopy(as, 0, tmp, 0, as.length);
            tmp[tmp.length - 1] = s;
            course.setScoList(tmp);
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
			if(b && !hasAdminRight())
				return;
			String name = course.getName();
			name = CourseManagementPanel.replaceDuplicate(name, map.getChildNames());
			boolean isMap = course.isWithChildren();
			Course parent = b?new Course():null;
			String description = course.getDescription();
			Course c = instance().addCourse(name, description, parent, isMap);
            if(c == null)
            {
                LOG.warning("copyCourseMap failed: "+course + ", " + map + ", " + isMap);
                return;
            }
			updateLogo(course,c);
			map.addChild(c);
			if(isMap) {
				copyCourseMap(c, course.getChildren());
			} else {
				copySco(c, course);
			}
			getCenter().updateMap(map);
			
			// recurse copyCourseMap, copySco
		}


		private void copyCourseMap(Course c, CourseMap[] children) {
			for (int i = 0; i < children.length; i++) {
				copyCourseMap(c, (Course)children[i]);
			}
// save ordering 
			if(DWO.SEQUENCE) instance().setCourseSequence(c, c.getChildren());
			
		}
		
		private Course copyCourseMap(Course dest, Course course) {
			String name = course.getName();
			name = CourseManagementPanel.replaceDuplicate(name, dest.getChildNames());
			boolean isMap = course.isWithChildren();
			String description = course.getDescription();
			Course c = instance().addCourse(name, description, dest, isMap);
			if(c == null)
			{
				LOG.warning("copyCourseMap failed: "+course + ", " + dest + ", " + isMap);
				return null;
			}
			updateLogo(course, c);
			dest.addChild(c);
			if(isMap) {
				copyCourseMap(c, course.getChildren());
			} else {
				copySco(c, course);
			}
			return c;
			
		}
    private void updateLogo(Course course, Course copy) {
      // Wim: getImageUrl is "" 
			if(course.getImageData() != null || course.getImageUrl() != null && course.getImageUrl().length() > 0)				
			{
				copy.setImageData(course.getImageData());
				copy.setImageUrl(course.getImageUrl());
				copy.setCourseLogo(course.getCourseLogo());
				if(copy.getImageUrl() != null && !copy.getImageUrl().isEmpty() && copy.getImageData() == null) {
				   try {
			            ByteArrayOutputStream output = new ByteArrayOutputStream();
			            BufferedImage img = ImageIO.read(new URL(copy.getImageUrl()));
			            Image reduced;
			            int w = img.getWidth();
			            int h = img.getHeight();
			            if (w <= 252 && h <= 160) {
			                reduced = img;
			            } else {
			                float scalex = w/252f;
			                float scaley = h/160f;
			                float scale = Math.max(scalex, scaley);
			                w = Math.round(w/scale);
			                h = Math.round(h/scale);
			                reduced = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			            }
			            if (reduced instanceof BufferedImage) {
			                img = (BufferedImage) reduced;
			            } else {
			                img = new BufferedImage(Math.min(252, w), Math.min(160, h), BufferedImage.TYPE_INT_ARGB);
			                img.createGraphics().drawImage(reduced, 0, 0, null);
			            }
			            ImageIO.write(img, "png", output);
			            output.close();
			            byte[] data = output.toByteArray();
			            copy.setImageData(data);
				   
				   } catch (Exception e) {
				     LOG.log(Level.WARNING, "update logo", e);
				   }
				}
				instance().updateLogo(copy);
			}
    }
		
		private void copyCourseMap(Course dest, Course course, CourseMap map) {
			Course c = copyCourseMap(dest, course);
			getCenter().updateMap(map);

		}
		
		private void copySco(Course dest, Course course) {
			Sco[] list = course.getScoList();
			if(list == null)
			{	course.loadScos();
				if(null == (list = course.getScoList()))
						return;
			}
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
			} else {
			  int seq = (int) sco.getSequencenr()-1;
			  if(course.getScoList() == null) course.loadScos(); // should not happen;
			  List<Sco> scos = new LinkedList<>(Arrays.asList(course.getScoList()));
			  int os = scos.indexOf(sco);
			  scos.remove(sco);
			  if(seq <= os)
			    scos.add(seq, sco);
			  else
			    scos.add(seq-1, sco);
			  seq = 1;
			  for(Sco s: scos) {
			    if (s.getSequencenr() != seq) {
			      s.setSequencenr(seq);
			      if(s != sco) instance().updateSco(s);
			    }
			    seq ++;
			  } 
			}
			instance().updateSco(sco);
//			old.loadScos(); course.loadScos(); // refresh sco's (zonder dbaccess mogelijk?)
			old.setScoList(null); course.setScoList(null);
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
						pid = (PersistenceFacade.instance().getCourse(pid)).getParentID();
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
					course.setSchoolID(instance().getUser().getSchool().getSchoolID());
				map.addChild(course);
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
			instance().updateCourse(course);
			getCenter().updateMap(map);
			getCenter().updateMap(oldmap);
			//cmd = "copy"; // 2x paste wordt altijd copy
		}

		private void removeChild(CourseMap oldmap, Course course) {
			CourseMap[] children = oldmap.getChildren();
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
				return PersistenceFacade.instance().getCourse(id);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(), e);
			} 
		}

		public PasteAction(CourseMap object) {
			super(TextMapper.getText("paste"));
			this.map = object;
			setMap(map);
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
