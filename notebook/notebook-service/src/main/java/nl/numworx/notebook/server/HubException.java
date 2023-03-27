package nl.numworx.notebook.server;

import java.io.IOException;

import com.owlike.genson.annotation.JsonCreator;
import com.owlike.genson.annotation.JsonProperty;

public class HubException extends IOException {

	public final int status;

	public HubException() {
		status = 0;
	}

	public HubException(String message) {
		super(message);
		status = 0;
	}

	public HubException(Throwable cause) {
		super(cause);
		status = 0;
	}

	public HubException(String message, Throwable cause) {
		super(message, cause);
		status = 0;
	}

	@JsonCreator
	public HubException(@JsonProperty("status") int status, @JsonProperty("message") String message) {
		super(message);
		this.status = status;
	}
}
