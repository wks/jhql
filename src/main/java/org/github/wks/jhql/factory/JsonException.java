package org.github.wks.jhql.factory;

/**
 * Thrown if there is an error while paring JSON.
 */
public class JsonException extends RuntimeException {

	private static final long serialVersionUID = 3153920744484987436L;

	public JsonException() {
	}

	public JsonException(String message) {
		super(message);
	}

	public JsonException(Throwable cause) {
		super(cause);
	}

	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}

}
