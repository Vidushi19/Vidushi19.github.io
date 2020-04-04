# CSYE 6225 - Fall 2019

## File information and Steps to run the code are as follows:

1: csye6225-aws-cf-create-stack : Will create the instance of the stack in the cloud environment
Steps: 
1: sh the file
2: Enter the stack name, json file with complete address, region, CIDRBlock, Subnet1, Subnet2, subnet3, VPCName, Profile

Wait for the process, it will create the instance in the cloud. 

2: Terminate 
  csye6225-aws-cf-terminate-stack: will delete the created instance from the AWS console. 

  1: sh the file to initiate
  2: Enter the stack name to delete 

Wait for the process to complete 

Note: You should have the Modification credentials to create or terminate an instance. 

Also, 

csye6225-cf-networking.json is the cloud formation position json which will contain the information for validation of sh files.
