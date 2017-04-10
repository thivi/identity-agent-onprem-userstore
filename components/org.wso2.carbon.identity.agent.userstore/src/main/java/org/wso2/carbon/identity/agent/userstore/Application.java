/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.identity.agent.userstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.agent.userstore.security.SecretManagerInitializer;

import java.net.URISyntaxException;
import java.util.Scanner;
import javax.net.ssl.SSLException;

/**
 * org.wso2.carbon.identity.agent.outbound.Application entry point.
 *
 * @since 0.1
 */

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClient.class);
    private Thread shutdownHook;

    public static void main(String[] args) throws InterruptedException, SSLException, URISyntaxException {

        Application application = new Application();
        application.startAgent();
    }

    private void startAgent() throws InterruptedException, SSLException, URISyntaxException {
        new SecretManagerInitializer().init();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Access token: ");
        String accessToken = scanner.next();
        System.out.print("Enter Node (1, 2) : ");
        String node = scanner.next();
        WebSocketClient echoClient = new WebSocketClient("ws://localhost:8080/server/" + accessToken + "/" + node);
        //TODO configure URL
        echoClient.handhshake();
        LOGGER.info("############ echoClient 1 : " + echoClient);
        Application app = new Application();
        app.addShutdownHook(echoClient);
        LOGGER.info("############ echoClient 3 : " + echoClient);
    }

    private void addShutdownHook(WebSocketClient echoClient) {
        LOGGER.info("############ echoClient 2 : " + echoClient);
        if (shutdownHook != null) {
            return;
        }
        LOGGER.info("############ echoClient 3 : " + echoClient);
        shutdownHook = new Thread() {

            public void run() {
                LOGGER.info("############ echoClient 4 : " + echoClient);
                LOGGER.info("Shutdown hook triggered....");
                shutdownGracefully(echoClient);
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private void shutdownGracefully(WebSocketClient echoClient) {
        try {
            LOGGER.info("############ echoClient 5 : " + echoClient);
            echoClient.shutDown();
        } catch (InterruptedException e) {
            LOGGER.error("Error occurred while sending shutdown signal.");
        }
        LOGGER.info("Shutdown hook triggered....");
    }
}
