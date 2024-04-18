package nl.numworx.schoolyear.jclient;

import java.io.IOException;

public class SchoolyearException extends IOException {
	
	int code;
	String body;

	public SchoolyearException() {
	}

	public SchoolyearException(int code, String reason, String body) {
		super(reason);
		this.code = code;
		this.body = body;
	}

	public SchoolyearException(Throwable cause) {
		super(cause);
	}

	public SchoolyearException(int code, String reason, String body, Throwable cause) {
		super(reason, cause);
		this.code = code;
		this.body = body;
	}

	public String toString() {
		return super.toString() + ", code=" + code + ", body=" + body;
	}
}
