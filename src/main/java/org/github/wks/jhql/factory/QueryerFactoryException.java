package org.github.wks.jhql.factory;

public class QueryerFactoryException extends RuntimeException {

	private static final long serialVersionUID = 3153920744484987436L;

	public QueryerFactoryException() {
	}

	public QueryerFactoryException(String message) {
		super(message);
	}

	public QueryerFactoryException(Throwable cause) {
		super(cause);
	}

	public QueryerFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
