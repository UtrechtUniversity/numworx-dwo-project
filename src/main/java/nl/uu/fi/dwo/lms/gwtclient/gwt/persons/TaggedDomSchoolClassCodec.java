package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;


public interface TaggedDomSchoolClassCodec extends JsonEncoderDecoder<TaggedDomSchoolClass> {
	TaggedDomSchoolClassCodec CODEC = GWT.create(TaggedDomSchoolClassCodec.class);
}
