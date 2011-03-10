package org.github.wks.jhql.factory;

/**
 * Thrown if a JSON value is not a valid JHQL expression.
 */
public class JhqlGrammarException extends RuntimeException {

	private static final long serialVersionUID = 136294107893625236L;

	public JhqlGrammarException() {
		super();
	}

	public JhqlGrammarException(String message, Throwable cause) {
		super(message, cause);
	}

	public JhqlGrammarException(String message) {
		super(message);
	}

	public JhqlGrammarException(Throwable cause) {
		super(cause);
	}

}
