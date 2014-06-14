Design
======

This page documents the design of the architecture of the application.
We do not include a full Big Data Lambda Architecture, but the given one can easily be extended to such an architecture.

Architecture
============

Most of the components of the applications are open source software technologies, which are only configured and little needs to be programmed. The currently implemented/configured components are highlighted green in the following architecture picture.


Dynamic View
============
Authentication
* LDAP
* OpenID / LDAP

Authorization
* LDAP

Data
* Retrieve Customer List
* Create/Update/Delete Customer

WebSocket
* Send Message
* Receive Message

Configuration
* Create a Configuration Manager
* Read Configuration
* Watch Configuration

User Manager
* Create a User Manager
* Create User
* Find a User
* Get group memberships of a user

Scenarios
=========

Authentication
* User can authenticate with a username and password stored on the LDAP server
* User can authenticate via an OpenID provider

Authorization
* User can see all pages except admin page if not logged in as admin user
* User can see all pages if logged in as admin user

Big Data (Master Data)
* User sees a list of customers
* User can add/modify/delete customers

Big Data (Analytics)
* User selects a data set and visualization type in the browser and it is rendered and send to the user within 1 second
* User selects in the browser an existing R script that is executed to the server and the answer is displayed in the user's browser

Big Data (Hadoop Core)
* User selects a Hadoop Job and starts it
* User browse Hadoop File System in the browser

Big Data (Solr)
* User searches the existing documents index by Solr

Big Data Stream (Spark)
* User sees a list of available sensors
* User can select and start a Spark Streaming Program that processes sensor data delivered via WebSockets to the RabbitMQ bus

WebRTC
* User can make a WebRTC video call with another user
* User sees available WebRTC calls
