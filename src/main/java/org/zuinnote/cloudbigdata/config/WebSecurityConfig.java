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

package org.zuinnote.cloudbigdata.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.http.HttpMethod;

import  org.zuinnote.cloudbigdata.security.OpenIDLDAPUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .authorizeRequests()
		// allow the followin urls without authentication (GET)
		.antMatchers("/cloudbigdata/logout*").permitAll()
		.antMatchers("/cloudbigdata/login*").permitAll()
		.antMatchers("/cloudbigdata/img/openid/*").permitAll();
// allow all other urls with authentication (GET)
	http
           .authorizeRequests().anyRequest().authenticated();
// allow form based login for in memory or ldap authentication
        http
            .formLogin()
                .defaultSuccessUrl("/cloudbigdata/main",true)
		.loginProcessingUrl("/cloudbigdata/loginnormal")
                .loginPage("/cloudbigdata/login")
		.failureUrl("/cloudbigdata/loginerror")
                .permitAll()
                .and()
                 .logout()
                    .deleteCookies("remove")
                    .invalidateHttpSession(true)
                    .logoutUrl("/cloudbigdata/logout")
		    .logoutSuccessUrl("/cloudbigdata/logoutsuccess");
// allow openid based login
 	http
             .authorizeRequests()
                 .antMatchers("/**").hasRole("USER")
                 .and()
  		.openidLogin()
                 .loginPage("/cloudbigdata/login")
		 .defaultSuccessUrl("/cloudbigdata/main",true)
	         .loginProcessingUrl("/cloudbigdata/loginopenid")
		 .failureUrl("/cloudbigdata/loginerror")
                 .permitAll()
		 .authenticationUserDetailsService(new OpenIDLDAPUserDetailsService());

    }

    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
	// check ldap
		authManagerBuilder
			.ldapAuthentication()
				.userDnPatterns("uid={0},ou=users,o=companyexample")
				.groupSearchBase("ou=exampleapp,o=companyexample")
				.contextSource()
					.ldif("classpath:example.ldif").root("dc=example,dc=com");
   		
 }
}
