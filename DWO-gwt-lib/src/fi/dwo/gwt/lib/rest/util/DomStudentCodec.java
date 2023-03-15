package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomStudent;

public interface DomStudentCodec extends JsonEncoderDecoder<DomStudent> {
	DomStudentCodec CODEC = GWT.create(DomStudentCodec.class);
}
