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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Configuration {

	private static Configuration instance;
	private Properties properties;

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	private Configuration() {
		this.properties = new Properties();
		try {
			this.properties.load(getClass().getResourceAsStream("/sos-soe_EEA.properties"));
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	public static synchronized Configuration instance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	public String getSOEServiceURL() {
		return this.properties.getProperty("SOE_URL");
	}
	
	public String getObservedProperty() {
		return this.properties.getProperty("observedProperty");
	}
	
	public String getProcedure() {
		return this.properties.getProperty("procedure");
	}

	public List<String> getFeatures() {
		List<String> result = new ArrayList<>();
		result.add(this.properties.getProperty("features"));
		return result;
	}

	public String getNetwork() {
		return this.properties.getProperty("network");
	}
	
}
