package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public interface DomMethodCodec extends JsonEncoderDecoder<DomMethod> {
	DomMethodCodec CODEC = GWT.create(DomMethodCodec.class);
	
	static String toString(DomMethod structure) {
		return CODEC.encode(structure).toString();
	}

	static DomMethod toValue(String json) {
		return CODEC.decode(json);
	}
	
}
