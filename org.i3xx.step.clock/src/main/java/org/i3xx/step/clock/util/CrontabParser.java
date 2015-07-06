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


import java.util.Arrays;

public class CrontabParser {
	
	/**
	 * The day offset to match the field DAY_OF_WEEK_OFFSET to the Gregorian Calendar
	 */
	public static final int DAY_OF_WEEK_OFFSET = 1;
	
	/**
	 * The day offset to match the field MONTH_OFFSET to the Gregorian Calendar
	 */
	public static final int MONTH_OFFSET = 1;
	
	/**
	 * Parses a statement of the CronTab-Syntax
	 * 
	 * 
	 * 'http://troubadix.dn.fh-koeln.de/unix/cronjobs_syntax.html'
	 */
	public CrontabParser() {
		
	}
	
	/**
	 * Parses a CronTab statement '/15' '5' '*' '3-5' '~' and creates
	 * a BitTime object. The fields are; minute, hour, day, month, day-of-week
	 * 
	 * minute      ::= 0-59
	 * hour        ::= 0-23
	 * day         ::= 1-31
	 * month       ::= 1-12
	 * day-of-week ::= 0-7  (0 and 7 means Sunday)
	 * 
	 * @param stmt The statement to parse 
	 * @return The BitTime object
	 */
	public BitTime parseToBitTime(String stmt) {
		
		BitTime bitTime = new BitTime();
		
		//
		// process the aliases
		//
		if(stmt.startsWith("@")) {
			if(stmt.equalsIgnoreCase("@reboot")){
				stmt = "* * * * *";
				
				bitTime.setImmediate(true);
			}else if(stmt.equalsIgnoreCase("@hourly")){
				stmt = "~ 0 * * * *";
			}else if(stmt.equalsIgnoreCase("@daily")){
				stmt = "~ 0 0 * * *";
			}else if(stmt.equalsIgnoreCase("@weekly")){
				stmt = "~ 0 0 * * 0";
			}else if(stmt.equalsIgnoreCase("@monthly")){
				stmt = "~ 0 0 1 * *";
			}else if(stmt.equalsIgnoreCase("@yearly")){
				stmt = "~ 0 0 1 1 *";
			}
		}//fi
		
		String[] args = stmt.split("\\s");
		
		//
		// remove the '~' prefix and sets the once flag.
		//
		if(args[0].equals("~")){
			String[] tt = new String[5];
			System.arraycopy(args, 1, tt, 0, 5);
			args = tt;
			
			bitTime.setOnce(true);
		}//fi
		
		//
		// process the fields
		//
		
		//minutes
		for(int val : parseValue(args[0], 60)) {
			bitTime.setMinute(val);
		}//for
		
		//hours
		for(int val : parseValue(args[1], 24)) {
			bitTime.setHour(val);
		}//for
		
		//days
		for(int val : parseValue(args[2], 31)) {
			bitTime.setDay(val);
		}//for
		
		//month
		for(int val : parseValue(args[3], 12)) {
			//Note: the GregorianCalendar uses int 0-11 to identify the month
			bitTime.setMonth(val-MONTH_OFFSET);
		}//for
		
		//days-of-week
		for(int val : parseValue(args[4], 8)) {
			//note contab: day 0 and day 7 are sundays
			val = val==7 ? 0 : val;
			//Note: the GregorianCalendar uses int 1-7 to identify the days
			bitTime.setDayOfTheWeek(val+DAY_OF_WEEK_OFFSET);
		}//for
		
		return bitTime;
	}
	
	/**
	 * Parses the value '/15' '5' '*' '3-5'
	 * 
	 * Note: The prefix '~' is not allowed in the input statement.
	 * 
	 * @param stmt The statement to parse
	 * @param distance The distance (60: minute, 24:hour | 31: day | 12: month | 7: days-of-week)
	 * @return The values
	 */
	public int[] parseValue(String stmt, int distance) {
		
		//not set
		if(stmt.equals("*"))
			return new int[0];
		
		int[] ii = new int[distance];
		int c = 0;
		
		//comma separated list
		if(stmt.indexOf(",")>-1) {
			String[] args = stmt.split(",");
			for(int i=0;i<args.length;i++){
				int[] rr = parseValue(args[i], distance);
				System.arraycopy(rr, 0, ii, c, rr.length);
				c+=rr.length;
			}
		}
		
		//period /15
		else if(stmt.startsWith("/")) {
			int n = toInteger( stmt.substring(1) );
			for(int i=0;i<distance;i=i+n) {
				ii[c] = i;
				c++;
			}//for
		}
		
		//distance 3-5 (3 inclusive, 5 inclusive)
		else if(stmt.indexOf("-")>-1) {
			int p = stmt.indexOf('-');
			int a = toInteger(stmt.substring(0,p));
			int b = toInteger(stmt.substring(p+1));
			
			for(int i=a;i<=b;i++) {
				ii[c] = i;
				c++;
			}
		}
		
		//number
		else{
			ii[c] = toInteger(stmt);
			c++;
		}//fi
		
		//build the temp array
		int[] tt = new int[c];
		Arrays.fill(tt, -1);
		
		//drop doublets
		int n = 0;
		for(int i=0;i<c;i++) {
			if( Arrays.binarySearch(tt, ii[i])<0 ){
				tt[n] = ii[i];
				n++;
			}
		}//for
		
		//resize the result
		ii = new int[n];
		System.arraycopy(tt, 0, ii, 0, n);

		//sort it
		Arrays.sort(ii);
		
		return ii;
	}
	
	/**
	 * Parses the statement to an int
	 * 
	 * @param stmt The statement to parse
	 * @return The integer
	 */
	private int toInteger(String stmt) {
		return Integer.parseInt(stmt);
	}
	
	/**
	 * @param ii The result array
	 * @return The array
	 */
	public String print(int[] ii) {
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0;i<ii.length;i++) {
			buffer.append(ii[i]);
			buffer.append(' ');
		}
		if(buffer.length()>0)
			buffer.setLength(buffer.length()-1);
		
		return buffer.toString();
	}
	
	/**
	 * @param stmt The statement
	 * @return The String
	 */
	public String print(String stmt) {
		String[] args = stmt.split("\\s");
		
		StringBuffer buffer = new StringBuffer();
		
		if(args.length>0){
			buffer.append("minutes: ");
			buffer.append(args[0]);
			buffer.append(", ");
		}
		if(args.length>1){
			buffer.append("hours: ");
			buffer.append(args[1]);
			buffer.append(", ");
		}
		if(args.length>2){
			buffer.append("days: ");
			buffer.append(args[2]);
			buffer.append(", ");
		}
		if(args.length>3){
			buffer.append("month: ");
			buffer.append(args[3]);
			buffer.append(", ");
		}
		if(args.length>4){
			buffer.append("days-of-week: ");
			buffer.append(args[4]);
		}
		
		return buffer.toString();
	}

}
