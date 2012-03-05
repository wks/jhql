/*
 *   Copyright 2011,2012 Kunshan Wang
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
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
