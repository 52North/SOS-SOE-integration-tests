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

import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Test;

public class GetFirstGetLatestObservationTest extends AbstractValidationTest {
	
	private static final String BASE_REQUEST = "GetObservation?service=SOS&version=2.0.0&request=GetObservation&observedProperty=%s&procedure=%s&f=xml&temporalFilter=om:phenomenonTime,%s";
	
	@Test
	public void testGetFirst() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		Configuration config = Configuration.instance();
		String url = HttpUtil.resolveServiceURL().concat(String.format(BASE_REQUEST,
				config.getObservedProperty(), config.getProcedure(), "first"));
		
		XmlObject xo = HttpUtil.executeGetAndParseAsXml(url);
		
		Assert.assertTrue("Not a GetObservationResponse: "+xo.getClass(), xo instanceof GetObservationResponseDocument);
		
		validateXml(xo);
		
		GetObservationResponseDocument obs = (GetObservationResponseDocument) xo;
		
		Assert.assertNotNull("Got no ObservationData!", obs.getGetObservationResponse().getObservationDataArray(0));
		Assert.assertNotNull("Got no Result!", obs.getGetObservationResponse().getObservationDataArray(0).getOMObservation().getResult());
		
		for (ObservationData od : obs.getGetObservationResponse().getObservationDataArray()) {
			int c = GetObservationTest.validateContentsAndReturnValueCount(od.getOMObservation());
			
			Assert.assertTrue("GetFirst should only return 1 value!", c == 1);
		}
	}
	
	@Test
	public void testGetLatest() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		Configuration config = Configuration.instance();
		String url = HttpUtil.resolveServiceURL().concat(String.format(BASE_REQUEST,
				config.getObservedProperty(), config.getProcedure(), "latest"));
		
		XmlObject xo = HttpUtil.executeGetAndParseAsXml(url);
		
		Assert.assertTrue("Not a GetObservationResponse: "+xo.getClass(), xo instanceof GetObservationResponseDocument);
		
		validateXml(xo);
		
		GetObservationResponseDocument obs = (GetObservationResponseDocument) xo;
		
		Assert.assertNotNull("Got no ObservationData!", obs.getGetObservationResponse().getObservationDataArray(0));
		Assert.assertNotNull("Got no Result!", obs.getGetObservationResponse().getObservationDataArray(0).getOMObservation().getResult());
		
		for (ObservationData od : obs.getGetObservationResponse().getObservationDataArray()) {
			int c = GetObservationTest.validateContentsAndReturnValueCount(od.getOMObservation());
			
			Assert.assertTrue("GetLatest should only return 1 value!", c == 1);
		}
		
	}
	
	

}
