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


import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.i3xx.step.clock.service.model.Notify;
import org.i3xx.step.clock.util.BitMap;
import org.i3xx.step.clock.util.BitTime;
import org.i3xx.step.clock.util.TimeTick;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The clock worker 'TimeTick' implements the OSGi whiteboard pattern.
 * 
 * 
 * 
 * @author Stefan
 *
 */
public class TimeTickImpl implements TimeTick {
	
	static Logger logger = LoggerFactory.getLogger(TimeTickImpl.class);
	
	/**  */
	private BlockingQueue<Runnable> queue;
	
	/**  */
	private ExecutorService executor;
	
	/**  */
	private BitMap bitMap;
	
	/**  */
	BundleContext bundleContext;
	
	/**
	 * @param bitMap The map of the symbols
	 * @param queue The queue
	 * @param executor The executor of the queue
	 */
	public TimeTickImpl(BitMap bitMap, BlockingQueue<Runnable> queue,
			ExecutorService executor) {
		
		this.queue = queue;
		this.bitMap = bitMap;
		this.executor = executor;
		this.bundleContext = null;
	}

	public void tick(Calendar calendar) {
		Date date = calendar.getTime();
		
		//
		// Get crontab values
		//
		Integer[] keys = bitMap.getGroupMappingIntersects(new BitTime(date).getTime());
		
		logger.debug("Tick date:{}, keys:{}", date, keys.length);
		if(logger.isTraceEnabled()){
			logger.trace("Bits used: {}", new BitTime(date).getTime().toString());
			logger.trace( File.separatorChar=='\\' ? 
					bitMap.toString().replace("\n", "\r\n") : bitMap.toString() );
		}
		
		for(int i=0;i<keys.length;i++) {
			String symbol = bitMap.getSymbol(keys[i]);
			logger.debug("Add to queue key:{}, symbol:{}, waiting:{}", keys[i], symbol, queue.size());
			
			//queue.add( new Runner(symbol, bundleContext) );
			executor.execute( new Runner(symbol, bundleContext) );
		}//for
		
	}
	
	/**
	 * The runner class
	 * 
	 * @author Stefan
	 *
	 */
	private class Runner implements Runnable {
		
		private String symbol;
		private BundleContext context;
		
		public Runner(String symbol, BundleContext context) {
			this.symbol = symbol;
			this.context = context;
		}
		
		public void run() {
			String filter = "("+Notify.TIME_SYMBOL+"="+symbol+")";
			try {
				logger.debug("Notifying {}", symbol);
				
				ServiceReference<?>[] refs = context.getServiceReferences(Notify.class.getName(), filter);
				if(refs==null)
					return;
				
				for(int i=0;i<refs.length;i++) {
					Notify notify = (Notify)context.getService(refs[i]);
					
					Map<String, Object> param = new HashMap<String, Object>();
					param.put(Notify.TIME_SYMBOL, symbol);
					notify.notify(param);
				}//for
			} catch (InvalidSyntaxException e) {
				logger.error("Filter syntax exception.", e);
			}
			
		}
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

}
