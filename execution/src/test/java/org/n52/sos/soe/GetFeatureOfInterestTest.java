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
import java.util.Arrays;
import java.util.List;

import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.aqd.aqd.x03.AQDSampleDocument;

public class GetFeatureOfInterestTest extends AbstractValidationTest {

	private static final Logger logger = LoggerFactory
			.getLogger(GetFeatureOfInterestTest.class);

	List<String> featureIds = Arrays
			.asList(new String[] {
					"http%3A%2F%2Fcdr.eionet.europa.eu%2Fhu%2Feu%2Faqd%2Fd%2Fenvut_vxq%2FREP_D-HU_OMSZ_20140122_D-001.xml%23SPO_F-HU0002A_00001_500_500" });

	@Test
	public void validateGetFOI() throws ClientProtocolException,
			IllegalStateException, IOException, XmlException {
		
		registerLaxValidationForAbstractFeatures();
		
		for (String fid : featureIds) {
			String target = String.format("%s%s",
					HttpUtil.resolveServiceURL(),
					String.format("GetFeatureOfInterest?service=SOS&version=2.0.0&request=GetFeatureOfInterest&featureOfInterest=%s&observedProperty=&procedure=&namespaces=&spatialFilter=&f=xml", fid));

			XmlObject xo = HttpUtil.executeGet(target);

			Assert.assertTrue(
					"Not a GetFeatureOfInterest Reponse: " + xo.getClass(),
					xo instanceof GetFeatureOfInterestResponseDocument);

			validateXml(xo);

			GetFeatureOfInterestResponseDocument foi = (GetFeatureOfInterestResponseDocument) xo;

			AQDSampleDocument aqdSampl = AQDSampleDocument.Factory
					.parse(foi.getGetFeatureOfInterestResponse()
							.getFeatureMemberArray()[0].xmlText());

			logger.info("Got a AQD Sample: "
					+ aqdSampl.getAQDSample().getDomNode().getLocalName());

			validateXml(aqdSampl);

		}

	}
}
