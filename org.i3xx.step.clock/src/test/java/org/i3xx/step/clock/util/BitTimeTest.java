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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.i3xx.step.clock.util.BitTime;
import org.junit.Test;

public class BitTimeTest {

	@Test
	public void testA() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date date = sdf.parse("28.01.2015 12:37:05");
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setTime(date);
		
		int second = cal.get(Calendar.SECOND);
		int minute = cal.get(Calendar.MINUTE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_MONTH)-1;
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		
		assertEquals(5, second);
		assertEquals(37, minute);
		assertEquals(12, hour);
		assertEquals(27, day);
		assertEquals(0, month);
		assertEquals(2015, year);
		assertEquals(4, dayOfWeek);
	}

	@Test
	public void testB() throws ParseException {
		BitTime time = new BitTime();
		
		//second 0-59
		for(int i=0;i<60;i++) {
			time.setSecond(i);
			assertEquals("second:", i, time.getSecond());
			time.clear();
		}//for
		
		//minute 0-59
		for(int i=0;i<60;i++) {
			time.setMinute(i);
			assertEquals("minute:", i, time.getMinute());
			time.clear();
		}//for
		
		//hour 0-23
		for(int i=0;i<24;i++) {
			time.setHour(i);
			assertEquals("hour:", i, time.getHour());
			time.clear();
		}//for
		
		//day 1-31
		for(int i=1;i<32;i++) {
			time.setDay(i);
			assertEquals("day:", i, time.getDay());
			time.clear();
		}//for
		
		//month 0-11
		for(int i=0;i<12;i++) {
			time.setMonth(i);
			assertEquals("month:", i, time.getMonth());
			time.clear();
		}//for
		
		//day-of-week 1-7
		for(int i=1;i<8;i++) {
			time.setDayOfTheWeek(i);
			assertEquals("day-of-week:", i, time.getDayOfTheWeek());
			time.clear();
		}//for
		
		time.setUltimo(true);
		assertTrue( time.isUltimo() );
		time.clear();
		
		time.setFirst(true);
		assertTrue( time.isFirst() );
		time.clear();
		
		//year 2015-2069
		for(int i=2015;i<2070;i++) {
			time.setYear(i);
			assertEquals("year:", i, time.getYear());
			time.clear();
		}//for
		
		Date date = new Date();
		time.setTime(date);
		assertEquals(getTime(date), time.getDate().getTime());
		time.clear();
	}
	
	/**
	 * Skip millis
	 * 
	 * @param date
	 * @return
	 */
	private long getTime(Date date) {
		long l = date.getTime();
		return (l/1000)*1000;
	}

}
