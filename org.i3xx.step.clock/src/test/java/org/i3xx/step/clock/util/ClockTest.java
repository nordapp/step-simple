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
import java.util.Date;

import org.junit.Ignore;

public class ClockTest {

	/**
	 * TAKE A LOOK TO YOUR SYSTEM'S CLOCK BEFORE RUNNING THIS TEST
	 * maybe it is better to change the time to test ;-)
	 * 
	 * @throws InterruptedException
	 */
	@Ignore
	public void test() throws InterruptedException {
		
		final BitMap map = new BitMapImpl(BitMap.LENGTH_ALL_FEATURES);
		
		Integer keyA = map.getSymbolMapping("Symbol-A");
		Integer keyB = map.getSymbolMapping("Symbol-B");
		Integer keyC = map.getSymbolMapping("Symbol-C");
		Integer keyD = map.getSymbolMapping("Symbol-D");
		
		//Change the time before testing.
		map.setMapping(new BitTime("15 * * * *").getTime(), keyA);
		map.setMapping(new BitTime("16 * * * *").getTime(), keyB);
		map.setMapping(new BitTime("17 * * * *").getTime(), keyC);
		map.setMapping(new BitTime("18 * * * *").getTime(), keyD);
		
		Clock clock = new Clock(new TimeTick(){

			public void tick(Calendar calendar) {
				Date date = calendar.getTime();
				
				Integer[] keys = map.getMappingIntersects(new BitTime(date).getTime());
				for(int i=0;i<keys.length;i++) {
					
					System.out.println( map.getSymbol(keys[i]) );
				}
			}
		});
		
		
		Thread t = new Thread(clock);
		t.start();
		
		Thread.sleep(300*1000);
	}

}
