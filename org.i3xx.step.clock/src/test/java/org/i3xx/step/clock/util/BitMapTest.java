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

import java.text.ParseException;

import org.i3xx.step.clock.util.BitMapImpl;
import org.i3xx.step.clock.util.BitTime;
import org.junit.Test;

public class BitMapTest {

	@Test
	public void testA() throws ParseException {
		
		String format = "dd.MM.yyyy HH:mm:ss";
		BitMapImpl map = new BitMapImpl(256);
		
		Integer intA = map.getSymbolMapping("Symbol-A");
		Integer intB = map.getSymbolMapping("Symbol-B");
		Integer intC = map.getSymbolMapping("Symbol-C");
		Integer intD = map.getSymbolMapping("Symbol-D");
		Integer intE = map.getSymbolMapping("Symbol-E");
		Integer intF = map.getSymbolMapping("Symbol-F");
		Integer intG = map.getSymbolMapping("Symbol-G");
		Integer intH = map.getSymbolMapping("Symbol-H");
		Integer intJ = map.getSymbolMapping("Symbol-J");
		
		map.setMapping( new BitTime("18.02.2015 14:32:00", format).getTime(), intA);
		map.setMapping( new BitTime("18.02.2015 19:52:52", format).getTime(), intB);
		map.setMapping( new BitTime("18.02.2015 21:16:38", format).getTime(), intC);
		map.setMapping( new BitTime("19.02.2015 19:52:52", format).getTime(), intD);
		map.setMapping( new BitTime("19.02.2015 08:32:12", format).getTime(), intE);
		map.setMapping( new BitTime("20.02.2015 14:56:58", format).getTime(), intF);
		map.setMapping( new BitTime("21.02.2015 19:52:00", format).getTime(), intG);
		map.setMapping( new BitTime("23.02.2015 19:52:52", format).getTime(), intH);
		map.setMapping( new BitTime(-1,1,19,-1,-1,-1,-1).getTime(), intJ);
		
		assertEquals( map.getMappingFilter(new BitTime("18.02.2015 19:52:52", format).getTime()).length, 1);
		assertEquals( map.getMappingIntersects(new BitTime("18.02.2015 19:52:52", format).getTime()).length, 1);
		
		assertEquals( map.getMappingFilter(new BitTime(-1,1,-1,-1,-1,-1,-1).getTime()).length, 9);
		assertEquals( map.getMappingFilter(new BitTime(-1,1,19,-1,-1,-1,-1).getTime()).length, 3);
		
		assertEquals( map.getMappingIntersects(new BitTime("19.02.2015 19:52:52", format).getTime()).length, 2);
		
		//System.out.println( map.toString() );
	}

	@Test
	public void testB() throws ParseException {
		
		//
		// A bitmap of the size 200 uses the syntax of a CronTab only
		// and cannot process years.
		//
		BitMapImpl map = new BitMapImpl(200);
		
		Integer intA = map.getSymbolMapping("Symbol-A");
		Integer intB = map.getSymbolMapping("Symbol-B");
		Integer intC = map.getSymbolMapping("Symbol-C");
		Integer intD = map.getSymbolMapping("Symbol-D");
		Integer intE = map.getSymbolMapping("Symbol-E");
		Integer intF = map.getSymbolMapping("Symbol-F");
		
		map.setMapping( new BitTime("@reboot").getTime(), intA);
		map.setMapping( new BitTime("@hourly").getTime(), intB);
		map.setMapping( new BitTime("@daily").getTime(), intC);
		map.setMapping( new BitTime("@weekly").getTime(), intD);
		map.setMapping( new BitTime("@monthly").getTime(), intE);
		map.setMapping( new BitTime("@yearly").getTime(), intF);
		
		//Test reboot
		assertEquals( map.getMappingFilter(new BitTime("@reboot").getTime()).length, 1);
		
		//Test hourly
		//System.out.println( map.getMappingFilter(new BitTime("* 0 * * *").getTime()) );
		assertEquals( map.getMappingFilter(new BitTime("* 0 * * *").getTime()).length, 4); //daily, weekly, monthly, yearly
		assertEquals( map.getMappingFilter(new BitTime("0 * * * *").getTime()).length, 5); //hourly, daily, weekly, monthly, yearly
	}

}
