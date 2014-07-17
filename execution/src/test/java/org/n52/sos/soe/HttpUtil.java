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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	public static String resolveServiceURL() {
		String url = Configuration.instance().getSOEServiceURL();
		logger.info(url);

		Assert.assertNotNull("URL was null!", url);
		Assert.assertTrue("URL equalled '${sos.service.url}'", !url.equals("${sos.service.url}"));
		Assert.assertTrue("URL equalled was empty!", !url.isEmpty());
		return url;
	}
	
	public static XmlObject executeGet(String target) throws ClientProtocolException, IOException, IllegalStateException, XmlException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		
		logger.info("HTTP GET: "+ target);
		
		long start = System.currentTimeMillis();
		HttpResponse resp = client.execute(new HttpGet(target));
		logger.info("Request latency: "+ (System.currentTimeMillis()-start));

		XmlObject xo;
		try {
			xo = XmlObject.Factory.parse(resp.getEntity().getContent());
		} catch (IOException e) {
			throw e;
		} finally {
			client.close();
		}
		
		return xo;
	}

	public static JsonNode executeHttpGet(String target)
			throws IOException, ClientProtocolException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		
		logger.info("HTTP GET: "+ target);
		
		long start = System.currentTimeMillis();
		HttpResponse resp = client.execute(new HttpGet(target));
		logger.info("Request latency: "+ (System.currentTimeMillis()-start));
		
		ObjectMapper mapper = new ObjectMapper(); 
		JsonNode json = mapper.readTree(resp.getEntity().getContent());
		
		client.close();
		return json;
	}

}
