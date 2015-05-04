package org.i3xx.step.clock.util;

/*
 * #%L
 * NordApp OfficeBase :: clock
 * %%
 * Copyright (C) 2014 - 2015 I.D.S. DialogSysteme GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import static org.junit.Assert.*;

import org.i3xx.step.clock.util.CrontabParser;
import org.junit.Test;

public class CrontabParserTest {

	@Test
	public void testA() {
		
		CrontabParser p = new CrontabParser();
		
		assertEquals("", p.print( p.parseValue("*", 60) ));
		assertEquals("0 15 30 45", p.print( p.parseValue("/15", 60) ));
		assertEquals("0 15 30 45", p.print( p.parseValue("0,15,30,45", 60) ));
		assertEquals("0 15 22 30 44 45", p.print( p.parseValue("/15,/22", 60) ));
		assertEquals("3 4 5 22 23 24", p.print( p.parseValue("3-5,22-24", 60) ));
		
		assertEquals("3 4 5 22 23 24", p.print( p.parseValue("3-5,22-24", 60) ));
		
		
		System.out.println( p.print( p.parseValue("0,15,30,45", 60) ) );

		System.out.println( p.print("* * * * *") );
	}

	@Test
	public void testB() {
		
		CrontabParser p = new CrontabParser();
		
		System.out.println( p.parseToBitTime("3-5,22-24 * * * *").getTime().toString() );
	}

}
