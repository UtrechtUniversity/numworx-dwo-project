package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;


public interface DomTreeCodec extends JsonEncoderDecoder<DomTree<DomCourseOfClass>> {
	DomTreeCodec CODEC = GWT.create(DomTreeCodec.class);
}
