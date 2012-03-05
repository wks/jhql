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
package org.github.wks.jhql;

public class HtmlException extends RuntimeException {

	private static final long serialVersionUID = 6030398222844546176L;

	public HtmlException() {
		super();
	}

	public HtmlException(String message, Throwable cause) {
		super(message, cause);
	}

	public HtmlException(String message) {
		super(message);
	}

	public HtmlException(Throwable cause) {
		super(cause);
	}

}
