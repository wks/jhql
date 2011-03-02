package org.github.wks.jhql;

public class JhqlException extends RuntimeException {

	private static final long serialVersionUID = 6030398222844546176L;

	public JhqlException() {
		super();
	}

	public JhqlException(String message, Throwable cause) {
		super(message, cause);
	}

	public JhqlException(String message) {
		super(message);
	}

	public JhqlException(Throwable cause) {
		super(cause);
	}

}
