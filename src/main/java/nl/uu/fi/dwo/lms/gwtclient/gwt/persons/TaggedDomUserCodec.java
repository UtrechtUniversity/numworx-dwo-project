package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;


public interface TaggedDomUserCodec extends JsonEncoderDecoder<TaggedDomUser> {
	TaggedDomUserCodec CODEC = GWT.create(TaggedDomUserCodec.class);
}
