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
import java.util.Collection;

import javax.xml.namespace.QName;

import net.opengis.sos.x20.CapabilitiesDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlValidationError;
import org.junit.Assert;
import org.junit.Test;
import org.n52.oxf.xmlbeans.parser.LaxValidationCase;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCapabilitiesValidationTest {

	private static final Logger logger = LoggerFactory.getLogger(GetCapabilitiesValidationTest.class);
	
	private static final Object FEATURE_QN = new QName("http://www.opengis.net/swes/2.0", "AbstractOffering");
	
	@Test
	public void validateCapabilities() throws ClientProtocolException, IOException, IllegalStateException, XmlException {
		String url = Configuration.instance().getSOEServiceURL();
		logger.info(url);

		Assert.assertNotNull("URL was null!", url);
		Assert.assertTrue("URL equalled '${sos.service.url}'", !url.equals("${sos.service.url}"));
		Assert.assertTrue("URL equalled was empty!", !url.isEmpty());
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
				
		HttpResponse resp = client.execute(new HttpGet(url.concat("GetCapabilities?service=SOS&request=GetCapabilities&f=xml")));

		XmlObject xo;
		try {
			xo = XmlObject.Factory.parse(resp.getEntity().getContent());
		} catch (IOException e) {
			throw e;
		} finally {
			client.close();
		}
		
		Assert.assertTrue("Not a Capabilities doc", xo instanceof CapabilitiesDocument);
		
		CapabilitiesDocument caps = (CapabilitiesDocument) xo;
		
		XMLBeansParser.registerLaxValidationCase(new LaxValidationCase() {
			
			public boolean shouldPass(XmlValidationError xve) {
				return xve.getExpectedQNames() != null && xve.getExpectedQNames().contains(FEATURE_QN);
			}

			public boolean shouldPass(XmlError validationError) {
				if (!(validationError instanceof XmlValidationError)) return false;
				
				XmlValidationError xve = (XmlValidationError) validationError;
				return shouldPass(xve);
			}
		});
		
		Collection<XmlError> errors = XMLBeansParser.validate(caps);
		
		if (!errors.isEmpty()) {
			for (XmlError xmlError : errors) {
				logger.warn(xmlError.toString());
			}
			Assert.fail("Capabilities not valid!");
		}
	}
	
}
