package fi.dwo.server.rest.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import fi.dwo.commons.persistence.entities.PersistentACL;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.server.PersistentDataManagers.core.ACLManager;

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
		DomCourseStudent build = c.buildDomCourseStudent();
		if(c.getImageData() != null) {
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
	}