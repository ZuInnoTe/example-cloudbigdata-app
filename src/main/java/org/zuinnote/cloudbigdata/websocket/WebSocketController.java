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

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/* This controller implements a simple websocket interface to the web application. All messages are send to messaging service so that they can be dealt with asynchronously in the background */

@Controller
public class WebSocketController {

    /** Websocket/Sock.js endpoint (see also configuration) **/
    @MessageMapping("/cloudbigdata/ws")
    /** Message will be sent to a message-driven middleware **/
    @SendTo("/topic/greetings")
    public HelloResponse hello(HelloMessage message) throws Exception {
        Thread.sleep(3000); // simulated delay
        return new HelloResponse("Hello, " + message.getName() + "!");
    }

}
