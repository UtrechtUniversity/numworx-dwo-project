package fi.dwo.server.rest;

import java.util.Comparator;

import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

class DomScoContextComparator implements Comparator<DomScoContext> {
	@Override
	public int compare(DomScoContext o1, DomScoContext o2) {
		Long s1 = o1.getSequencenr();
		Long s2 = o2.getSequencenr();
		return s1.compareTo(s2);
	}
}