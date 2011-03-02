package org.github.wks.jhql.factory;

/**
 * Thrown if a JSON value is not a valid JHQL expression.
 */
public class JhqlJsonGrammarException extends RuntimeException {

	private static final long serialVersionUID = 136294107893625236L;

	public JhqlJsonGrammarException() {
		super();
	}

	public JhqlJsonGrammarException(String message, Throwable cause) {
		super(message, cause);
	}

	public JhqlJsonGrammarException(String message) {
		super(message);
	}

	public JhqlJsonGrammarException(Throwable cause) {
		super(cause);
	}

}
