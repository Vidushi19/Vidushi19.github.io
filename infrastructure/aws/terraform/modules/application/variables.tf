variable "vpcop_id"{}

//variable "subnetIds"{}

//variable "ec2CodplyRoleName"{}
variable "subnets"{}
variable "certiArn"{}

variable "vpc-cluster-name" {
  description = "This name will be used for all the resources created in AWS"
  type    = "string"
  //default = "vpc-circleci-one"
}

variable "VPC_ciderBlock"{
  description = "Cider block to be used for VPC creation"
  type    = "string"
  //default = "10.0.0.0/16"
}

variable "ami_name" {
description = "AMI Name to be used for EC2 Security group creation"
  type    = "string"
  //default = "csye6225_1573140630" 
}

variable "ami_id" {
description = "AMI Id to be used for EC2 Security group creation"
  type    = "string"
   // default="ami-00f2221295c193b21"

}

variable "ami_key_pair_name" {
description = "AMI Key-Pair Name to be used for EC2 Security group creation"
  type    = "string"
    //default="csye6225_key"

}

variable "instance_type"{
description = "Instance Type to be used for EC2 Instance creation"
  type    = "string"
    default="t2.micro"

}

variable "volume_size"{
  description = "Instance Volume Size to be used for EC2 Instance creation"
  type    = "string"
  default="20"
}

variable "volume_type"{
  description = "Instance Volume Type to be used for EC2 Instance creation"
  type    = "string"
  default="gp2"
}

variable "domain-name"{
  description = "Domain Name to be used for S3 bucket creation"
  type    = "string"
  //default="dev.csye6225mrinal.me"
}
variable "account_id"{
  type    = "string"
  //default="227417268421"
}
variable "region"{
  type    = "string"
  default="us-east-1"
}
variable "profile" {
  type= "string"
  default = "dev"
}
//variable "ec2CodplyRole" {}