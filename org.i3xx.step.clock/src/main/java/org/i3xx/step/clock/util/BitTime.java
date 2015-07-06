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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The BitMap provides a 2 dimensional index of bits to sort and select values.
 * <p>
 * The bits have the following meaning.
 * <ul>
 * <li>  0 -  59   0-59 Seconds
 * <li> 60 - 119   0-59 Minutes
 * <li>120 - 143   0-23 Hours
 * <li>144 - 174   1-31 Days
 * <li>175 - 186   0-11 Month
 * <li>187 - Monday
 * <li>188 - Tuesday
 * <li>189 - Wednesday
 * <li>190 - Thursday
 * <li>191 - Friday
 * <li>192 - Saturday
 * <li>193 - Sunday
 * <li>194 - First
 * <li>195 - Ultimo
 * <li>196 - Once
 * <li>197 - Immediate (at boot time)
 * <li>200 - 255   2015-2070
 * </ul>
 * <p>
 * The fields matches the value of the gregorian calendar
 * set with a 'cal.setFirstDayOfWeek(Calendar.MONDAY);'
 * <ul>
 * <li>Calendar.SECOND
 * <li>Calendar.MINUTE
 * <li>Calendar.HOUR_OF_DAY
 * <li>Calendar.DAY_OF_MONTH
 * <li>Calendar.MONTH
 * <li>Calendar.YEAR
 * <li>Calendar.DAY_OF_WEEK
 * </ul>
 * 
 * @author Stefan
 *
 */
public class BitTime {
	
	/**
	 * The first year that is in the range of valid years
	 */
	public static final int FIRST_YEAR = 2015;
	
	/**
	 * The last year that is in the range of valid years
	 */
	public static final int LAST_YEAR = 2070;
	
	/**
	 * The day offset to match the field DAY_OF_MONTH of the Gregorian Calendar
	 */
	public static final int DAY_OFFSET = 1;
	
	/**
	 * The day offset to match the field DAY_OF_WEEK_OFFSET of the Gregorian Calendar
	 */
	public static final int DAY_OF_WEEK_OFFSET = 1;
	
	/** The time value*/
	private BitSet time;
	
	/**
	 * Creates a new BitTime object with the current time.
	 */
	public BitTime() {
		time = new BitSet();
	}
	
	/**
	 * Creates a new BitTime object with the date.
	 * 
	 * @param date The date of the BitTime
	 */
	public BitTime(Date date) {
		time = new BitSet();
		setTime(date);
	}
	
	/**
	 * Creates a new BitTime object with the date. The date uses the format
	 * described in SimpleDateFormat.
	 * 
	 * @param date The date as a String
	 * @param format The format of the String (@see SimpleDateFormat)
	 * @throws ParseException
	 */
	public BitTime(String date, String format) throws ParseException {
		time = new BitSet();
		setTime(date, format);
	}
	
	/**
	 * Creates a new BitTime object with the date. The date uses the same
	 * time format as a cron job <tt>(minute, hour, day, month, day-of-week)</tt>.
	 * 
	 * 'http://troubadix.dn.fh-koeln.de/unix/cronjobs_syntax.html' 
	 * 
	 * @param date The date as a String
	 */
	public BitTime(String date) {
		CrontabParser p = new CrontabParser();
		time = p.parseToBitTime(date).getTime();
	}
	
	
	/**
	 * @param year The value
	 * @param month The value
	 * @param day The value
	 * @param hour The value
	 * @param minute The value
	 * @param second The value
	 * @param dayOfTheWeek The value
	 */
	public BitTime(int year, int month, int day, int hour, int minute, int second, int dayOfTheWeek) {
		time = new BitSet();
		
		if(year>-1)
			setYear(year);
		if(month>-1)
			setMonth(month);
		if(day>-1)
			setDay(day);
		if(hour>-1)
			setHour(hour);
		if(minute>-1)
			setMinute(minute);
		if(second>-1)
			setSecond(second);
		if(dayOfTheWeek>-1)
			setDayOfTheWeek(dayOfTheWeek);
	}
	
	/**
	 * Gets the BitSet
	 * 
	 * @return The BitSet
	 */
	public BitSet getTime() {
		return time;
	}
	
	/**
	 * Gets the BitSet as a Date
	 * 
	 * @return The Date
	 */
	public Date getDate() {
		int second = getSecond();
		int minute = getMinute();
		int hour = getHour();
		int day = getDay();
		int month = getMonth();
		int year = getYear();
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.set(year, month, day, hour, minute, second);
		
		//skip millis
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * Sets the time and date
	 * 
	 * @param date
	 * @param format (dd.MM.yyyy HH:mm:ss)
	 * @throws ParseException
	 * 
	 * @see SimpleDateFormat
	 */
	void setTime(String date, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date d = sdf.parse(date);
		setTime(d);
	}
	
	/**
	 * Sets the bits using a Date object.
	 * The first day of the week is the sunday.
	 * 
	 * @param date The date to set the bits
	 */
	void setTime(Date date) {
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setTime(date);
		
		int second = cal.get(Calendar.SECOND);
		int minute = cal.get(Calendar.MINUTE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		
		setSecond(second);
		setMinute(minute);
		setHour(hour);
		setDay(day);
		setMonth(month);
		setYear(year);
		setDayOfTheWeek(dayOfWeek);
	}
	
	/**
	 * Clears the time
	 */
	public void clear() {
		time.clear();
	}
	
	/**
	 * Sets the bit of the second
	 * 
	 * @param val The second 0-59
	 */
	public void setSecond(int val) {
		if(val<0 || val>59)
			throw new IllegalArgumentException("The value "+val+" is no valid second (0-59).");
		
		time.set(val);
	}
	
	/**
	 * Gets the second
	 * 
	 * @return The value
	 */
	public int getSecond() {
		BitSet s = time.get(0, 60);
		return s.nextSetBit(0);
	}
	
	/**
	 * Sets the bit of the minute
	 * 
	 * @param val The minute 0-59
	 */
	public void setMinute(int val) {
		if(val<0 || val>59)
			throw new IllegalArgumentException("The value "+val+" is no valid minute (0-59).");
		
		time.set(60+val);
	}
	
	/**
	 * Gets the minute
	 * 
	 * @return The value
	 */
	public int getMinute() {
		BitSet s = time.get(60, 120);
		return s.nextSetBit(0);
	}
	
	/**
	 * Sets the bit of the hour
	 * 
	 * @param val The hour 0-23
	 */
	public void setHour(int val) {
		if(val<0 || val>23)
			throw new IllegalArgumentException("The value "+val+" is no valid hour (0-23).");
		
		time.set(120+val);
	}
	
	/**
	 * Gets the hour 0-23
	 * 
	 * @return The value
	 */
	public int getHour() {
		BitSet s = time.get(120, 144);
		return s.nextSetBit(0);
	}
	
	/**
	 * Sets the bit of the day. Note: The field is 1 less than the field
	 * day-of-month of the gregorian calendar.
	 * 
	 * @param val The day 1-31
	 */
	public void setDay(int val) {
		if(val<0 || val>31)
			throw new IllegalArgumentException("The value "+val+" is no valid day (0-30).");
		
		time.set(144+(val-DAY_OFFSET));
	}
	
	/**
	 * Gets the day 1-31
	 * 
	 * @return The value
	 */
	public int getDay() {
		BitSet s = time.get(144, 175);
		return s.nextSetBit(0)+DAY_OFFSET;
	}
	
	/**
	 * Sets the bit of the month
	 * 
	 * @param val The month 0-11
	 */
	public void setMonth(int val) {
		if(val<0 || val>11)
			throw new IllegalArgumentException("The value "+val+" is no valid month (0-11).");
		
		time.set(175+val);
	}
	
	/**
	 * Gets the month 0-11
	 * 
	 * @return The value
	 */
	public int getMonth() {
		BitSet s = time.get(175, 187);
		return s.nextSetBit(0);
	}
	
	/**
	 * Sets the bit of the day of the week
	 * 
	 * 1 - Sunday
	 * 2 - Monday
	 * 3 - Tuesday
	 * 4 - Wednesday
	 * 5 - Thursday
	 * 6 - Friday
	 * 7 - Saturday
	 * 
	 * @param val The day of the week 1-7
	 */
	public void setDayOfTheWeek(int val) {
		if(val<1 || val>7)
			throw new IllegalArgumentException("The value "+val+" is no valid day of the week (1-7).");
		
		time.set(187+(val-DAY_OF_WEEK_OFFSET));
	}
	
	/**
	 * Gets the day of the week
	 * 
	 * @return The value
	 */
	public int getDayOfTheWeek() {
		BitSet s = time.get(187, 194);
		return s.nextSetBit(0)+DAY_OF_WEEK_OFFSET;
	}
	
	/**
	 * Sets the bit of first
	 */
	public void setFirst(boolean flag) {
		time.set(194, flag);
	}
	
	/**
	 * Gets the bit of first
	 * @return The bit
	 */
	public boolean isFirst() {
		return time.get(194);
	}
	
	/**
	 * Sets the bit of ultimo
	 */
	public void setUltimo(boolean flag) {
		time.set(195, flag);
	}
	
	/**
	 * Gets the bit of ultimo
	 * @return The bit
	 */
	public boolean isUltimo() {
		return time.get(195);
	}
	
	/**
	 * Sets the bit of once
	 */
	public void setOnce(boolean flag) {
		time.set(196, flag);
	}
	
	/**
	 * Gets the bit of once
	 * @return The bit
	 */
	public boolean isOnce() {
		return time.get(196);
	}
	
	/**
	 * Sets the bit of immediate (boot time)
	 */
	public void setImmediate(boolean flag) {
		time.set(197, flag);
	}
	
	/**
	 * Gets the bit of immediate (boot time)
	 * @return The bit
	 */
	public boolean isImmediate() {
		return time.get(197);
	}
	
	/**
	 * Sets the bit of the year
	 * 
	 * @param val The year FIRST_YEAR-LAST_YEAR
	 */
	public void setYear(int val) {
		if(val<FIRST_YEAR || val>LAST_YEAR)
			throw new IllegalArgumentException("The value "+val+" is no valid year (2015-2070).");
		
		time.set(200+(val-FIRST_YEAR));
	}
	
	/**
	 * Gets the day of the week
	 * 
	 * @return The value
	 */
	public int getYear() {
		BitSet s = time.get(200, time.length()+1);
		return s.nextSetBit(0)+FIRST_YEAR;
	}

}
