package fi.dwo.server.rest.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import fi.dwo.commons.persistence.entities.PersistentACL;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseData;
import fi.dwo.server.PersistentDataManagers.core.ACLManager;
import fi.dwo.server.PersistentDataManagers.core.CourseDataManager;

public final class CourseBuilder implements
			Function<PersistentCourse, DomCourseStudent> {
		private final String pfx;
		private final String hasRoleId;
		private final boolean access;
		public CourseBuilder(String pfx) {
			this.pfx = pfx;
			this.hasRoleId = "";
			this.access = false;
		}

		public CourseBuilder(String pfx, DomHasRole hasRole, boolean access) {
			super();
			this.pfx = pfx;
			this.access = access;
			this.hasRoleId = "&hasRoleId=" + hasRole.getId().getIdString();
		}

		public DomCourseStudent apply(PersistentCourse c) {
		DomCourseStudent build = buildDomCourseStudent(c);
		if(build.getImageData() != null) {
			build.setImage(pfx + "?courseId=" + c.getCourseID() + hasRoleId);
			build.setImageData(null);
		} else if ("" .equals(build.getImage()))
			build.setImage(null);
		if (access) {
		  try {
		    List<PersistentACL> list = ACLManager.findByCourse(c);
		    build.setAcls(list.stream().map(PersistentACL::buildDomACL).collect(Collectors.toList()));
		  } catch(Exception ignore) {
		    
		  }
		}
		return build;
}

		private DomCourseStudent buildDomCourseStudent(PersistentCourse c) {
			DomCourseStudent dom = c.buildDomCourseStudent();
// zie https://numworx.atlassian.net/browse/LMS-468
			try {
				PersistentCourseData data = CourseDataManager.findEntity(c.getCourseID());
				if (data != null) data.fillDomCourseStudent(dom);
			} catch (Exception oops) {}
			
			return dom;
		}
	}