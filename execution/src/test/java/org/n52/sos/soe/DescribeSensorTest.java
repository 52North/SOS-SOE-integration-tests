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

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.SensorDescriptionType;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescribeSensorTest extends AbstractValidationTest {

	
	private static final Logger logger = LoggerFactory.getLogger(DescribeSensorTest.class);
	
	@Test
	public void validateDescribeSensor() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		String url = HttpUtil.resolveServiceURL();
		
		Configuration config = Configuration.instance();
		XmlObject xo = HttpUtil.executeGet(url.concat(
				String.format("DescribeSensor?service=SOS&version=2.0.0&request=DescribeSensor&procedure=%s&procedureDescriptionFormat=http://www.opengis.net/sensorML/1.0.1&f=xml", config.getNetwork())));
		
		Assert.assertTrue("Not a DescribeSensorResponse: "+ xo.getClass(), xo instanceof DescribeSensorResponseDocument);
		
		validateXml(xo);
		
		DescribeSensorResponseDocument ds = (DescribeSensorResponseDocument) xo;
		SensorDescriptionType description = ds.getDescribeSensorResponse().getDescriptionArray()[0].getSensorDescription();
		
		SensorMLDocument sml = SensorMLDocument.Factory.parse(description.getData().xmlText());
		
		logger.info("description is a SensorML 1.0.1 document: "+sml.getSensorML().getDomNode().getLocalName());
		
		validateXml(sml);
	}

}
