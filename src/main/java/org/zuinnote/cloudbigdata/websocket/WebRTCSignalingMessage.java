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

import java.util.Map;

/* This class describes a WebRTC signaling message */


public class WebRTCSignalingMessage {
    
    /** to avoid any incompabilities with different browsers etc. we put the whole JSON message generated by the browser's WebRTC implementation in one String **/
    private Map<String, Object> message; /** WebRTC data **/
    private String type; /** Used in the Java-Script application **/
    private String fromUserID; /** should be always filled with the correct principal **/
    private String toUserID; /** mandatory only for signaling messages **/
    private String room; /** chat room **/

    public Map<String, Object> getMessage() {
        return message;
    }

    public  String getType() {
	return type;
    }

   public String getFromUserID() {
	return fromUserID;
   }

   public void setFromUserID(String fromUserID) {
	this.fromUserID=fromUserID;
   }

   public String getToUserID() {
	return toUserID;
   }

   public String getRoom() {
	return room;
   }

}
