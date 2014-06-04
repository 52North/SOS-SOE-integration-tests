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

import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlValidationError;
import org.junit.Assert;
import org.n52.oxf.xmlbeans.parser.LaxValidationCase;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractValidationTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractValidationTest.class);

	protected static final QName FEATURE_QN = new QName(
			"http://www.opengis.net/gml/3.2", "AbstractFeature");
	
	protected void validateXml(XmlObject xo) {
		Collection<XmlError> errors = XMLBeansParser.validate(xo);
		
		if (!errors.isEmpty()) {
			for (XmlError xmlError : errors) {
				logger.warn(xmlError.toString());
			}
			Assert.fail("Response is not valid!");
		}
	}
	
	protected void registerLaxValidationForAbstractFeatures() {
		XMLBeansParser.registerLaxValidationCase(new LaxValidationCase() {

			public boolean shouldPass(XmlValidationError xve) {
				return xve.getExpectedQNames() != null
						&& xve.getExpectedQNames().contains(FEATURE_QN);
			}

			public boolean shouldPass(XmlError validationError) {
				if (!(validationError instanceof XmlValidationError))
					return false;

				XmlValidationError xve = (XmlValidationError) validationError;
				return shouldPass(xve);
			}
		});		
	}
	
}
