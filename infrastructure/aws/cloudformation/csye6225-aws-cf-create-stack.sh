#!/bin/bash
stackName=$1
templateFile=$2
vpcName=$3
profile=$4
awsRegion=$5
cidrBlock=$6
SUBNET_BLOCK1=$7
SUBNET_BLOCK2=$8
SUBNET_BLOCK3=$9
Availability_Zone1="${awsRegion}a"
Availability_Zone2="${awsRegion}b"
Availability_Zone3="${awsRegion}c"

aws cloudformation create-stack --profile ${profile} --stack-name ${stackName} --template-body ${templateFile} --parameters ParameterKey=VpcName,ParameterValue=${vpcName} ParameterKey=CidrBlock,ParameterValue=${cidrBlock} \
    ParameterKey=SubnetBlock1,ParameterValue=${SUBNET_BLOCK1} ParameterKey=SubnetBlock2,ParameterValue=${SUBNET_BLOCK2} ParameterKey=SubnetBlock3,ParameterValue=${SUBNET_BLOCK3} ParameterKey=AvailabilityZone1,ParameterValue=${Availability_Zone1} \
    ParameterKey=AvailabilityZone2,ParameterValue=${Availability_Zone2} ParameterKey=AvailabilityZone3,ParameterValue=${Availability_Zone3}
   
aws cloudformation wait stack-create-complete --stack-name ${STACK_NAME} --profile ${PROFILE}

if [ $? != 0 ]; then
    echo "Stack creation failed"
else
    echo "Stack creation successful"
fi        
