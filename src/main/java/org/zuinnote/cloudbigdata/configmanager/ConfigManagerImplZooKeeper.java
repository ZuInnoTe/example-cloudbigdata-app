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


import java.util.Properties;

/**
*
* Implements a ZooKeeper based configuration manager (@see AbstractConfigManager)
*
*
*/

public class ConfigManagerImplZooKeeper extends AbstractConfigManager {
	private static Logger log = LogManager.getLogger(ConfigManagerImplZooKeeper.class.getName());

	// you have to provide properties for ZooKeeper basic configuration
	public ConfigManagerImplZooKeeper(Properties zooKeeperProperties) {
		// read properties
		// create a new thread for communicating with zookeeper
		// create a watcher thread for the main zookeeper communication thread
	}

	/** This thread watches the ZooKeeper thread since it depends on other libraries out of our control and failure can be expected **/
	private class ZooKeeperCommunicationWatcherThread extends Thread {
		
		public void run() {
			while(true) {
			}
		}
	}

	/** This thread is used for communicating with ZooKeeper **/
	private class ZooKeeperCommunicationThread extends Thread {
	
		public void run() {
			while (true) {
			}
		}
	}


}
