package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;

public interface DomCourseCodec extends JsonEncoderDecoder<DomCourse> {
	DomCourseCodec CODEC = GWT.create(DomCourseCodec.class);
}
