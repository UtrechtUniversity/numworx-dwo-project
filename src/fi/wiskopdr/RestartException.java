package fi.wiskopdr;

public class RestartException extends Exception {

	/**
	 * dummy
	 */
	private static final long serialVersionUID = 1L;

	public interface RestartHandler {

		void restart(String message, Runnable run);

	}

	private RestartHandler handler;

	public RestartException(String message, RestartHandler handler) {
		super(message);
		this.handler = handler;
	}

	public void restart(Runnable run) {
		handler.restart(getMessage(), run);		
	}

}
