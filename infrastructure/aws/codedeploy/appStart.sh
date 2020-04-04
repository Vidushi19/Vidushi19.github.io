#!/bin/bash

cd ~

sudo chmod 777 /opt/tomcat/bin/
#sudo chmod 777 /opt/tomcat/conf/Catalina
sudo chmod 777 /opt/tomcat/webapps/
# cd /
sudo chmod 777 /opt/tomcat/logs/
java -jar CloudAssignment2-0.0.1-SNAPSHOT.war
