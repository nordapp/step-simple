package org.i3xx.step.clockmongo.service.impl;

/*
 * #%L
 * NordApp OfficeBase :: clockmongo
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
import java.util.List;

import org.i3xx.step.clock.service.model.ClockPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class ClockPersistenceServiceImpl implements ClockPersistenceService {
	
	private static Logger logger = LoggerFactory.getLogger(ClockPersistenceService.class);
	
	/** The name of the intern database */
	private static final String DB_NAME = "naCLOCKSTORE";
	
	/** Index in ascending order */
	private static final int ASC = -1;
	
	private String host;
	private int port;
	
	private MongoClient mongo;
	private DB db;

	public ClockPersistenceServiceImpl() {
		host = "localhost";
		port = 27017;
		
		mongo = null;
		db = null;
	}
	
	/**
	 * Startup the service
	 * 
	 * @throws Exception
	 */
	public void startUp() throws Exception {
		logger.debug("Starts the clock store service 'ClockPersistenceServiceImpl'");
		
		mongo = new MongoClient(host, port);
		db = mongo.getDB(DB_NAME);
	}
	
	/**
	 * Shutdown the service
	 * 
	 * @throws Exception
	 */
	public void shutDown() throws Exception {
		logger.debug("Shutdown the clock store service 'ClockPersistenceServiceImpl'");
		
		mongo.close();
		mongo = null;
	}

	/**
	 * @param nspc The namespace
	 * @return The list of symbols as an array
	 */
	public String[] getSymbols(String nspc) {
		logger.trace("Searches all symbols nspc:{}", nspc);
		
		DBCollection col = ensureCollection(nspc, null);
		DBCursor cursor = col.find();
		List<String> list = new ArrayList<String>();
		while(cursor.hasNext()) {
			String symbol = (String)cursor.next().get("symbol");
			list.add(symbol);
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * @param nspc The namespace
	 * @param stmt The time statement
	 * @param symbol The symbol to add
	 */
	public void addMapping(String nspc, String stmt, String symbol) {
		logger.trace("Adds the object nspc:{} symbol:{} stmt:{}", nspc, symbol, stmt);
		
		DBCollection col = ensureCollection(nspc, symbol);
		DBObject dbo = ensureObject(col, symbol);
		
		dbo.put("statement", stmt);
		
		col.update(symbolQuery(symbol), dbo, false, false);
	}

	/**
	 * @param nspc The namespace
	 * @param symbol The symbol to remove
	 */
	public void removeMapping(String nspc, String symbol) {
		logger.trace("Removes the object nspc:{}, symbol:{}", nspc, symbol);
		
		DBCollection col = ensureCollection(nspc, symbol);
		col.remove(symbolQuery(symbol));
	}

	/**
	 * @param nspc The namespace
	 * @param symbol The symbol
	 * @return The time statement
	 */
	public String getMapping(String nspc, String symbol) {
		logger.trace("Retrieves the object nspc:{}, symbol:{}", nspc, symbol);
		
		DBCollection col = ensureCollection(nspc, symbol);
		DBObject dbo = col.findOne(symbolQuery(symbol));
		
		if(dbo==null){
			return null;
		}
		
		return (String)dbo.get("statement");
	}
	
	//
	//
	//
	
	/**
	 * @param nspc The namespace
	 * @param symbol The symbol
	 * @return The DBCollection
	 */
	private DBCollection ensureCollection(String nspc, String symbol) {
		
		DBCollection col = null;
		
		if( db.collectionExists(nspc) ) {
			col = db.getCollection(nspc);
		}else{
			col = db.getCollection(nspc);
			col.createIndex(new BasicDBObject("symbol", ASC));
		}//fi
		
		return col;
	}
	
	/**
	 * @param col The collection to get the object from
	 * @param symbol The symbol (key)
	 * @param timeout The timeout or -1 for no timeout
	 * @return The DBObject
	 */
	private DBObject ensureObject(DBCollection col, String symbol) {
		DBObject dbo = null;
		DBObject query = new BasicDBObject("symbol", symbol);
		dbo = col.findOne(query);
		
		if(dbo == null) {
			String nspc = col.getName();
			
			dbo = new BasicDBObject();
			dbo.put("nspc", nspc);
			dbo.put("symbol", symbol);
			dbo.put("born", new Long(System.currentTimeMillis()));
			dbo.put("statement", "");
			
			col.insert(dbo);
		}
		
		return dbo;
	}
	
	/**
	 * Returns the query to get an object from the collection.
	 * @param symbol The symbol
	 * @return The DBObject
	 */
	private DBObject symbolQuery(String symbol) {
		return new BasicDBObject("symbol", symbol);
	}

	//
	// Parameter settings
	//
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

}
