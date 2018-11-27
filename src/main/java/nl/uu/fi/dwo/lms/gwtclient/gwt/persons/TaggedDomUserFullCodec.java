package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;


public interface TaggedDomUserFullCodec extends JsonEncoderDecoder<TaggedDomUserFull> {
	TaggedDomUserFullCodec CODEC = GWT.create(TaggedDomUserFullCodec.class);
}
