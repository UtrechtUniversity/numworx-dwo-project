package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import nl.uu.fi.dwo.rest.dom.DomResultTree;


public interface DomResultTreeCodec extends JsonEncoderDecoder<DomResultTree> {
	DomResultTreeCodec CODEC = GWT.create(DomResultTreeCodec.class);
}
