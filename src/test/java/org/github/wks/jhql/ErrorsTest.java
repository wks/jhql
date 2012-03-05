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

import org.github.wks.jhql.factory.JhqlGrammarException;
import org.github.wks.jhql.query.Queryer;
import org.junit.Test;

/**
 * Testing how this program handles erroneous jhql expressions;
 */
public class ErrorsTest {
	private Jhql jhql = new Jhql();

	private Queryer loadQueryer(String filename) {
		try {
			return jhql.makeQueryer(ErrorsTest.class
					.getResourceAsStream(filename));
		} catch (JhqlGrammarException e) {
			System.out.println("Error for " + filename + ": " + e.getMessage());
			
			Throwable cause = e.getCause();
			while (cause != null) {
				System.out.println("        >> caused by: "
						+ cause.getClass().getName() + ": "
						+ cause.getMessage());
				cause = cause.getCause();
			}
			throw e;
		}
	}

	@Test(expected = JhqlGrammarException.class)
	public void badexpression() {
		loadQueryer("error-badexpression.jhql");
	}

	@Test(expected = JhqlGrammarException.class)
	public void badproperty() {
		loadQueryer("error-badproperty.jhql");
	}

	@Test(expected = JhqlGrammarException.class)
	public void badproperty2() {
		loadQueryer("error-badproperty2.jhql");
	}

	@Test(expected = JhqlGrammarException.class)
	public void badproperty3() {
		loadQueryer("error-badproperty3.jhql");
	}

	@Test(expected = JhqlGrammarException.class)
	public void badstringexpression() {
		loadQueryer("error-badstringexpression.jhql");
	}

	@Test(expected = JhqlGrammarException.class)
	public void badtype() {
		loadQueryer("error-badtype.jhql");
	}
	@Test(expected = JhqlGrammarException.class)
	public void badxpath() {
		loadQueryer("error-badxpath.jhql");
	}

	@Test(expected = JhqlGrammarException.class)
	public void missingrequired() {
		loadQueryer("error-missingrequired.jhql");
	}

	@Test(expected = JhqlGrammarException.class)
	public void unknowntype() {
		loadQueryer("error-unknowntype.jhql");
	}

	@Test()
	public void normal() {
		loadQueryer("normal.jhql");
	}
}
