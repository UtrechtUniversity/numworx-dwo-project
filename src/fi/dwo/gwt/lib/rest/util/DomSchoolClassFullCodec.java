package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;

public interface DomSchoolClassFullCodec extends JsonEncoderDecoder<DomSchoolClassFull> {
	DomSchoolClassFullCodec CODEC = GWT.create(DomSchoolClassFullCodec.class);
}
