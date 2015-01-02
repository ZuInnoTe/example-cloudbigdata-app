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

package org.zuinnote.cloudbigdata.websocket;

import org.zuinnote.cloudbigdata.configmanager.ConfigManagerInterface;

import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import  org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.Map;

/* This controller implements a simple websocket interface to the web application. All messages are send to messaging service so that they can be dealt with asynchronously in the background */

@Controller
public class WebSocketController {

   
   private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ConfigManagerInterface configManager;
    
    @Autowired
    public WebSocketController(SimpMessageSendingOperations messagingTemplate) {
	this.messagingTemplate=messagingTemplate;
    }

    /** Websocket/Sock.js endpoint (see also configuration) **/
    @MessageMapping("/cloudbigdata/ws")
    /** Message will be sent to a message-driven middleware **/
    @SendTo("/topic/greetings")
    public HelloResponse hello(HelloMessage message) throws Exception {
        Thread.sleep(3000); // simulated delay
        return new HelloResponse("Hello, " + message.getName() + "!");
    }



    /** Websocket/Sock.js endpoint for webrtc (see also configuration) **/
    @MessageMapping("/cloudbigdata/wswebrtc")
    /** Message will be sent to a topic or a private user queue depending on destination **/
    public void webrtc(Principal p, @Headers Map headers, WebRTCSignalingMessage message) throws Exception {
	if (p!=null) {
		/** set the sender on server side so nobody is able to impersonate other users **/
		message.setFromUserID(p.getName());
		/** forward it to all users if no destination user is set **/
		if ("".equals(message.getToUserID())) {
			messagingTemplate.convertAndSend(configManager.getValue("messaging.stomp.webrtc.topic"), message, headers);
		} else {
			/** forward it to the private queue of the destination user if it is a private signaling message **/
	 		messagingTemplate.convertAndSendToUser(message.getToUserID(), configManager.getValue("messaging.stomp.webrtc.privatequeue"), message, headers);
        	}
	}
    }



}
