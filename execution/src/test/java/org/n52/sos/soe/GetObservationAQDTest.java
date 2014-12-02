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

import net.opengis.gml.x32.FeatureCollectionDocument;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.om.x20.OMObservationDocument;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.aqd.aqd.x03.AQDReportingHeaderDocument;

public class GetObservationAQDTest extends GetObservationTest {
	
	private static final Logger logger = LoggerFactory.getLogger(GetObservationAQDTest.class);
	
	@Test
	@Override
	public void validateGetObservation() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		Configuration config = Configuration.instance();
		String url = HttpUtil.resolveServiceURL().concat(String.format("GetObservation?service=SOS&version=2.0.0&request=GetObservation&observedProperty=%s&procedure=%s&f=xml&responseFormat=%s",
				config.getObservedProperty(), config.getProcedure(), "http%3A%2F%2Faqd.ec.europa.eu%2Faqd%2F0.3.7c"));
		
		if (config.getTemporalFilter() != null) {
			url = url.concat("&temporalFilter=").concat(config.getTemporalFilter());
		}
		
		
		XmlObject xo = HttpUtil.executeGetAndParseAsXml(url);
		
		Assert.assertTrue("Not a FeatureCollection: "+xo.getClass(), xo instanceof FeatureCollectionDocument);
		
		registerLaxValidationForAbstractFeatures();
		
		validateXml(xo);
		
		FeatureCollectionDocument obs = (FeatureCollectionDocument) xo;
		
		for (FeaturePropertyType fpt : obs.getFeatureCollection().getFeatureMemberArray()) {
			AQDReportingHeaderDocument aqd = AQDReportingHeaderDocument.Factory.parse(fpt.xmlText());
			
			logger.info("Got an AQD_ReportingHeader: "+aqd.getAQDReportingHeader().getDomNode().getLocalName());
			
			validateXml(aqd);
			
			for (FeaturePropertyType content : aqd.getAQDReportingHeader().getContentArray()) {
				OMObservationDocument om = OMObservationDocument.Factory.parse(content.xmlText());
				
				validateXml(om);
				
				validateContentsAndReturnValueCount(om.getOMObservation());
			}
		}
	}

}
