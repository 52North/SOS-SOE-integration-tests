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

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCacheMetadataTest extends AbstractValidationTest {

	
	private static final Logger logger = LoggerFactory.getLogger(GetCacheMetadataTest.class);
	
	@Test
	public void validateGetCacheMetadata() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		String url = HttpUtil.resolveServiceURL();
		
		JsonNode json = HttpUtil.executeHttpGet(url.concat(
				"GetCacheMetadata?service=SOS&version=2.0.0&request=GetCacheMetadata&f=json"));
		
		logger.info(json.toString());
		
		JsonNode pum = json.get("PropertyUnitMappingCache");
		Assert.assertTrue("PropertyUnitMappingCache too old", validateLatestExecution(pum, url));
		
		JsonNode oo = json.get("ObservationOfferingCache");
		Assert.assertTrue("ObservationOfferingCache too old", validateLatestExecution(oo, url));
		
		Assert.assertTrue("coult not find updateCacheOnStartup!", json.has("updateCacheOnStartup"));
	}

	private boolean validateLatestExecution(JsonNode pum, String url) {
		DateTime lastUpdated = new DateTime(pum.get("lastUpdated").getTextValue());
		
		DateTime yesterdayPlusOneHour;
		if (url.contains("ags.dev.52north")) {
			yesterdayPlusOneHour= new DateTime().minusMinutes(25);
		}
		else {
			yesterdayPlusOneHour= new DateTime().minusHours(23);
		}

		return lastUpdated.isAfter(yesterdayPlusOneHour);
	}

}
