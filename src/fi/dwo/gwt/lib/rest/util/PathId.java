package fi.dwo.gwt.lib.rest.util;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;

public class PathId {

	public static String getId(DomContext rest) {
		try {
			return "0" + rest.getDomHasRole().getId().getIdString().substring(23);
		} catch (Exception e) {
			return "-";
		}
	}

}
