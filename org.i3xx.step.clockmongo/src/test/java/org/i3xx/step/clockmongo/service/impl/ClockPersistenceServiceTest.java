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


import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.PropertyConfigurator;
import org.i3xx.step.clock.service.model.ClockPersistenceService;
import org.i3xx.test.workspace.Workspace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClockPersistenceServiceTest {

	ClockPersistenceService service;
	
	@Before
	public void setUp() throws Exception {
		String home = Workspace.location()+("/src/test/properties".replace('/', File.separatorChar));
		
		//Log4j configuration and setup
		PropertyConfigurator.configure(home+File.separator+"Log4j.properties");
		
		service = new ClockPersistenceServiceImpl();
		((ClockPersistenceServiceImpl) service).startUp();
	}

	@After
	public void tearDown() throws Exception {
		((ClockPersistenceServiceImpl) service).shutDown();
	}

	@Test
	public void test1() {
		service.addMapping("clock", "stmt-1", "symbol-A");
		service.addMapping("clock", "stmt-2", "symbol-B");
		service.addMapping("clock", "stmt-3", "symbol-C");
		service.addMapping("clock", "stmt-4", "symbol-D");
		service.addMapping("clock", "stmt-5", "symbol-E");
		service.addMapping("clock", "stmt-6", "symbol-F");
	}

	@Test
	public void test2() {
		assertEquals("stmt-4", service.getMapping("clock", "symbol-D"));
		assertEquals("stmt-2", service.getMapping("clock", "symbol-B"));
		assertEquals("stmt-5", service.getMapping("clock", "symbol-E"));
	}

	@Test
	public void test3() {
		String[] list = service.getSymbols("clock");
		Arrays.sort(list);
		assertEquals("symbol-A", list[0]);
		assertEquals("symbol-B", list[1]);
		assertEquals("symbol-C", list[2]);
		assertEquals("symbol-D", list[3]);
		assertEquals("symbol-E", list[4]);
		assertEquals("symbol-F", list[5]);
		assertEquals(6, list.length);
	}

	@Test
	public void test4() {
		service.removeMapping("clock", "symbol-B");
		service.removeMapping("clock", "symbol-C");
		service.removeMapping("clock", "symbol-D");
		service.addMapping("clock", "stmt-2", "symbol-C");
		service.addMapping("clock", "stmt-3", "symbol-B");
		service.addMapping("clock", "stmt-4", "symbol-D");
	}

	@Test
	public void test5() {
		String[] list = service.getSymbols("clock");
		Arrays.sort(list);
		assertEquals("symbol-A", list[0]);
		assertEquals("symbol-B", list[1]);
		assertEquals("symbol-C", list[2]);
		assertEquals("symbol-D", list[3]);
		assertEquals("symbol-E", list[4]);
		assertEquals("symbol-F", list[5]);
		assertEquals(6, list.length);
	}
}
