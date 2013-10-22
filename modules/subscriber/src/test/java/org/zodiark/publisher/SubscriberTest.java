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
package org.zodiark.publisher;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.zodiark.server.ZodiarkServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;

public class SubscriberTest {

    public final static String TEST = "This is a test";

    public final static int findFreePort()  {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(0);

            return socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 8080;
    }

    private ZodiarkServer server;
    private int port = findFreePort();

    @Test
    public void startZodiark() throws IOException {
        server = new ZodiarkServer().listen(URI.create("http://127.0.0.1:" + port))
                .service(Echo.class)
                .serve("./modules/publisher/src/test/resources")
                .serve("./modules/publisher/src/main/webapp/javascript").on();

//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        String a = "";
//        while (!(a.equals("quit"))) {
//            a = br.readLine();
//        }
//        System.exit(-1);
    }

    @AfterClass
    public void stopZodiark() {
        if (server != null) server.off();
    }

}
