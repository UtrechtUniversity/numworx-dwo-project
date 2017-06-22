package fi.dwo.server.rest;

import java.util.Comparator;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;

final class DomCourseStudentComparator implements Comparator<DomCourseStudent> {
	@Override
	public int compare(DomCourseStudent o1, DomCourseStudent o2) {
		Long l1 = o1.getSequenceNr();
		Long l2 = o2.getSequenceNr();
		if(l1 == null && l2 == null) {
			return o1.getName().compareTo(o2.getName());
		}
		if(l1 == null) return +1;
		if(l2 == null) return -1;
		return l1.compareTo(l2);
	}
}