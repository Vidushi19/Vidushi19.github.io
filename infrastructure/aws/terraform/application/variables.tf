variable "vpc-cluster-name" {
  description = "This name will be used for all the resources created in AWS"
  type    = "string"
}

variable "VPC_ciderBlock"{
  description = "Cider block to be used for VPC creation"
  type    = "string"
}

variable "ami_name" {
description = "AMI Name to be used for EC2 Security group creation"
  type    = "string"
}

variable "ami_id" {
description = "AMI Id to be used for EC2 Security group creation"
  type    = "string"

}

variable "ami_key_pair_name" {
description = "AMI Key-Pair Name to be used for EC2 Security group creation"
  type    = "string"

}

variable "instance_type"{
description = "Instance Type to be used for EC2 Instance creation"
  type    = "string"

}

variable "volume_size"{
  description = "Instance Volume Size to be used for EC2 Instance creation"
  type    = "string"
}

variable "volume_type"{
  description = "Instance Volume Type to be used for EC2 Instance creation"
  type    = "string"
}

variable "domain-name"{
  description = "Domain Name to be used for S3 bucket creation"
  type    = "string"
}
variable "account_id"{
  type    = "string"
}
variable "region"{
  type    = "string"
<<<<<<< HEAD
}
variable "profile" {
  type= "string"
=======
>>>>>>> b4c0685bc6af0d1e5dc208d556fd578f0c9bd713
}
variable "profile" {
  type= "string"
}