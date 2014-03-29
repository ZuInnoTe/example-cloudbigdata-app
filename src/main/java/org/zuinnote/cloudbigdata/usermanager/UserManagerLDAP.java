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

package org.zuinnote.cloudbigdata.usermanager;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.log4j.Logger;
import  org.springframework.beans.factory.annotation.Autowired;


import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.zuinnote.cloudbigdata.configmanager.ConfigManagerInterface;

public class UserManagerLDAP implements UserManagerInterface {
	
	private static Logger log = Logger.getLogger(UserManagerLDAP.class.getName());
	@Autowired
	private ConfigManagerInterface configManager;
	@Autowired
	private LdapTemplate ldapTemplate;

	public User createUser(User theUser) {
		String cleanedUserID= cleanUserID(theUser.getUserID());
      		Attributes attr = new BasicAttributes();
 		BasicAttribute obclattr = new BasicAttribute("objectclass");
      		obclattr.add("top");
		obclattr.add("inetOrgPerson");
        	obclattr.add("person");
       		obclattr.add("organizationalPerson");
        	attr.put(obclattr);
		attr.put("uid", cleanedUserID);
     		attr.put("sn", cleanedUserID);
		DistinguishedName userDN = new DistinguishedName("uid="+cleanedUserID+","+theUser.getType());
		log.debug(userDN);
    		ldapTemplate.bind(userDN, null, attr);
		return new User(cleanedUserID, theUser.getType(), theUser.getFirstName(), theUser.getLastName());
	}
	
	public User findUser(User theUser) {
		
		List foundUserList=ldapTemplate.search(
         	"","uid="+cleanUserID(theUser.getUserID()),new ContextMapper() {
			
           	 	public Object mapFromContext(Object ctx) {
				DirContextAdapter theCtx = (DirContextAdapter)ctx;
				String currentFirstName="";
				if (theCtx.attributeExists("firstName")) {
		 			currentFirstName=theCtx.getStringAttribute("firstName");
				}
				String currentLastName="";
				if (theCtx.attributeExists("lastName")) {
		 			currentLastName=theCtx.getStringAttribute("lastName");
				}
				String currentType="";
				//remove uid
				String currentDN=theCtx.getDn().toString();
				currentType=currentDN.substring(currentDN.indexOf(",")+1);
				if (theCtx.attributeExists("uid")==false) return null;
				String currentUid=theCtx.getStringAttribute("uid");
				User currentFoundUser=new User(currentUid,currentType,currentFirstName,currentLastName);
				return currentFoundUser;
            		}
                });
		if (foundUserList.size()>0) {
			return (User)foundUserList.get(0);
		}
		return null; 
	}

	
	public Collection<String> getUserGroupMemberShip(User theUser) {
	List foundGroupList=ldapTemplate.search(
        	"","uniqueMember=uid="+cleanUserID(theUser.getUserID())+","+theUser.getType()+","+configManager.getValue("ldap.base"),new ContextMapper() {
			
           	 	public Object mapFromContext(Object ctx) {
				DirContextAdapter theCtx = (DirContextAdapter)ctx;
				String currentGroupName="";
				if (theCtx.attributeExists("cn")) {
		 			currentGroupName=theCtx.getStringAttribute("cn");
				}
				if (theCtx.getDn().toString().contains(configManager.getValue("ldap.groupSearchBase"))) {
					// consider only groups for this app
					return currentGroupName;
	
				}
				return null;
				
            		}
                });
		HashSet<String> resultSet = new HashSet();
		Iterator resultListIterator = foundGroupList.iterator();
		while (resultListIterator.hasNext()) {
			Object currentItem =resultListIterator.next();
			if (currentItem!=null) {
				resultSet.add((String)currentItem);
			}
		}
		return resultSet;
	}


	private String cleanUserID(String userID) {
		return userID.replaceAll("[^a-zA-Z0-9]+","");
	}

}
