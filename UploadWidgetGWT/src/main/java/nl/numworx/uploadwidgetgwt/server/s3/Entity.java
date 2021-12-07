package nl.numworx.uploadwidgetgwt.server.s3;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class Entity implements Closeable {

	private final ResponseInputStream<GetObjectResponse> entity;

	Entity(ResponseInputStream<GetObjectResponse> result) {
		this.entity = result;
	}
	
	public InputStream inputstream() {
		return entity;
	}
	
	String contentType() { 
		return entity.response().contentType();
	}
	long contentLength() {
		return entity.response().contentLength().longValue();
	}
	
	public void close() throws IOException {
		entity.close();
	}
}
