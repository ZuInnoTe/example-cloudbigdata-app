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

package org.zuinnote.cloudbigdata;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

import javax.sql.DataSource; 

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.core.LdapTemplate;


import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.zuinnote.cloudbigdata.jpadata.Customer;
import org.zuinnote.cloudbigdata.jpadata.CustomerRepository;
import org.zuinnote.cloudbigdata.configmanager.ConfigManagerFactory;
import org.zuinnote.cloudbigdata.configmanager.ConfigManagerInterface;
import org.zuinnote.cloudbigdata.usermanager.OpenIDUserDetailsService;
import org.zuinnote.cloudbigdata.usermanager.UserManagerInterface;
import org.zuinnote.cloudbigdata.usermanager.UserManagerFactory;

@Configuration
@EnableJpaRepositories
@ComponentScan
@EnableAutoConfiguration
public class Application {

private static Logger log = Logger.getLogger(Application.class.getName());
    public static void main(String[] args) {
	// Start Spring-Application (for testing purposes)
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
	
	// Load test data for testing purposes
        CustomerRepository repository = context.getBean(CustomerRepository.class);

        // save a couple of customers
        repository.save(new Customer("Joe", "Doe", "USA"));
        repository.save(new Customer("Foo", "Bar", "USA"));
        repository.save(new Customer("Martha", "Mustermann", "Germany"));
        repository.save(new Customer("Angela", "Merkel", "Germany"));
	// just for checking that your application can handle proper UTF-8 data
        repository.save(new Customer("Jörn", "Franke", "Germany"));
    }

    /*** Configure in-memory database for testing purposes ***/
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(H2).build();
    }

    /** Configuration bean that can be used throughout the application for its configuration **/
    @Bean
    public ConfigManagerInterface configManager() {
	return ConfigManagerFactory.getConfigManager();
    }

    @Bean
    public OpenIDUserDetailsService openIDUserDetailsService() {
	return new OpenIDUserDetailsService();
    }

    @Bean
    public UserManagerInterface userManager() {
	return UserManagerFactory.getUserManager();
    }

    @Bean
    public LdapContextSource ldapContextSource() {
	// if configuration == embedded, use default shipped ldif
	if (new String("embedded").equals(configManager().getValue("ldap.type"))) {
		log.info("Using embedded LDAP server");
		LdapContextSource lcs = new LdapContextSource();
		lcs.setUrl("ldap://localhost:33389/");
		lcs.setBase(configManager().getValue("ldap.base"));
		try {
			lcs.afterPropertiesSet();
		} catch (Exception e) {
			log.info(e);
		}
		
		return lcs;
	}
	// if configuration == external
	log.info("Using external LDAP server");
	LdapContextSource lcs = new LdapContextSource();
	lcs.setUrl(configManager().getValue("ldap.url"));
	lcs.setBase(configManager().getValue("ldap.base"));
		try {
			lcs.afterPropertiesSet();
		} catch (Exception e) {
			log.info(e);
		}
		 
	return lcs;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
	LdapTemplate lt = new LdapTemplate(ldapContextSource());
	return lt;
    }


    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan("org.zuinnote.cloudbigdata.jpadata");
        return lef;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.H2);
        return hibernateJpaVendorAdapter;
    }

    
   
}
