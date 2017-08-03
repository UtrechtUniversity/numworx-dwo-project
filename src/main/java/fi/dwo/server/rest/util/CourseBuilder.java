package fi.dwo.server.rest.util;

import java.util.function.Function;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import fi.dwo.commons.persistence.entities.PersistentCourse;

public final class CourseBuilder implements
			Function<PersistentCourse, DomCourseStudent> {
		private final String pfx;
		private final String hasRoleId;

		public CourseBuilder(String pfx) {
			this.pfx = pfx;
			this.hasRoleId = "";
		}

		public CourseBuilder(String pfx, DomHasRole hasRole) {
			super();
			this.pfx = pfx;
			this.hasRoleId = "&hasRoleId=" + hasRole.getId().getIdString();
		}

		public DomCourseStudent apply(PersistentCourse c) {
		DomCourseStudent build = c.buildDomCourseStudent();
		if(c.getImageData() != null) {
			build.setImage(pfx + "?courseId=" + c.getCourseID() + hasRoleId);
			build.setImageData(null);
		}
		return build;
}
	}