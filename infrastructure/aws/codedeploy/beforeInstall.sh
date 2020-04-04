#!/bin/bash

cd ~
if [ -f "*.jar" ]
then
sudo rm -rf /*.jar
sudo rm -rf /opt/tomcat/*.jar
sudo rm -rf /opt/tomcat/*.war
sudo rm -rf /home/centos/*/csye6225.log
pid=$(ps aux | grep "java -jar" | grep "root" | awk '{print $2}')
sudo kill -9 "$pid" 
sudo systemctl stop amazon-cloudwatch-agent.service
fi