/**
* The MIT License (MIT)
*
* Copyright (c) 2014 ZuInnoTe (zuinnote@gmail.com) / Jörn Franke

* Permission is hereby granted, free of charge, to any person obtaining a copy of
* this software and associated documentation files (the "Software"), to deal in
* the Software without restriction, including without limitation the rights to
* use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
* the Software, and to permit persons to whom the Software is furnished to do so,
* subject to the following conditions:

* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
* FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
* COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
* IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
* CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package org.zuinnote.cloudbigdata.config;

import org.apache.log4j.Logger;

import org.springframework.context.annotation.Configuration;
import  org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;


import org.zuinnote.cloudbigdata.configmanager.ConfigManagerInterface;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

@Autowired
private ConfigManagerInterface configManager;



	/**
	* Configure a message broker for web socket message, so we can interact more loosely coupled with the backend
	*
	*/
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		if (new String("embedded").equals(configManager.getValue("messaging.stomp.type"))) {
			String destinations = configManager.getValue("messaging.stomp.broker.register.destinations");
			String[] destinationArray = destinations.split(",");
			config.enableSimpleBroker(destinationArray);
		} else { //== extern
			
			String destinations = configManager.getValue("messaging.stomp.broker.register.destinations");
			String[] destinationArray = destinations.split(",");
			 StompBrokerRelayRegistration myStompBrokerRelayRegistration = config.enableStompBrokerRelay(destinationArray);
	 		myStompBrokerRelayRegistration.setRelayHost(configManager.getValue("messaging.stomp.relay-host"));
			myStompBrokerRelayRegistration.setRelayPort(new Integer(configManager.getValue("messaging.stomp.relay-port")).intValue());			
			myStompBrokerRelayRegistration.setClientLogin(configManager.getValue("messaging.stomp.clientlogin"));
			myStompBrokerRelayRegistration.setClientPasscode(configManager.getValue("messaging.stomp.clientpasscode"));
			myStompBrokerRelayRegistration.setSystemHeartbeatReceiveInterval(new Long(configManager.getValue("messaging.stomp.heartbeat-receive-interval")).longValue());
			myStompBrokerRelayRegistration.setSystemHeartbeatSendInterval(new Long(configManager.getValue("messaging.stomp.heartbeat-send-interval")).longValue());
			myStompBrokerRelayRegistration.setSystemLogin(configManager.getValue("messaging.stomp.systemlogin"));
			myStompBrokerRelayRegistration.setSystemPasscode(configManager.getValue("messaging.stomp.systempasscode"));
		}
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/cloudbigdata/ws").withSockJS();
		registry.addEndpoint("/cloudbigdata/wswebrtc").withSockJS();
	}




}
