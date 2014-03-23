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

package org.zuinnote.cloudbigdata.controller;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import  org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.zuinnote.cloudbigdata.jpadata.Customer;
import org.zuinnote.cloudbigdata.jpadata.CustomerRepository;
import org.zuinnote.cloudbigdata.jpadata.converter.jdatajson.JTableCreateActionResponse;
import org.zuinnote.cloudbigdata.jpadata.converter.jdatajson.JTableUpdateActionResponse;
import org.zuinnote.cloudbigdata.jpadata.converter.jdatajson.JTableDeleteActionResponse;
import org.zuinnote.cloudbigdata.jpadata.converter.jdatajson.JTableListActionResponse;

import javax.servlet.http.HttpServletRequest;

@Controller
public class JPAController {
    private @Autowired ConfigurableApplicationContext appContext;

   @RequestMapping(value="/cloudbigdata/jpa/createcustomer", method=RequestMethod.POST)
    public @ResponseBody JTableCreateActionResponse<Customer> jpaCreateCustomer(@RequestParam(value="firstName", required=true) String firstName, @RequestParam(value="lastName", required=true) String lastName, @RequestParam(value="location", required=true) String location,  Model model) {
	CustomerRepository repository =appContext.getBean(CustomerRepository.class);
	Customer newCustomer = new Customer(firstName, lastName, location);
	Customer savedCustomer=repository.save(newCustomer);
	JTableCreateActionResponse<Customer> resp = new JTableCreateActionResponse<Customer>();
	resp.Result="OK";
	resp.Record=savedCustomer;
        return resp;
    }

   @RequestMapping(value="/cloudbigdata/jpa/updatecustomer", method=RequestMethod.POST)
    public @ResponseBody JTableUpdateActionResponse<Customer> jpaUpdateCustomer(@RequestParam(value="id", required=true) long id, @RequestParam(value="firstName", required=true) String firstName, @RequestParam(value="lastName", required=true) String lastName, @RequestParam(value="location", required=true) String location,  Model model) {
	CustomerRepository repository =appContext.getBean(CustomerRepository.class);
	JTableUpdateActionResponse<Customer> resp = new JTableUpdateActionResponse<Customer>();
	if (repository.exists(id)==true) {
		Customer updateCustomer = new Customer(id, firstName, lastName, location);
		Customer savedCustomer=repository.save(updateCustomer);
		resp.Result="OK";
		resp.Record=savedCustomer;
	} else {
		resp.Result="ERROR";
		resp.Message="Customer with id "+id+" does not exist";
	}
        return resp;
    }

   @RequestMapping(value="/cloudbigdata/jpa/deletecustomer", method=RequestMethod.POST)
    public @ResponseBody JTableDeleteActionResponse<Customer> jpaDeleteCustomer(@RequestParam(value="id", required=true) long id,  Model model) {
	CustomerRepository repository =appContext.getBean(CustomerRepository.class);
	JTableDeleteActionResponse<Customer> resp = new JTableDeleteActionResponse<Customer>();
	// check if exists
	if (repository.exists(id)==true) {
	 try {
		repository.delete(id);
		resp.Result="OK";
	 } catch(IllegalArgumentException e) {
			resp.Result="ERROR";
			resp.Message=e.getMessage();
	 } 
	} else {
		resp.Result="ERROR";
		resp.Message="Customer with id "+id+" does not exist";
	}
	
        return resp;
    }

   @RequestMapping(value="/cloudbigdata/jpa/listcustomer", method=RequestMethod.POST)
    public @ResponseBody JTableListActionResponse<Customer>  jpaListCustomer(@RequestParam(value="jtStartIndex", required=true) int jtStartIndex, @RequestParam(value="jtPageSize", required=true) int jtPageSize, Model model) {
	// do not overload database  and do not allow division by zero exception
	if ((jtPageSize>200) || (jtPageSize<1)) {
		JTableListActionResponse<Customer> errorResp=new JTableListActionResponse<Customer>();
		errorResp.Result="ERROR";
		errorResp.Message="Invalid page size";
		return errorResp;
	}
	// list customers in database
        CustomerRepository repository = appContext.getBean(CustomerRepository.class);
	Pageable jTablePagination = new PageRequest(jtStartIndex/jtPageSize, jtPageSize);
	JTableListActionResponse<Customer> resp = new JTableListActionResponse<Customer>();
	resp.Result="OK";
	resp.Records=repository.findAll(jTablePagination).getContent();
	resp.TotalRecordCount=repository.count();
	return resp;
    }




}
