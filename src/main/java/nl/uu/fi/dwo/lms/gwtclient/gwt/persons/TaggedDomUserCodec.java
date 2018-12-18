package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomUser;


public interface TaggedDomUserCodec extends JsonEncoderDecoder<TaggedDomUser<DomUser>> {
	TaggedDomUserCodec CODEC = GWT.create(TaggedDomUserCodec.class);
}
