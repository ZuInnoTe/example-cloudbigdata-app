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


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


import java.util.Iterator;
import java.util.Properties;

/**
*
* Implements a file based configuration manager (@see AbstractConfigManager)
*
*
*/

public class ConfigManagerImplLocal extends AbstractConfigManager {
	private static Logger log = LogManager.getLogger(ConfigManagerImplLocal.class.getName());
	// get local properties from file
	public ConfigManagerImplLocal(Properties localProperties) {
		this.getConfigurationLock();
		// go through properties and add them to the hash map
		Iterator<String> propertyKeyIterator = localProperties.stringPropertyNames().iterator();
		while (propertyKeyIterator.hasNext()) {
			String nextPropertyKey = propertyKeyIterator.next();
				// remove "local." prefix
				String currentPropertyKey=null;
				if (nextPropertyKey.startsWith("local.")) {
					 currentPropertyKey = nextPropertyKey.substring(new String("local.").length());
				} else {
					currentPropertyKey=nextPropertyKey;
				}
				String currentPropertyValue = (String)localProperties.get(nextPropertyKey);
				this.setValue(currentPropertyKey,currentPropertyValue);
		}
		this.releaseConfigurationLock();
	}
}
