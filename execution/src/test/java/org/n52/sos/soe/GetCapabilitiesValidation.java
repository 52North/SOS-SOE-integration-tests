/**
 * Copyright (C) 2014 - 2014
 * by 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: http://52north.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sos.soe;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCapabilitiesValidation {

	private static final Logger logger = LoggerFactory.getLogger(GetCapabilitiesValidation.class);
	
	@Test
	public void validateCapabilities() {
		String url = Configuration.instance().getSOEServiceURL();
		logger.info(url);
		
		Assert.assertNotNull(url);
		Assert.assertTrue(url.equals("halalslslsls"));
	}
	
}
