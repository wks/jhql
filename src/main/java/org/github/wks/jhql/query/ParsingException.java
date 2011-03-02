package org.github.wks.jhql.query;

public class ParsingException extends RuntimeException {
	private static final long serialVersionUID = -8466586888491230777L;

	public ParsingException() {
		super();
	}

	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(Throwable cause) {
		super(cause);
	}

}
