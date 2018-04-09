package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

public interface DomUserFullCodec extends JsonEncoderDecoder<DomUserFull> {
	DomUserFullCodec CODEC = GWT.create(DomUserFullCodec.class);
}
