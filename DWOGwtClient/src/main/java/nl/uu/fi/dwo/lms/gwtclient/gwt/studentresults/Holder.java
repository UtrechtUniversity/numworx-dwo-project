package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;

class Holder {
	Collection<String> foreknowledge;
	DomStudentModelObj obj;
	DomStudentModelScore<?> s;
	Holder() { foreknowledge = Collections.emptySet(); }
	Holder(DomStudentModelObj obj, DomStudentModelScore<?> s) {
		this.obj = obj;
		this.s = s;
		foreknowledge = obj.getInfo().getVoorkennis();
		if (foreknowledge != null) foreknowledge = new HashSet<>(foreknowledge);
		else foreknowledge = Collections.emptySet();
	}
	
}