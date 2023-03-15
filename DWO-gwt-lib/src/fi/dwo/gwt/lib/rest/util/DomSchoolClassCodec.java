package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public interface DomSchoolClassCodec extends JsonEncoderDecoder<DomSchoolClass> {
	DomSchoolClassCodec CODEC = GWT.create(DomSchoolClassCodec.class);
}
