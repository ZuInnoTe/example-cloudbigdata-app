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

package org.zuinnote.cloudbigdata.jpadata;

/**
* Implements a simple Customer Entity using JPA
*
*/

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String firstName;
    private String lastName;
    private String location;

    protected Customer() {}

    public Customer(long id, String firstName, String lastName, String location) {
	this.id=id;
        this.firstName = firstName;
        this.lastName = lastName;
	this.location = location;
    }

    public Customer(String firstName, String lastName, String location) {
        this.firstName = firstName;
        this.lastName = lastName;
	this.location = location;
    }

    public long getId() {
	return this.id;
    }

    public String getFirstName() {
	return this.firstName;
    }

    public String getLastName() {
	return this.lastName;
    }

    public String getLocation() {
	return this.location;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s', location='%s']",
                id, firstName, lastName, location);
    }

}
