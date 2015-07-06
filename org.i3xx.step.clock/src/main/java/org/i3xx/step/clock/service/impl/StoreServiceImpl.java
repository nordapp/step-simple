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

import org.i3xx.step.clock.service.model.ClockPersistenceService;
import org.i3xx.step.clock.service.model.ClockService;
import org.i3xx.step.clock.service.model.StoreService;
import org.i3xx.step.clock.util.StoreUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreServiceImpl implements StoreService {
	
	static Logger logger = LoggerFactory.getLogger(StoreService.class);
	
	/**  */
	BundleContext bundleContext;
	
	/**  */
	ClockService clockService;
	
	/**  */
	private String serviceName;
	
	/**  */
	private String namespace;
	
	public StoreServiceImpl() {
		bundleContext = null;
		clockService = null;
		serviceName = null;
		namespace = null;
	}
	
	public void addEntry(String stmt, String symbol) {
		ClockPersistenceService srv = getService();
		if(srv==null) {
			logger.info("The persistance service of the clock is not available.");
		}else{
			String temp = "text:"+symbol+":"+stmt;
			srv.addMapping(namespace, temp, symbol);
		}//fi
	}

	public void addEntry(BitSet bitSet, String symbol) {
		ClockPersistenceService srv = getService();
		if(srv==null) {
			logger.info("The persistance service of the clock is not available.");
		}else{
			
			String temp = "bits:"+symbol+":"+StoreUtils.serialize(bitSet);
			srv.addMapping(namespace, temp, symbol);
		}//fi
	}

	public void removeEntry(String symbol) {
		ClockPersistenceService srv = getService();
		if(srv==null) {
			logger.info("The persistance service of the clock is not available.");
		}else{
			srv.removeMapping(namespace, symbol);
		}//fi
	}
	
	/**
	 * Gets the persistence service.
	 * 
	 * @return The clock persistence service
	 */
	private ClockPersistenceService getService() {
		ServiceReference<?> ref = bundleContext.getServiceReference(serviceName);
		if(ref!=null) {
			Object srv = bundleContext.getService(ref);
			if(srv instanceof ClockPersistenceService) {
				return (ClockPersistenceService)srv;
			}
		}
		return null;
	}
	
	/**
	 * startUp the service (from blueprint)
	 */
	public void startUp() {
		ClockPersistenceService srv = getService();
		if(srv==null) {
			logger.info("The persistance service of the clock is not available.");
		}else{
			String[] map = srv.getSymbols(namespace);
			for(int i=0;i<map.length;i++) {
				String temp = srv.getMapping(namespace, map[i]);
				if(temp.startsWith("text:")) {
					String[] arr = temp.split("\\:", 3);
					String symbol = arr[1];
					String stmt = arr[2];
					
					clockService.addMapping(stmt, symbol);
				}else if(temp.startsWith("bits:")) {
					String[] arr = temp.split("\\:", 3);
					String symbol = arr[1];
					BitSet set = (BitSet)StoreUtils.deserialize(arr[2]);
					
					clockService.addMapping(set, symbol);
				}//fi
			}//for
		}//fi
	}
	
	/**
	 * shutDown the service (from blueprint)
	 */
	public void shutDown() {
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
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return the clockService
	 */
	public ClockService getClockService() {
		return clockService;
	}

	/**
	 * @param clockService the clockService to set
	 */
	public void setClockService(ClockService clockService) {
		this.clockService = clockService;
	}

}
