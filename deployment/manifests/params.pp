# This class represents parameters for deploying the example big-data-cloud application
# Author: Jörn Franke
class examplecloudbigdataapp::params () {
	### select a database for deployment
	## currently supported: mysql or hbase (both for JPA)
	$database = "mysql"
}
