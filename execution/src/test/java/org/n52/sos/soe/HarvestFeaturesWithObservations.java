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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.opengis.om.x20.OMObservationType;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.ComponentType;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sos.x20.CapabilitiesDocument;
import net.opengis.sos.x20.GetObservationResponseDocument;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;
import net.opengis.sos.x20.ObservationOfferingDocument;
import net.opengis.swe.x101.AbstractDataRecordType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x20.DataArrayDocument;
import net.opengis.swes.x20.AbstractContentsType.Offering;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.SensorDescriptionType;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvestFeaturesWithObservations extends AbstractValidationTest {

	private static final Logger logger = LoggerFactory
			.getLogger(HarvestFeaturesWithObservations.class);

	public static void main(String[] args) throws ClientProtocolException,
	IllegalStateException, IOException, XmlException {
		new HarvestFeaturesWithObservations().harvestNetworks();
	}
	
//	@Test
	public void harvestNetworks() throws ClientProtocolException,
			IllegalStateException, IOException, XmlException {
		String url = HttpUtil.resolveServiceURL();

		List<String> networks = findNetworks();
		boolean doing = true;
		for (String n : networks) {
//			if ("NET.DE_UB".equals(n)) {
//				doing = true;
//			}
			
			if (!doing) {
				continue;
			}
			
			logger.info("########### Doing network: "+n);
			
			XmlObject xo = HttpUtil
					.executeGetAndParseAsXml(url.concat(String
							.format("DescribeSensor?service=SOS&version=2.0.0&request=DescribeSensor&procedure=%s&procedureDescriptionFormat=http://www.opengis.net/sensorML/1.0.1&f=xml",
									n)));

			Assert.assertTrue("Not a DescribeSensorResponse: " + xo.getClass(),
					xo instanceof DescribeSensorResponseDocument);

			validateXml(xo);

			resolveAssociatedObservationCount((DescribeSensorResponseDocument) xo);
		}
	}

	private List<String> findNetworks() throws ClientProtocolException, IllegalStateException, IOException, XmlException {
		String url = HttpUtil.resolveServiceURL();
		
		XmlObject xo = HttpUtil.executeGetAndParseAsXml(url.concat("GetCapabilities?service=SOS&request=GetCapabilities&f=xml"));
		
		CapabilitiesDocument caps = (CapabilitiesDocument) xo;
		
		List<String> result = new ArrayList<>();
		
		for (Offering o : caps.getCapabilities().getContents().getContents().getOfferingArray()) {
			ObservationOfferingDocument off = ObservationOfferingDocument.Factory.parse(o.xmlText());
			result.add(off.getObservationOffering().getIdentifier());
		}
		
		return result;
	}

	protected void resolveAssociatedObservationCount(DescribeSensorResponseDocument ds)
			throws ClientProtocolException, IllegalStateException, IOException,
			XmlException {
		SensorDescriptionType description = ds.getDescribeSensorResponse()
				.getDescriptionArray()[0].getSensorDescription();

		SensorMLDocument sml = SensorMLDocument.Factory.parse(description
				.getData().xmlText());

		logger.info("description is a SensorML 1.0.1 document: "
				+ sml.getSensorML().getDomNode().getLocalName());

		validateXml(sml);

		AbstractProcessType proc = sml.getSensorML().getMemberArray(0)
				.getProcess();
		if (proc instanceof SystemType) {
			SystemType system = (SystemType) proc;

			if (!system.isSetComponents() || !system.getComponents().isSetComponentList()) {
				logger.info("Network does not have any components!");
				return;
			}
			
			for (Component c : system.getComponents().getComponentList()
					.getComponentArray()) {
				proc = c.getProcess();
				if (proc instanceof ComponentType) {
					ComponentType component = (ComponentType) proc;
					AbstractDataRecordType adr = component
							.getCapabilitiesArray(0).getAbstractDataRecord();

					if (adr instanceof DataRecordType) {
						DataRecordType dr = (DataRecordType) adr;

						for (DataComponentPropertyType prop : dr
								.getFieldArray()) {
							if (prop.getName().startsWith("FeatureOfInterest")) {
								getObservationByFeature(prop.getText()
										.getValue());
							}
						}
					}
				}
			}
		}
	}

	private void getObservationByFeature(String feature)
			throws ClientProtocolException, IllegalStateException, IOException,
			XmlException {
		String n = URLEncoder.encode(feature, "UTF-8");
		String url = HttpUtil.resolveServiceURL();

		XmlObject xo = HttpUtil
				.executeGetAndParseAsXml(url.concat(String
						.format("GetObservation?service=SOS&version=2.0.0&request=GetObservation&offering=&observedProperty=&procedure=&featureOfInterest=%s&namespaces=&spatialFilter=&temporalFilter=om:phenomenonTime,%s/%s&aggregationType=&responseFormat=&f=xml",
								n,
								"2014-02-03T14:00:00Z",
								"2014-03-03T14:00:00Z")));

		int count = checkForObservations(xo);

		logger.info(count +" observations associated with Feature " + feature);
	}

	private int checkForObservations(XmlObject xo) throws XmlException {
		if (xo instanceof ExceptionReportDocument) {
			ExceptionReportDocument excep = (ExceptionReportDocument) xo;
			String code = excep.getExceptionReport().getExceptionArray(0)
					.getExceptionCode();

			if ("ResponseExceedsSizeLimit".equals(code)) {
				return 1000;
			}
		}

		else if (xo instanceof GetObservationResponseDocument) {
			GetObservationResponseDocument obs = (GetObservationResponseDocument) xo;

			if (obs.getGetObservationResponse().getObservationDataArray().length == 0) {
				return 0;
			}
			
			for (ObservationData od : obs.getGetObservationResponse()
					.getObservationDataArray()) {
				try {
					return extractValueCount(od.getOMObservation());
				}
				catch (XmlException | NullPointerException e) {
					logger.warn(e.getMessage(), e);
					return 0;
				}
			}
		}

		return 0;
	}

	protected int extractValueCount(OMObservationType omObservationType)
			throws XmlException {
		DataArrayDocument dad = DataArrayDocument.Factory
				.parse(omObservationType.getResult().xmlText());

		logger.info("Got a DataArray result: "
				+ dad.getDataArray1().getDomNode().getLocalName());

		validateXml(dad, false);

		return dad.getDataArray1().getElementCount().getCount().getValue()
				.intValue();
	}

}
