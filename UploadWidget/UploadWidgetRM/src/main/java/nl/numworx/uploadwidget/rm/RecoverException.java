package nl.numworx.uploadwidget.rm;

import org.cbook.cbookif.rm.ResourceException;

class RecoverException extends ResourceException {
	
	final private int code;

	RecoverException(int code) {
		this.code = code;
	}

	RecoverException(int code, String message) {
		super(message);
		this.code = code;
	}

	RecoverException(int code, String message, Throwable t) {
		super(message, t);
		this.code = code;
	}

	int getCode() {
		return code;
	}
	
	

}
