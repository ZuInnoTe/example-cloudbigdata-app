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

/**
* Provides basic methods for configuration management
*
*
*/

package org.zuinnote.cloudbigdata.configmanager;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.Iterator;

import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractConfigManager implements ConfigManagerInterface {
private static ReentrantLock lock; 
private static Map<String,String> configValues; 
private static Map<String, List<ConfigChangeInterface>> watcherMap;
	
	public AbstractConfigManager() {
		this.lock=new ReentrantLock();
		// config values for application
		configValues = new HashMap<String,String>();
		// set up watchers for configValues
		watcherMap = Collections.synchronizedMap(new HashMap<String, List<ConfigChangeInterface>>());
	}

	public String getValue(String key) {
		// if in process of a reconfiguration we have to wait
		String result="";
 		lock.lock();  // block until potential reconfiguration has finished
     		try {
      			result=configValues.get(key);
     		} finally {
       			lock.unlock();
     		}
		return result;
	}

	public boolean watchKey(String key, ConfigChangeInterface objToNotify) {
		// if in process of a reconfiguration we have to wait
 		lock.lock();  // block until potential reconfiguration has finished
     		try {
			if (configValues.containsKey(key)==false)  return false;
			// key is in configValues so we can add a watcher
			synchronized(watcherMap) {
				List<ConfigChangeInterface> keyList= watcherMap.get(key);
				// check if watcherList for key already exists
				if (keyList==null) {
					keyList = new ArrayList<ConfigChangeInterface>();
					watcherMap.put(key, keyList);
			}
			// check if watcher already exists
			if (keyList.contains(objToNotify)==false) {
				keyList.add(objToNotify);
			}
		}
		} finally {
       			lock.unlock();
     		}
		return true;
	}

	public void unWatchKey(String key, ConfigChangeInterface objToNotify) {
		synchronized(watcherMap) {
			List<ConfigChangeInterface> keyList= watcherMap.get(key);
			// check if watcherList for key already exists
			if (keyList!=null) {
				keyList.remove(objToNotify);
			}
			if (keyList.size()==0) watcherMap.remove(key);
		}
	}

	protected void setValue(String key, String value) {
		// if in process of a reconfiguration we have to wait
 		lock.lock();  // block until potential reconfiguration has finished
     		try {
			if (value==null) {
				configValues.remove(key);
			} else {
      				configValues.put(key,value);
			}
     		} finally {
       			lock.unlock();
			// inform watchers
			synchronized(watcherMap) { // synchronized because watchers can be added at any time
				List<ConfigChangeInterface> watchers = watcherMap.get(key);
				if (watchers!=null) {
			 		Iterator<ConfigChangeInterface> watchersIterator = watchers.iterator();
					while (watchersIterator.hasNext()) {
						ConfigChangeInterface watcherItem = watchersIterator.next();
						watcherItem.valueChanged(key,value);
					} 
				}
			}
			// remove watchers
			synchronized(watcherMap) {
				watcherMap.remove(key);
			}
     		}
	}

	protected void getConfigurationLock() {
		lock.lock();
	}

	protected void releaseConfigurationLock() {
		lock.unlock();
	}

}
