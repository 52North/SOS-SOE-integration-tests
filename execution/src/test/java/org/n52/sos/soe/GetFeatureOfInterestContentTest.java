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

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GetFeatureOfInterestContentTest extends AbstractValidationTest {

	@Before
	public void registerLaxValidation() {
		registerLaxValidationForAbstractFeatures();
	}
	
	@Test
	public void instanceShouldReturnZeroFeatures() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		String subUrl = "GetFeatureOfInterest?service=SOS&version=2.0.0&request=GetFeatureOfInterest&featureOfInterest=&observedProperty=&procedure=IamNotAProcedureAtAll&namespaces=&spatialFilter=&f=xml";
		GetFeatureOfInterestResponseDocument getFoi = executeAndParseRequest(subUrl);
		
		Assert.assertTrue("Response was not expected to contain features", getFoi.getGetFeatureOfInterestResponse().getFeatureMemberArray().length == 0);
	}
	
	@Test
	public void instanceShouldReturnSizeLimitExceedsException() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		String url = HttpUtil.resolveServiceURL().concat("GetFeatureOfInterest?service=SOS&version=2.0.0&request=GetFeatureOfInterest&featureOfInterest=&observedProperty=&procedure=&namespaces=&spatialFilter=&f=xml");
		
		XmlObject xo = HttpUtil.executeGet(url);
		
		Assert.assertTrue("Not a ExceptionReportDocument: "+xo.getClass(), xo instanceof ExceptionReportDocument);
		
		validateXml(xo);
		
		ExceptionReportDocument exc = (ExceptionReportDocument) xo;
		Assert.assertTrue("not the expected exception", "ResponseExceedsSizeLimit".equals(exc.getExceptionReport().getExceptionArray(0).getExceptionCode()));
	}
	
	@Test
	public void instanceShouldReturnFeaturesForNetwork() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		String subUrl = "GetFeatureOfInterest?service=SOS&version=2.0.0&request=GetFeatureOfInterest&featureOfInterest=&observedProperty=&procedure=NET-HU004A&namespaces=&spatialFilter=&f=xml";
		GetFeatureOfInterestResponseDocument getFoi = executeAndParseRequest(subUrl);
		
		Assert.assertTrue("Response did not contain multiple features!", getFoi.getGetFeatureOfInterestResponse().getFeatureMemberArray().length > 1);
	}
	
	@Test
	public void instanceShouldReturnOneFeature() throws ClientProtocolException, IOException, XmlException {
		String subUrl = "GetFeatureOfInterest?service=SOS&version=2.0.0&request=GetFeatureOfInterest&featureOfInterest=http%3A%2F%2Fcdr.eionet.europa.eu%2Fhu%2Feu%2Faqd%2Fd%2Fenvut_vxq%2FREP_D-HU_OMSZ_20140122_D-001.xml%23SPO_F-HU0002A_00001_500_500&observedProperty=&procedure=&namespaces=&spatialFilter=&f=xml";
		
		GetFeatureOfInterestResponseDocument getFoi = executeAndParseRequest(subUrl);
		
		Assert.assertTrue("Response did not contain exactly one feature!", getFoi.getGetFeatureOfInterestResponse().getFeatureMemberArray().length == 1);
	}


	private GetFeatureOfInterestResponseDocument executeAndParseRequest(String subUrl) throws ClientProtocolException, IOException,
			XmlException {
		String url = HttpUtil.resolveServiceURL().concat(subUrl);
		
		XmlObject xo = HttpUtil.executeGet(url);
		
		Assert.assertTrue("Not a GetFeatureOfInterestResponseDocument: "+xo.getClass(), xo instanceof GetFeatureOfInterestResponseDocument);
		
		validateXml(xo);
		
		return (GetFeatureOfInterestResponseDocument) xo;
	}
}
