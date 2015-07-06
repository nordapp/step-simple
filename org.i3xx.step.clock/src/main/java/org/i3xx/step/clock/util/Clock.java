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


import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clock implements Runnable {
	
	static Logger logger = LoggerFactory.getLogger(Clock.class);
	
	/**  */
	private boolean _cont;
	
	/**  */
	private GregorianCalendar calendar;
	
	/**  */
	private TimeTick timeTick;
	
	/**
	 * @param tick
	 */
	public Clock(TimeTick tick) {
		this._cont = true;
		
		this.calendar = new GregorianCalendar();
		this.calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		this.timeTick = tick;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		int minute = 0;
		//active wait for one s
		while(_cont) {
			try{
				calendar.setTimeInMillis(System.currentTimeMillis());
				int min = calendar.get(Calendar.MINUTE);
				logger.trace("Clock tests minute:{}, current:{}", minute, min);
				
				if(minute != min){
					logger.debug("Clock ticks minute:{}, current:{}", minute, min);
					minute = min;
					
					timeTick.tick(calendar);
				}
			}catch(Exception e){
				e.printStackTrace();
				logger.error("The clock throws an exception.", e);
			}
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){}
		}//while
	}
	
	public void stop() {
		_cont = false;
	}

}
