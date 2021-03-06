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

package org.zuinnote.cloudbigdata.controller;

import org.zuinnote.cloudbigdata.configmanager.ConfigManagerInterface;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.security.core.userdetails.UserDetails;


import javax.servlet.http.HttpServletRequest;

/***
*
* This controller provides access to dynamic web pages of the web application leveraging the template engine
*
*/


@Controller
public class MainController {
    private @Autowired ConfigurableApplicationContext appContext;
    
    @Autowired
    private ConfigManagerInterface configManager;

    @RequestMapping("/cloudbigdata/main")
    public String main(@AuthenticationPrincipal UserDetails activeUser, @RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
	model.addAttribute("principalname", activeUser.getUsername());
        return "main";
    }

  
    @RequestMapping("/cloudbigdata/login")
    public String login(Model model) {
        return "login";
    }

   @RequestMapping("/cloudbigdata/loginerror")
    public String loginerror(Model model) {
        return "loginerror";
    }


    @RequestMapping("/cloudbigdata/jpa")
    public String jpa(Model model) {
        return "jpa";
    }

   @PreAuthorize("hasAuthority('ROLE_ADMINS')")
   @RequestMapping("/cloudbigdata/administrator")
    public String administrator(@AuthenticationPrincipal UserDetails activeUser, Model model) {
	model.addAttribute("principalname", activeUser.getUsername());
	model.addAttribute("authorities", activeUser.getAuthorities());
        return "administrator";
    }



   @RequestMapping("/cloudbigdata/websocket")
    public String websocket(Model model) {	
        return "websocket";
    }


   @RequestMapping("/cloudbigdata/webrtc")
    public String webrtc(Model model) {
	// provide some configuration information to the website
	model.addAttribute("webrtcTopic", configManager.getValue("messaging.stomp.webrtc.topic"));
	model.addAttribute("webrtcPrivateQueue", configManager.getValue("messaging.stomp.webrtc.privatequeue"));
        return "webrtc";
    }

    @RequestMapping("/cloudbigdata/logoutsuccess")
    public String logout(Model model) {
        return "logout";
    }


}
