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

import net.opengis.sos.x20.CapabilitiesDocument;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlValidationError;
import org.junit.Assert;
import org.junit.Test;
import org.n52.oxf.xmlbeans.parser.LaxValidationCase;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;

public class GetCapabilitiesValidationTest extends AbstractValidationTest {

	private static final Object FEATURE_QN = new QName("http://www.opengis.net/swes/2.0", "AbstractOffering");
	
	@Test
	public void validateCapabilities() throws ClientProtocolException, IOException, IllegalStateException, XmlException {
		String url = HttpUtil.resolveServiceURL();
		
		XmlObject xo = HttpUtil.executeGet(url.concat("GetCapabilities?service=SOS&request=GetCapabilities&f=xml"));
		
		Assert.assertTrue("Not a Capabilities doc:"+ xo.getClass(), xo instanceof CapabilitiesDocument);
		
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

	}

	
}
