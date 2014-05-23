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

import javax.xml.namespace.QName;

import net.opengis.gml.x32.FeatureCollectionDocument;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.om.x20.OMObservationDocument;

import org.apache.http.client.ClientProtocolException;
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

import eu.europa.ec.aqd.aqd.x03.AQDReportingHeaderDocument;

public class GetObservationAQDTest extends GetObservationTest {
	
	private static final Logger logger = LoggerFactory.getLogger(GetObservationAQDTest.class);
	
	protected static final QName FEATURE_QN = new QName(
			"http://www.opengis.net/gml/3.2", "AbstractFeature");
	
	@Test
	@Override
	public void validateGetObservation() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		String url = HttpUtil.resolveServiceURL().concat("GetObservation?service=SOS&version=2.0.0&request=GetObservation&offering=&observedProperty=http%3A%2F%2Fdd.eionet.europa.eu%2Fvocabulary%2Faq%2Fpollutant%2F1&procedure=http%3A%2F%2Fcdr.eionet.europa.eu%2Fpl%2Feu%2Faqd%2Fd%2Fenvuvt6ea%2FPL_REPORT_D_FOR_2014v2.xml%23SPO_P_PL0518A_1_001&featureOfInterest=&namespaces=&spatialFilter=&temporalFilter=om:phenomenonTime,2013-12-03T14:00:00Z/2014-03-04T14:00:00Z&aggregationType=&responseFormat=http%3A%2F%2Faqd.ec.europa.eu%2Faqd%2F0.3.7c&f=xml");
		
		XmlObject xo = HttpUtil.executeGet(url);
		
		Assert.assertTrue("Not a FeatureCollection: "+xo.getClass(), xo instanceof FeatureCollectionDocument);
		
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
		
		validateXml(xo);
		
		FeatureCollectionDocument obs = (FeatureCollectionDocument) xo;
		
		for (FeaturePropertyType fpt : obs.getFeatureCollection().getFeatureMemberArray()) {
			AQDReportingHeaderDocument aqd = AQDReportingHeaderDocument.Factory.parse(fpt.xmlText());
			
			logger.info("Got an AQD_ReportingHeader: "+aqd.getAQDReportingHeader().getDomNode().getLocalName());
			
			validateXml(aqd);
			
			for (FeaturePropertyType content : aqd.getAQDReportingHeader().getContentArray()) {
				OMObservationDocument om = OMObservationDocument.Factory.parse(content.xmlText());
				
				validateXml(om);
				
				validateContents(om.getOMObservation());
			}
		}
	}

}
