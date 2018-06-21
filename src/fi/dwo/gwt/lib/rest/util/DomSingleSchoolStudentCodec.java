package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;


public interface DomSingleSchoolStudentCodec extends JsonEncoderDecoder<DomSingleSchoolStudent> {
	DomSingleSchoolStudentCodec CODEC = GWT.create(DomSingleSchoolStudentCodec.class);
}
