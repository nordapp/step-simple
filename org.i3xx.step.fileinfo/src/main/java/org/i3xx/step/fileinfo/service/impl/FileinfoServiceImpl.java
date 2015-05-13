package org.i3xx.step.fileinfo.service.impl;

/*
 * #%L
 * NordApp OfficeBase :: fileinfo
 * %%
 * Copyright (C) 2015 I.D.S. DialogSysteme GmbH
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


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.i3xx.step.fileinfo.service.model.FileinfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileinfoServiceImpl implements FileinfoService {
	
	Logger logger = LoggerFactory.getLogger(FileinfoServiceImpl.class);
	
	public void startUp() {
		logger.debug("Bundle starts");
	}
	
	public void shutDown() {
		logger.debug("Bundle stops");
	}
	
	/* (non-Javadoc)
	 * @see org.i3xx.step.fileinfo.service.model.FileinfoService#getMimetype(java.lang.String, java.io.InputStream)
	 */
	public String getMimetype(String filename, InputStream in) throws IOException {
		String contentType = null;
		if(contentType==null) {
			Tika tika = new Tika();
			contentType = tika.detect(filename);
			if(contentType==null && in!=null) {
				in = new BufferedInputStream(in);
				contentType = tika.detect(in);
			}
		}
		
		return contentType;
	}

}
