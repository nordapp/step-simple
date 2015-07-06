package org.i3xx.step.clock.service.impl;

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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.i3xx.step.clock.service.model.ClockService;
import org.i3xx.step.clock.service.model.StoreService;
import org.i3xx.step.clock.util.BitMap;
import org.i3xx.step.clock.util.BitMapImpl;
import org.i3xx.step.clock.util.BitTime;
import org.i3xx.step.clock.util.Clock;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClockServiceImpl implements ClockService {
	
	static Logger logger = LoggerFactory.getLogger(ClockServiceImpl.class);
	
	static final int corePoolSize  =    5;
	static final int maxPoolSize   =   10;
	static final long keepAliveTime = 60000;	
	
	/**  */
	BundleContext bundleContext;
	
	/**  */
	StoreService clockStore;
	
	/**  */
	private ExecutorService executor;
	
	/**  */
	private Thread thread;
	
	/**  */
	private BlockingQueue<Runnable> queue;
	
	/**  */
	private Clock clock;
	
	/**  */
	private BitMap bitMap;
	
	public ClockServiceImpl() {
		
		bundleContext = null;
		clockStore = null;
		executor = null;
		thread = null;
		clock = null;
		
		queue = new LinkedBlockingQueue<Runnable>();
		bitMap = new BitMapImpl(BitMap.LENGTH_ALL_FEATURES);
		
	}
	
	/**
	 * Adds a time mapping to the ClockService.
	 * 
	 * @param stmt The crontab time statement.
	 * @param symbol The symbol to use for the whiteboard pattern (must be unique).
	 * @throws IllegalArgumentException
	 * @see org.i3xx.step.clock.util.BitTime
	 */
	public void addMapping(String stmt, String symbol) throws IllegalArgumentException {
		if(bitMap.exists(symbol))
			throw new IllegalArgumentException("The symbol is already used.");
		
		if(clockStore!=null) {
			clockStore.addEntry(stmt, symbol);
		}
		
		Integer key = bitMap.getSymbolMapping(symbol);
		bitMap.setMapping(new BitTime(stmt).getTime(), key);
		
		logger.trace("Add mapping crontab:{} symbol:{} key:{}", stmt, symbol, key);
	}
	
	/**
	 * Adds a time mapping to the ClockService.
	 * 
	 * @param bitSet The time as a BitSet with time flags.
	 * @param symbol The symbol to use for the whiteboard pattern (must be unique).
	 * @throws IllegalArgumentException
	 * @see org.i3xx.step.clock.util.BitTime
	 */
	public void addMapping(BitSet bitSet, String symbol) throws IllegalArgumentException {
		if(bitMap.exists(symbol))
			throw new IllegalArgumentException("The symbol is already used.");
		
		if(clockStore!=null) {
			clockStore.addEntry(bitSet, symbol);
		}
		
		Integer key = bitMap.getSymbolMapping(symbol);
		bitMap.setMapping(bitSet, key);
		
		if(logger.isTraceEnabled())
			logger.trace("Add mapping bitset:{} symbol:{} key:{}", bitSet.toString(), symbol, key);
	}
	
	/**
	 * Tests the symbol and returns true if it is already set, false otherwise.
	 * 
	 * @param symbol The symbol to use for the whiteboard pattern (must be unique).
	 * @return True if the bit relating to the symbol is set, false otherwise
	 */
	public boolean hasMapping(String symbol) {
		return bitMap.exists(symbol);
	}
	
	/**
	 * Removes the symbol from the mapping.
	 * 
	 * @param symbol The symbol to remove
	 */
	public void removeMapping(String symbol) {
		if( ! bitMap.exists(symbol))
			return;
		
		if(clockStore!=null) {
			clockStore.removeEntry(symbol);
		}
		
		Integer key = bitMap.getKey(symbol);
		BitSet set = bitMap.getBitSet(key);
		
		bitMap.removeMapping(set, key);
		bitMap.removeSymbolMapping(symbol);
		
		if(logger.isTraceEnabled())
			logger.trace("Remove mapping bitset:{} symbol:{} key:{}", set.toString(), symbol, key);
	}
	
	/**
	 * startUp the service (from blueprint)
	 */
	public void startUp() {
		
		executor = new ThreadPoolExecutor(
				corePoolSize, maxPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, queue);
		
		//TODO
		TimeTickImpl tick = new TimeTickImpl(bitMap, queue, executor);
		tick.setBundleContext(bundleContext);
		clock = new Clock(tick);
		
		thread = new Thread(clock);
		thread.start();
	}
	
	/**
	 * shutDown the service (from blueprint)
	 */
	public void shutDown() {
		clock.stop();
		clock = null;
		thread = null;
		
		executor.shutdown();
		executor = null;
		
		bitMap.reset();
	}

	/**
	 * @return the bundleContext
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * @param bundleContext the bundleContext to set
	 */
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * @return the clockStore
	 */
	public StoreService getClockStore() {
		return clockStore;
	}

	/**
	 * @param clockStore the clockStore to set
	 */
	public void setClockStore(StoreService clockStore) {
		this.clockStore = clockStore;
	}

}
