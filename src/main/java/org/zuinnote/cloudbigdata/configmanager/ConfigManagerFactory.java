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
*
* Creates objects for configuration management depending on the desired configuration method (local or zookeeper)
*
*/

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.Properties;

public class ConfigManagerFactory {
	public static final String ENV_VAR = "EXAMPLE_CLOUDBIGDATA_CONF";
	private static Logger log = Logger.getLogger(ConfigManagerFactory.class.getName());
	private static File configFile;

	private static ConfigManagerInterface currentConfigManager=null;

	public static ConfigManagerInterface getConfigManager() {
		log.debug("getting Config Manager");
		if (currentConfigManager!=null) {
			return currentConfigManager;
		}
		if (System.getenv().get(ENV_VAR)==null) {
			log.error("Cannot find configuration file in environment variable \""+ENV_VAR+"\"");
		}
		// read config file
		configFile=new File(System.getenv().get(ENV_VAR));
		if (configFile.exists()==false) {
			log.error("Configuration file \""+System.getenv().get(ENV_VAR)+"\" does not exist");
		}
		// properties
			Properties configProperties = new Properties();
		try {
			configProperties.load(new FileInputStream(configFile));
			String propertyConfigurationsource=configProperties.getProperty("configurationsource");
			if ((propertyConfigurationsource==null) || !((propertyConfigurationsource.equals("local")) | (propertyConfigurationsource.equals("zookeeper")))) {
				log.error("Error: Invalid configurationsource in configuration file");
			} else if (propertyConfigurationsource.equals("local")) {
				// use local configuration
				log.info("Using local (file-based) configuration");
				// getting local properties
				Properties localProperties = new Properties();
				Iterator<String> propertyKeyIterator = configProperties.stringPropertyNames().iterator();
				while (propertyKeyIterator.hasNext()) {
					String nextPropertyKey = propertyKeyIterator.next();
					if (nextPropertyKey.startsWith("local.")) {
						String currentPropertyValue = (String)configProperties.get(nextPropertyKey);
						localProperties.setProperty(nextPropertyKey,currentPropertyValue);
					}
				}
				currentConfigManager=new ConfigManagerImplLocal(localProperties);
				return currentConfigManager;
			} else if (propertyConfigurationsource.equals("zookeeper")) {
				// use zookeeper configuration
				log.info("Using zookeeper (distributed) configuration");
				// getting zookeeper properties
				Properties zookeeperProperties = new Properties();
				Iterator<String> propertyKeyIterator = configProperties.stringPropertyNames().iterator();
				while (propertyKeyIterator.hasNext()) {
					String nextPropertyKey = propertyKeyIterator.next();
					if (nextPropertyKey.startsWith("zookeeper.")) {
						String currentPropertyValue = (String)configProperties.get(nextPropertyKey);
						zookeeperProperties.setProperty(nextPropertyKey,currentPropertyValue);
					}
				}
				currentConfigManager=new ConfigManagerImplZooKeeper(zookeeperProperties);
				return currentConfigManager;
			}
			
		} catch (IOException e) {
			log.error("Problems loading property file");
			log.error(e);
		}
	     return null;
	}

}
