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

/*
* Basic interface of a user manager
*
*
*/

public interface UserManagerInterface {

	/*
	* Create a user
	* 
	* @param theUser user details
	*
	* @return User with details if successful, null if not
	*/

	public User createUser(User theUser);

	/*
	* Find a user matching criteria
	* 
	* @param theUser user details
	*
	* @return User with details if successful, null if not
	*/


	public User findUser(User theUser);

	/*
	* Retrieve group membership of a user
	* 
	* @param theUser user details
	*
	* @return a collection of groups the user is member of
	*/
	public Collection<String> getUserGroupMemberShip(User theUser);

}
