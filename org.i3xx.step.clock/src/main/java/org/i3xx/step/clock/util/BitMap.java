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


import java.util.BitSet;

public interface BitMap {
	
	/**
	 * The length supports only cron job format
	 */
	public static int LENGTH_CRON_ONLY = 200;
	
	/**
	 * The length supports all features
	 */
	public static int LENGTH_ALL_FEATURES = 256;
	
	/**
	 * Returns the first free symbol mapping
	 * or creates one if not available.
	 * 
	 * @param symbol The symbol to map
	 * @return The index bound to the symbol
	 */
	int getSymbolMapping(String symbol);
	
	/**
	 * Removes a symbol mapping
	 * 
	 * @param symbol The symbol to remove
	 */
	void removeSymbolMapping(String symbol);
	
	/**
	 * Gets the symbol that is mapped to the specified key.
	 * 
	 * @param key whose associated value is to be returned
	 * @return the value to which this map maps the specified key
	 */
	String getSymbol(Integer key);
	
	/**
	 * Returns the BitSet that is mapped to the key.
	 * 
	 * @param key The key the BitSet is mapped to.
	 * @return
	 */
	BitSet getBitSet(Integer key);
	
	/**
	 * Gets the key that is mapped to the specified symbol.
	 * 
	 * @param symbol
	 * @return
	 */
	Integer getKey(String symbol);
	
	/**
	 * Returns true if a symbol mapping exists.
	 * 
	 * @param symbol The symbol to look for
	 * @return
	 */
	boolean exists(String symbol);
	
	/**
	 * Returns true if a BitSet mapping exists.
	 * 
	 * @param set
	 * @return
	 */
	boolean exists(BitSet set);
	
	/**
	 * Creates a BitSet to key mapping.
	 * 
	 * @param set The BitSet to set
	 * @param key The corresponding index of the mapping as key
	 */
	void setMapping(BitSet set, Integer key);
	
	/**
	 * Returns the keys that match the mapping. The match doesn't
	 * discern inner and outer join. A matching BitSet has every
	 * bit of the filter BitSet set. The filter is less or equal
	 * in cardinality than the matched BitSet.
	 * <p>
	 * Example:<p>
	 * <ul>
	 * <li>Filter: 0101, Data: 1111, Result: match
	 * <li>Filter: 0101, Data: 0111, Result: match
	 * <li>Filter: 0101, Data: 0101, Result: match
	 * <li>Filter: 0101, Data: 0100, Result: no match
	 * <li>Filter: 0101, Data: 0001, Result: no match
	 * </ul>
	 * @param filter The BitSet to filter
	 * @return The indexes of the mapping
	 */
	Integer[] getMappingFilter(BitSet filter);
	
	/**
	 * Returns the keys that match the mapping. The match doesn't
	 * discern inner and outer join. The filter BitSet has every
	 * bit of the matched BitSet set. The filter is greater or equal
	 * in cardinality than the matched BitSet.
	 * <p>
	 * Example:<p>
	 * <ul>
	 * <li>Filter: 0101, Data: 1111, Result: no match
	 * <li>Filter: 0101, Data: 0111, Result: no match
	 * <li>Filter: 0101, Data: 0101, Result: match
	 * <li>Filter: 0101, Data: 0100, Result: match
	 * <li>Filter: 0101, Data: 0001, Result: match
	 * </ul>
	 * @param filter The BitSet to filter
	 * @return The indexes of the mapping
	 */
	Integer[] getMappingIntersects(BitSet filter);
	
	/**
	 * Returns the keys that match the mapping. The match doesn't
	 * discern inner and outer join. The filter BitSet has every
	 * bit of the matched BitSet set. The filter is greater or equal
	 * in cardinality than the matched BitSet.
	 * 
	 * The match uses groups. The intersection belongs to the groups.
	 * Inside a group a filter mapping is used where one of the bits
	 * of the filter must be set.
	 * 
	 * Example for all groups (each bit is a group):
	 * 
	 * Filter: 0101, Data: 1111, Result: no match
	 * Filter: 0101, Data: 0111, Result: no match
	 * Filter: 0101, Data: 0101, Result: match
	 * Filter: 0101, Data: 0100, Result: match
	 * Filter: 0101, Data: 0001, Result: match
	 * 
	 * Example for using:
	 * 
	 * The crontab statement '/1 * * * *' matches every minute. To do
	 * this a BitSet with all minute-flags set is used. If the tick
	 * is exact per minute the second-flags are unset (Clock.java).
	 * If the tick is exact a second the second-flag '00' must be set.
	 * 
	 * @param filter The BitSet to filter
	 * @return The indexes of the mapping
	 */
	public Integer[] getGroupMappingIntersects(BitSet filter);
	
	/**
	 * Removes the mapping of a BitSet
	 * 
	 * @param set The BitSet to remove
	 * @param key The corresponding index of the mapping as key
	 * @return The indexes of the mapping
	 */
	void removeMapping(BitSet set, Integer key);
	
	/**
	 * Resets the content
	 */
	public void reset();
	
}
