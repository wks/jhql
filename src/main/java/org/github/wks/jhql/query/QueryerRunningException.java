package org.github.wks.jhql.query;

public class QueryerRunningException extends RuntimeException {

	private static final long serialVersionUID = 6654598832482505765L;

	public QueryerRunningException() {
	}

	public QueryerRunningException(String message) {
		super(message);
	}

	public QueryerRunningException(Throwable cause) {
		super(cause);
	}

	public QueryerRunningException(String message, Throwable cause) {
		super(message, cause);
	}

}
