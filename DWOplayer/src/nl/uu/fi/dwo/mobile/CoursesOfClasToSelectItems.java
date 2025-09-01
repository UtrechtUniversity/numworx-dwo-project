package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.osgi.util.function.Function;

import dagger.Reusable;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public final class CoursesOfClasToSelectItems
			implements Function<DomCoursesOfSchoolClass, List<SelectModuleItem>> {
        private DWOplayerParameters PARAMETERS;        
  
		@Inject CoursesOfClasToSelectItems(DWOplayerParameters pARAMETERS) {
          PARAMETERS = pARAMETERS;
        }

    private Collection<DomClassCourse> sort(List<DomMapEntry<PersistenceId, DomClassCourse>> list, DomCoursesOfSchoolClass t) {
			boolean again;
			List<DomClassCourse> classcourses = null;
			if(list != null) {
				classcourses = new ArrayList<DomClassCourse>(list.size());
				for(DomMapEntry<?,DomClassCourse> e: list) classcourses.add(e.getValue());
			}
			if(classcourses == null || classcourses.isEmpty()|| Boolean.FALSE.equals(t.getSchoolClass().getIconizer()))
				return classcourses;
			List<DomClassCourse> courses = new ArrayList<>(classcourses);
			do {
				again = false;
				more:
				for(int i = 0; i < courses.size(); i++ ) {
					DomClassCourse course = courses.get(i);
					if( getParentID(course, t) == null) {
						int j;
						for(j = i-1; j >= 0; j--) {
							if(getParentID(courses.get(j), t) == null) {
								if(j == i-1)
									break;
								courses.add(j+1, courses.remove(i));
								continue more;
							}
						}
						if(j == -1) {
							courses.add(0, courses.remove(i));
							continue more;
						}
					} else {
						PersistenceId pid = getParentID(course,t); int j;
						for(j = i-1; j>=0; j--) {
							if(pid.equals( getParentID(courses.get(j),t)) || pid.equals(getID(courses.get(j)))) {
								if(j == i-1) break;
								courses.add(j+1, courses.remove(i));
								continue more;
							}
						}
						if(j == -1) {
							again = true;
						}
					}
				}
			} while(again);
			return courses;
		}

		private PersistenceId getParentID(DomClassCourse course, DomCoursesOfSchoolClass t) {
			PersistenceId id = getID(course);
			DomCourse r = find(t, id);
			if(r != null)
			{
				id = r.getParentID();
//  IF ROOT, return null
				if(id == null || PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentCourse) .equals(0) || ! containsKey(t, id))
						return null;
				return id;
			}
			return null;
		}

		protected boolean containsKey(DomCoursesOfSchoolClass t, PersistenceId id) {
			List<DomMapEntry<PersistenceId, DomCourseStudent>> courses = t.getCourses();
			for(DomMapEntry<PersistenceId, DomCourseStudent> entry : courses)
				if(id .equals( entry.getKey())) return true;
			return false;
		}

		protected DomCourse find(DomCoursesOfSchoolClass t, PersistenceId id) {
			List<DomMapEntry<PersistenceId, DomCourseStudent>> courses = t.getCourses();
			for(DomMapEntry<PersistenceId, DomCourseStudent>entry: courses)
				if(id.equals(entry.getKey())) return entry.getValue();
			return null;
		}

		private PersistenceId getID(DomClassCourse course) {
			if(course != null) return course.getCourseId();
			return null;
		}

		@Override
		public List<SelectModuleItem> apply(DomCoursesOfSchoolClass t) {
			long now = System.currentTimeMillis() + DWO2player.timezone;
			Long serverNow = t.getFetchTimeStamp();
// timezone = diff now/servernow
			if(serverNow != null) {
				DWO2player.timezone += serverNow.longValue() - now;
				now = serverNow.longValue();
			}
			boolean inExam = PARAMETERS.inKiosk(); // only secured courses
			Map<PersistenceId, DomCourseStudent> courses = map(t.getCourses());
			fillTree(courses);
			Collection<DomClassCourse> classcourses = sort(t.getClassCourses(),t);
			List<SelectModuleItem> result = new ArrayList<SelectModuleItem>(classcourses.size());
			for (Iterator<DomClassCourse> iterator = classcourses.iterator(); iterator.hasNext();) {
				DomClassCourse domClassCourse = iterator.next();
				Date o = domClassCourse.getNotBefore();
		        if (o != null) {
		            if (now < o.getTime()) {
		                continue;
		            }
		        }
		        o = domClassCourse.getNotAfter();
		        if (o != null) {
		            if (now > o.getTime()) {
		                continue;
		            }
		        }			            
				DomCourseStudent course = courses.get(domClassCourse.getCourseId());
				SelectModuleItem item = new SelectModuleItem(course, domClassCourse);
				if(item.getType() == SelectModuleItem.Type.FOLDER)
					item.setChildren(new ArrayList<SelectModuleItem>());
				else
					if(inExam && item.getCourseType() == CourseType.normal)
						continue;
				result.add(item);
			}
			return result;
		}

		private void fillTree(Map<PersistenceId, DomCourseStudent> courses) {
			for(DomCourseStudent item: courses.values()) {
				createTreeIndex(item, courses);			
			}
			
		}

		private String createTreeIndex(DomCourseStudent item, Map<PersistenceId, DomCourseStudent> courses) {
			if (item.getTreeIndex() != null) return item.getTreeIndex();
			PersistenceId pid = item.getParentID();
			DomCourseStudent parent = courses.get(pid);
			String tree = Character.toString(treeSequence(item));
			if (parent != null) {
				tree = createTreeIndex(parent, courses) + tree;
			}
			item.setTreeIndex(tree);
			return tree;
		}

		protected char treeSequence(DomCourseStudent item) {
			if (item.getSequenceNr() == null) 
				return '_';
			return (char) (item.getSequenceNr().shortValue()+'!');
		}

		private Map<PersistenceId, DomCourseStudent> map(
				List<DomMapEntry<PersistenceId, DomCourseStudent>> courses) {
			Map<PersistenceId, DomCourseStudent> map = new HashMap<>();
			for(DomMapEntry<PersistenceId, DomCourseStudent> entry: courses)
				map.put(entry.getKey(), entry.getValue());
			return map;
		}
	}