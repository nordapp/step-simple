package org.i3xx.step.clock.service.model;

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


import java.util.BitSet;

public interface ClockService {
	
	/**
	 * Adds a time mapping to the ClockService.
	 * 
	 * @param stmt The crontab time statement.
	 * @param symbol The symbol to use for the whiteboard pattern (must be unique).
	 * @throws IllegalArgumentException
	 * @see org.i3xx.step.clock.util.BitTime
	 */
	void addMapping(String stmt, String symbol) throws IllegalArgumentException;
	
	/**
	 * Adds a time mapping to the ClockService.
	 * 
	 * @param bitSet The time as a BitSet with time flags.
	 * @param symbol The symbol to use for the whiteboard pattern (must be unique).
	 * @throws IllegalArgumentException
	 * @see org.i3xx.step.clock.util.BitTime
	 */
	void addMapping(BitSet bitSet, String symbol) throws IllegalArgumentException;
	
	/**
	 * Removes the symbol from the mapping.
	 * 
	 * @param symbol The symbol to remove
	 */
	void removeMapping(String symbol);
	
	/**
	 * Tests the symbol and returns true if it is already set, false otherwise.
	 * 
	 * @param symbol The symbol to use for the whiteboard pattern (must be unique).
	 * @return
	 */
	boolean hasMapping(String symbol);
}