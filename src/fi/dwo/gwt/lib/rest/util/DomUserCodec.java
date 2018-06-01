package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomUser;

public interface DomUserCodec extends JsonEncoderDecoder<DomUser> {
	DomUserCodec CODEC = GWT.create(DomUserCodec.class);
}
