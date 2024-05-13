package nl.numworx.uploadwidgetgwt.client;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomToken;

public interface DomTokenCodec extends JsonEncoderDecoder<DomToken> {
	DomTokenCodec CODEC = GWT.create(DomTokenCodec.class);
	
	static String toString(DomToken structure) {
		return CODEC.encode(structure).toString();
	}

	static DomToken toValue(String json) {
		return CODEC.decode(json);
	}
	
}
