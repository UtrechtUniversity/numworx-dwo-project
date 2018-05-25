package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;

public interface DomCourseOfClassCodec extends JsonEncoderDecoder<DomCourseOfClass> {
	DomCourseOfClassCodec CODEC = GWT.create(DomCourseOfClassCodec.class);
}
