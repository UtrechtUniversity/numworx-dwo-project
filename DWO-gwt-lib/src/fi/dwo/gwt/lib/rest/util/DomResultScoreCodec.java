package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;


public interface DomResultScoreCodec extends JsonEncoderDecoder<DomResultScore> {
	DomResultScoreCodec CODEC = GWT.create(DomResultScoreCodec.class);
}
