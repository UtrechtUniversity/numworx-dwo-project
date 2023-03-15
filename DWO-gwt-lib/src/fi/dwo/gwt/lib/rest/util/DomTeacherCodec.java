package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;

public interface DomTeacherCodec extends JsonEncoderDecoder<DomTeacher> {
	DomTeacherCodec CODEC = GWT.create(DomTeacherCodec.class);
}
