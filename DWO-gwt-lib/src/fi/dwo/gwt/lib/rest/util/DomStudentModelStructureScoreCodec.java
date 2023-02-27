package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public interface DomStudentModelStructureScoreCodec extends JsonEncoderDecoder<DomStudentModelStructureScore> {
	DomStudentModelStructureScoreCodec CODEC = GWT.create(DomStudentModelStructureScoreCodec.class);
}
