package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;


public interface DomSchoolsRolesAndClassesV2Codec extends JsonEncoderDecoder<DomSchoolsRolesAndClassesV2> {
	DomSchoolsRolesAndClassesV2Codec CODEC = GWT.create(DomSchoolsRolesAndClassesV2Codec.class);
}
