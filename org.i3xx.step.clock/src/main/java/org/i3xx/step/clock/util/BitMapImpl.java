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


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The mapping needs an unique key for each BitSet. As a result the
 * symbols are also unique.
 * 
 * @author Stefan
 *
 */
public class BitMapImpl implements BitMap {
	
	Map<Integer, BitSet> bitsets;
	Map<Integer, String> symbols;
	Map<String, Integer> reverse;
	List<List<Integer>> mapping;
	List<Integer> free;
	
	
	/**
	 * Creates a BitMap
	 * 
	 * @param width The width of the map (default 256)
	 */
	public BitMapImpl(int width) {
		bitsets = new HashMap<Integer, BitSet>();
		symbols = new HashMap<Integer, String>();
		reverse = new HashMap<String, Integer>();
		mapping = new ArrayList<List<Integer>>();
		free = new ArrayList<Integer>();
		
		//create the columns
		for(int i=0;i<width;i++) {
			mapping.add(new ArrayList<Integer>());
		}
	}
	
	/**
	 * Returns the first free symbol mapping
	 * 
	 * @param symbol The symbol to map
	 * @return The index bound to the symbol
	 */
	public int getSymbolMapping(String symbol) {
		
		Integer i = reverse.get(symbol);
		if(i==null){
			if(free.isEmpty()){
				i = new Integer(symbols.size());
			}else{
				i = free.remove(0);
			}//fi
			
			symbols.put(i, symbol);
			reverse.put(symbol, i);
		}//fi
		
		return i.intValue();
	}
	
	/**
	 * Removes a symbol mapping
	 * 
	 * @param symbol The symbol to remove
	 */
	public void removeSymbolMapping(String symbol) {
		//The mapping doesn't exists
		if( ! reverse.containsKey(symbol))
			return;
		
		Integer i = reverse.remove(symbol);
		symbols.remove(i);
		free.add(i);
	}
	
	/**
	 * Gets the symbol that is mapped to the specified key.
	 * 
	 * @param key whose associated value is to be returned
	 * @return the value to which this map maps the specified key
	 */
	public String getSymbol(Integer key) {
		return symbols.get(key);
	}
	
	/**
	 * Gets the key that is mapped to the specified symbol.
	 * 
	 * @param symbol The symbol the key is mapped to.
	 * @return The key
	 */
	public Integer getKey(String symbol) {
		return reverse.get(symbol);
	}
	
	/**
	 * Returns the BitSet that is mapped to the key.
	 * 
	 * @param key The key the BitSet is mapped to.
	 * @return The BitSet
	 */
	public BitSet getBitSet(Integer key) {
		return bitsets.get(key);
	}
	
	/**
	 * Returns true if a symbol mapping exists.
	 * 
	 * @param symbol The symbol to look for
	 * @return True if the mapping exists, false otherwise
	 */
	public boolean exists(String symbol) {
		return reverse.containsKey(symbol);
	}
	
	/**
	 * Returns true if a BitSet mapping exists.
	 * 
	 * @param set The BitSet
	 * @return True if the mapping exists, false otherwise
	 */
	public boolean exists(BitSet set) {
		return bitsets.containsValue(set);
	}
	
	/**
	 * Creates a BitSet to key mapping.
	 * 
	 * @param set The BitSet to set
	 * @param key The corresponding index of the mapping as key (always unique)
	 */
	public void setMapping(BitSet set, Integer key) {
		for(int i=0;;i++) {
			i = set.nextSetBit(i);
			if(i>-1){
				List<Integer> list = mapping.get(i);
				int p = Collections.binarySearch(list, key);
				//
				// p >= 0 should never occurs because the key is unique.
				// otherwise remove won't work.
				//
				if(p < 0){
					int k = p>-1 ? p : (-p)-1;
					list.add(k, key);
				}//fi
			}else{
				break;
			}//fi
		}//for
		bitsets.put(key, set);
	}
	
	/**
	 * Returns the keys that match the mapping. The match doesn't
	 * discern inner and outer join. A matching BitSet has every
	 * bit of the filter BitSet set. The filter is less or equal
	 * in cardinality than the matched BitSet.
	 * 
	 * Example:
	 * 
	 * Filter: 0101, Data: 1111, Result: match
	 * Filter: 0101, Data: 0111, Result: match
	 * Filter: 0101, Data: 0101, Result: match
	 * Filter: 0101, Data: 0100, Result: no match
	 * Filter: 0101, Data: 0001, Result: no match
	 * 
	 * @param filter The BitSet to filter
	 * @return The indexes of the mapping
	 */
	public Integer[] getMappingFilter(BitSet filter) {
		
		List<List<Integer>> lists = new ArrayList<List<Integer>>();
		
		//search all matching lists
		for(int i=0;;i++) {
			i = filter.nextSetBit(i);
			if(i>-1){
				List<Integer> list = mapping.get(i);
				lists.add(list);
			}else{
				break;
			}//fi
		}//for
		
		//no matching result available
		if(lists.isEmpty())
			return new Integer[0];
		
		//sort'em in ascending order
		Collections.sort(lists, new Comparator<List<Integer>>(){
			public int compare(List<Integer> a, List<Integer> b) {
				return a.size() > b.size() ? 1 : a.size() < b.size() ? -1 : 0;
			}}
		);
		
		List<Integer> ref = new ArrayList<Integer>(lists.get(0));//lists.get(0);
		
		//the ref list is the result
		if(lists.size()==1)
			return ref.toArray(new Integer[ref.size()]);
		
		//and operation
		for(int i=1;i<lists.size()&&ref.size()>0;i++) {
			List<Integer> list = lists.get(i);
			for(int k=0;k<ref.size();k++){
				Integer key = ref.get(k);
				int p = Collections.binarySearch(list, key);
				if(p<0){
					ref.remove(k);
					k--;
				}//fi
			}//for
		}//for
		
		return ref.toArray(new Integer[ref.size()]);
	}
	
	/**
	 * Returns the keys that match the mapping. The match doesn't
	 * discern inner and outer join. The filter BitSet has every
	 * bit of the matched BitSet set. The filter is greater or equal
	 * in cardinality than the matched BitSet.
	 * 
	 * Example:
	 * 
	 * Filter: 0101, Data: 1111, Result: no match
	 * Filter: 0101, Data: 0111, Result: no match
	 * Filter: 0101, Data: 0101, Result: match
	 * Filter: 0101, Data: 0100, Result: match
	 * Filter: 0101, Data: 0001, Result: match
	 * 
	 * @param filter The BitSet to filter
	 * @return The indexes of the mapping
	 */
	public Integer[] getMappingIntersects(BitSet filter) {
		
		Set<Integer> keys = new HashSet<Integer>();
		
		//search all matching lists; or operation
		for(int i=0;;i++) {
			i = filter.nextSetBit(i);
			if(i>-1){
				List<Integer> list = mapping.get(i);
				keys.addAll(list);
			}else{
				break;
			}//fi
		}//for
		
		List<Integer> ref = new ArrayList<Integer>();
		
		for(Integer key : keys) {
			BitSet set = (BitSet)bitsets.get(key).clone();
			int ca = set.cardinality();
			set.and(filter);
			if(set.cardinality()==ca)
				ref.add(key);
		}//for
		
		return ref.toArray(new Integer[ref.size()]);
	}
	
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
	public Integer[] getGroupMappingIntersects(BitSet filter) {
		
		//groups
		//type         position  name
		
		//second:        0- 59   1
		//minute:       60-119   2
		//hour:        120-143   3
		//day:         144-174   4
		//month:       175-186   5
		//year:        200-255   6
		//day-of-week: 187-193   7
		
		Set<Integer> keys = new HashSet<Integer>();
		
		//search all matching lists; or operation
		for(int i=0;;i++) {
			i = filter.nextSetBit(i);
			if(i>-1){
				List<Integer> list = mapping.get(i);
				keys.addAll(list);
			}else{
				break;
			}//fi
		}//for
		
		List<Integer> ref = new ArrayList<Integer>();
		
		for(Integer key : keys) {
			BitSet set = bitsets.get(key);
			if( groupMatch(filter, set) )
				ref.add(key);
		}//for
		
		return ref.toArray(new Integer[ref.size()]);
	}
	
	/**
	 * Each bit index in a group can occur once in the filter.
	 * If a group is set in the filter one corresponding
	 * bit of the group must be set in the value set.
	 * 
	 * Only crontab compatible flags are supported.
	 * 
	 * @param set The BitSet to be matched
	 * @param filter The filter BitSet
	 * @return The matching value
	 */
	private boolean groupMatch(BitSet set, BitSet filter) {
		
		boolean group = false;
		int[] fltCnt = new int[8];
		int[] setCnt = new int[8];
		int name = 0;
		@SuppressWarnings("unused")
		int start = 0, end = 0;
		
		//search all matching lists; or operation
		for(int i=0;;i++) {
			i = filter.nextSetBit(i);
			if(i>-1){
				
				if(i>=0 && i<60){
					group = true; name = 1; //second
					start = 0; end = 59;
				}else if(i>=60 && i<120){
					group = true; name = 2; //minute
					start = 60; end = 119;
				}else if(i>=120 && i<144){
					group = true; name = 3; //hour
					start = 120; end = 143;
				}else if(i>=144 && i<175){
					group = true; name = 4; //day
					start = 144; end = 174;
				}else if(i>=175 && i<187){
					group = true; name = 5; //month
					start = 175; end = 186;
				}else if(i>=200 && i<256){
					group = true; name = 6; //year
					start = 200; end = 255;
				}else if(i>=187 && i<194){
					group = true; name = 7; //day-of-the-week
					start = 187; end = 193;
				}else{
					group = false; name = 0;
					start = 0; end = 0;
				}//fi
				
				if(group) {
					fltCnt[name]++;
					//the matcher
					if(set.get(i)) {
						setCnt[name]++;
						//skip searching if found
						if(i<end)
							i = end;
					}//fi
				}//fi
				
			}else{
				break;
			}//fi
		}//for
		
		if( fltCnt[1]>0 && setCnt[1]==0 )
			return false;
		if( fltCnt[2]>0 && setCnt[2]==0 )
			return false;
		if( fltCnt[3]>0 && setCnt[3]==0 )
			return false;
		if( fltCnt[4]>0 && setCnt[4]==0 )
			return false;
		if( fltCnt[5]>0 && setCnt[5]==0 )
			return false;
		if( fltCnt[6]>0 && setCnt[6]==0 )
			return false;
		if( fltCnt[7]>0 && setCnt[7]==0 )
			return false;
		
		return true;
	}
	
	/**
	 * Removes the mapping of a BitSet
	 * 
	 * @param set The BitSet to remove
	 * @param key The corresponding index of the mapping as key
	 */
	public void removeMapping(BitSet set, Integer key) {
		
		List<List<Integer>> lists = new ArrayList<List<Integer>>();
		
		//search all matching lists
		for(int i=0;;i++) {
			i = set.nextSetBit(i);
			if(i>-1){
				List<Integer> list = mapping.get(i);
				lists.add(list);
			}else{
				break;
			}//fi
		}//for
		
		//no matching result available
		if(lists.isEmpty())
			return;
		
		//sort'em in ascending order
		Collections.sort(lists, new Comparator<List<Integer>>(){
			public int compare(List<Integer> a, List<Integer> b) {
				return a.size() > b.size() ? 1 : a.size() < b.size() ? -1 : 0;
			}}
		);
		
		//
		// The key is unique. This is the reason while it
		// is possible to remove a key from the mapping.
		// If you want to use a mapping twice you need two
		// unique keys.
		//
		
		//remove the key from the list
		for(int i=1;i<lists.size();i++) {
			List<Integer> list = lists.get(i);
			int p = Collections.binarySearch(list, key);
			if(p>-1){
				list.remove(p);
			}//fi
		}//for
		
		bitsets.remove(key);
	}
	
	/**
	 * Resets the content
	 */
	public void reset() {
		bitsets.clear();
		symbols.clear();
		reverse.clear();
		free.clear();
		
		//clears the columns
		for(int i=0;i<mapping.size();i++) {
			mapping.get(i).clear();
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0;i<mapping.size();i++) {
			if(mapping.get(i).isEmpty())
				continue;
			
			buffer.append("=============== (");
			buffer.append(i);
			buffer.append(") ===============");
			buffer.append("\n");
			
			for(Integer key : mapping.get(i)){
				buffer.append("   ");
				buffer.append(key.toString());
				buffer.append(": ");
				buffer.append(symbols.get(key));
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}

}
