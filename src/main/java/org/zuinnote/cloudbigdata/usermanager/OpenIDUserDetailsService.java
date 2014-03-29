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

/****
** Example for an OpenID authorization service based on successful authentication by an OpenID provider
**
*
*/

package org.zuinnote.cloudbigdata.usermanager;

import  org.springframework.beans.factory.annotation.Autowired;
import java.util.Collection;

import org.apache.log4j.Logger;


import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import org.zuinnote.cloudbigdata.configmanager.ConfigManagerInterface;



 public class OpenIDUserDetailsService implements
         AuthenticationUserDetailsService<OpenIDAuthenticationToken> {


private static Logger log = Logger.getLogger(OpenIDUserDetailsService.class.getName());

@Autowired
private ConfigManagerInterface configManager;
@Autowired
private UserManagerInterface userManager;

     /***
	** load user details after successful OpenID authentication
	** add user to usermanager so we can assign further authorization rules (e.g. admin) 
     **/
     public UserDetails loadUserDetails(OpenIDAuthenticationToken token) throws UsernameNotFoundException {
	// check if user already exists 
	org.zuinnote.cloudbigdata.usermanager.User foundUser = userManager.findUser(new org.zuinnote.cloudbigdata.usermanager.User(token.getName(),configManager.getValue("ldap.openIDUsers"),"",""));
	if (foundUser!=null) {
		log.info("returning open id user");
		// check group memberships
		Collection<String> groupMemberShips=	userManager.getUserGroupMemberShip(foundUser);
		// if yes return openid user with permissions
		return new org.springframework.security.core.userdetails.User(foundUser.getUserID(), "NOTUSED", AuthorityUtils.createAuthorityList("ROLE_USER"));
	}	
  	// return user with minimum permission
	log.info("creating new open id user in user manager");
  	// if not add openid user to user manager with minimum permissions. The idea here is that you can authorize openid users for accessing different resources of the system
 	org.zuinnote.cloudbigdata.usermanager.User createdUser = userManager.createUser(new org.zuinnote.cloudbigdata.usermanager.User(token.getName(),configManager.getValue("ldap.openIDUsers"),"",""));
	if (createdUser!=null) {
         	return new org.springframework.security.core.userdetails.User(createdUser.getUserID(), "NOTUSED", AuthorityUtils.createAuthorityList("ROLE_USER"));
	}
	return null;
     }

     
 }
