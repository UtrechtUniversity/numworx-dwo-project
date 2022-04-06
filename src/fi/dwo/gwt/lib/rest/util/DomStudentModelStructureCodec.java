package fi.dwo.gwt.lib.rest.util;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public interface DomStudentModelStructureCodec extends JsonEncoderDecoder<DomStudentModelStructure> {
	DomStudentModelStructureCodec CODEC = GWT.create(DomStudentModelStructureCodec.class);
	
	static String toString(DomStudentModelStructure structure) {
		return CODEC.encode(structure).toString();
	}
}
