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


import java.util.Map;

public interface Notify {
	
	/**
	 * The key of the symbol used in the filter rule and the parameter map.
	 */
	public static final String TIME_SYMBOL = "time-symbol";
	
	/**
	 * The clock uses a whiteboard pattern to notify the watchers.
	 * 
	 * @param param The parameter of the event
	 */
	void notify(Map<String, Object> param);
}
