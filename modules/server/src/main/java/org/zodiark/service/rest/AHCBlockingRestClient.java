/*
 * Copyright 2013 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zodiark.service.rest;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

public class AHCBlockingRestClient implements RestClient {

    private final Logger logger = LoggerFactory.getLogger(AHCBlockingRestClient.class);

    private AsyncHttpClient client;

    @Inject
    public URI dbTarget;

    private String dbLocation;

    @PostConstruct
    public void initAHC() throws MalformedURLException {
        AsyncHttpClientConfig.Builder b = new AsyncHttpClientConfig.Builder();
        b.setFollowRedirects(true).setIdleConnectionTimeoutInMs(-1).setRequestTimeoutInMs(-1).setUserAgent("Zodiark/1.1");

        NettyAsyncHttpProviderConfig nettyConfig = new NettyAsyncHttpProviderConfig();

        nettyConfig.addProperty("child.tcpNoDelay", "true");
        nettyConfig.addProperty("child.keepAlive", "true");

        dbLocation = dbTarget.toURL().toString();
        if  (dbLocation.endsWith("/")) {
            dbLocation.substring(0, dbLocation.length() -1);
        }

        client = new AsyncHttpClient(b.setAsyncHttpClientProviderConfig(nettyConfig).build());
        logger.debug("AHC Client ready", client);
    }

    @Override
    public String serve(RestServiceImpl.METHOD m, String url, String body) throws IOException {
        url = dbLocation + url;
        logger.debug("Invoking DB with {}", url);
        Response response = null;
        try {
            switch (m) {
                case GET:
                    response = client.prepareGet(url).execute().get();
                    break;
                case POST:
                    response = client.preparePost(url).setBody(body).execute().get();
                    break;
                case PUT:
                    response = client.preparePost(url).setBody(body).execute().get();
                    break;
                case DELETE:
                    response = client.preparePost(url).setBody(body).execute().get();
                    break;
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }

        return response.getResponseBody();
    }
}
