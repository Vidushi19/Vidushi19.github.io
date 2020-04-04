#!/bin/bash

sudo systemctl stop tomcat.service
sudo rm -rf /opt/tomcat/webapps/docs /opt/tomcat/webapps/examples /opt/tomcat/webapps/host-manager /opt/tomcat/webapps/manager /opt/tomcat/webapps/CloudAssisgnment2
sudo chown tomcat:tomcat /opt/tomcat/webapps/Cloud*.war

#clean log files
#sudo rm -rf /opt/tomcat/logs/catalina*
#sudo rm -rf /opt/tomcat/logs/*.log
#sudo rm -rf /opt/tomcat/logs/*.txt


sudo cp /home/centos/vars.sh /opt/tomcat/webapps/.
sudo cp /home/centos/*-SNAPSHOT.jar .
sudo chmod +x ./vars.sh
sudo ./vars.sh >> /home/centos/csye6225.log 2>&1 &
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/centos/cloudwatch-config.json -s