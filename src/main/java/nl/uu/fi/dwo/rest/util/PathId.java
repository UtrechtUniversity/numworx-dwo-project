package nl.uu.fi.dwo.rest.util;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;

public class PathId {

	public static String getId(DomContext rest) {
		try {
			return "1" + rest.getDomHasRole().getId().getIdString().substring(23).replace(';', '-');
		} catch (Exception e) {
			return "-";
		}
	}

}
