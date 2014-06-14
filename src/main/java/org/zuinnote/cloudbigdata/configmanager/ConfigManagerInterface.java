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

package org.zuinnote.cloudbigdata.configmanager;

/**
* Interface for a configuration manager
*
*
*/

public interface ConfigManagerInterface {

	/*
	* read a value from the configuration
	*/

	public String getValue(String key);

	/*
	* watch key for configuration changes and inform object implementing @ConfigChangeInterface
	*/
	public boolean watchKey(String key, ConfigChangeInterface objToNotify);

	/*
	* stop watching key for configuration changes and stop informing object implementing @ConfigChangeInterface
	*/
	public void unWatchKey(String key, ConfigChangeInterface objToNotify);	
	
}
