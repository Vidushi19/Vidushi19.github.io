data "aws_availability_zones" "available" {}
provider "aws" {
  region = "${var.region}"
  profile="${var.profile}"
}
resource "aws_vpc" "vpc_tf" {
	cidr_block = "${var.VPC_ciderBlock}"
	instance_tenancy     = "default"
	enable_dns_support   = true
	enable_dns_hostnames = true
	tags = "${
    map(
		"Name", "${var.vpc-cluster-name}",
    )
  }"
}

resource "aws_subnet" "subnet_tf" {
	count = 3
	availability_zone = "${data.aws_availability_zones.available.names[count.index]}"
	cidr_block = "10.0.${count.index}.0/24"
	vpc_id = "${aws_vpc.vpc_tf.id}"
	tags = "${
	map(
		"Name","${var.vpc-cluster-name}",
    )
  }"
}
resource "aws_db_subnet_group" "dbSubnetGroup" {
<<<<<<< HEAD
  name       = "main"
=======
  name       = "main2"
>>>>>>> b4c0685bc6af0d1e5dc208d556fd578f0c9bd713
  subnet_ids = ["${aws_subnet.subnet_tf[1].id}", "${aws_subnet.subnet_tf[2].id}"]
  tags = {
    Name = "My DB subnet group"
  }
}


resource "aws_internet_gateway" "ig_tf" {
	vpc_id = "${aws_vpc.vpc_tf.id}"
	tags = "${
	map(
		"Name","${var.vpc-cluster-name}IG",
	)
	}"
}

resource "aws_route_table" "rt_tf" {
	vpc_id = "${aws_vpc.vpc_tf.id}"
	route {
		cidr_block = "0.0.0.0/0"
		gateway_id = "${aws_internet_gateway.ig_tf.id}"
  }
}

resource "aws_route_table_association" "rtAsso_tf" {
	count = 3
	subnet_id = "${aws_subnet.subnet_tf.*.id[count.index]}"
	route_table_id = "${aws_route_table.rt_tf.id}"
}

 # * * * * * * * * * * * * * Security group creation * * * * * * * * * * * * * *

#EC2 instance creation
resource "aws_instance" "ec2-instance" {
	count=1
	ami = "${var.ami_id}"
	instance_type = "${var.instance_type}"
	key_name = "${var.ami_key_pair_name}"
	security_groups = ["${aws_security_group.application.id}"]
	subnet_id = "${aws_subnet.subnet_tf[0].id}"
	disable_api_termination = "false"
	root_block_device {
		volume_size = "${var.volume_size}"
		volume_type = "${var.volume_type}"
	}
	tags = {
    Name = "myEC2Instance"
  	}
	depends_on = [
    	aws_db_instance.RDS,]
	iam_instance_profile = "${aws_iam_instance_profile.test_profile.name}"
	user_data = "${templatefile("userdata.sh",
		{
			s3_bucket_name = "${aws_s3_bucket.bucket.bucket}",
			aws_db_endpoint = "${aws_db_instance.RDS.endpoint}",
			aws_db_name = "${aws_db_instance.RDS.name}",
			aws_db_username = "${aws_db_instance.RDS.username}",
			aws_db_password = "${aws_db_instance.RDS.password}",
			aws_region = "${var.region}",
			aws_profile = "${var.profile}"
		})}"
	}
#Security group for EC2 instance created
resource "aws_security_group" "application" {
	name = "Application security group"
	description = "Allow traffic for Webapp"
	vpc_id = "${aws_vpc.vpc_tf.id}"
	ingress {
		cidr_blocks = ["0.0.0.0/0"]
		from_port = 22
		to_port = 22
		protocol = "tcp"
	}
	ingress {
		cidr_blocks = ["0.0.0.0/0"]
		from_port = 80
		to_port = 80
		protocol = "tcp"
	}
	ingress {
		cidr_blocks = ["0.0.0.0/0"]
		from_port = 443
		to_port = 443
		protocol = "tcp"
	}
	ingress {
		cidr_blocks = ["0.0.0.0/0"]
		from_port = 8080
		to_port = 8080
		protocol = "tcp"
	}
	egress {
		cidr_blocks = ["0.0.0.0/0"]
	    from_port = 0
	   	to_port = 0
	    protocol = "-1"
	}
}
#Creating RDS instances
resource "aws_db_instance" "RDS"{
	name = "csye6225"
	allocated_storage = 20
	engine = "mysql"
	storage_type = "gp2"
	instance_class = "db.t2.medium"
	multi_az = "false"
	identifier_prefix = "csye6225-fall2019-"
	port = "3306"
	username = "root"
	password = "Admit$18"
	publicly_accessible = "true"
	skip_final_snapshot = "true"
	vpc_security_group_ids = ["${aws_security_group.database.id}"]
	db_subnet_group_name = "${aws_db_subnet_group.dbSubnetGroup.name}"
}
#creating database security group
resource "aws_security_group" "database" {
  name        = "Database Security Group"
  description = "Allow TLS inbound traffic"
  vpc_id = "${aws_vpc.vpc_tf.id}"
}
resource "aws_security_group_rule" "ingress-database-rule" {
    type = "ingress" 
    # TLS (change to whatever ports you need)
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    security_group_id = "${aws_security_group.database.id}"
  }
resource "aws_security_group_rule" "egress-database-rule" {
    type            ="egress"
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
   # cidr_blocks     = ["0.0.0.0/0"]
    security_group_id = "${aws_security_group.database.id}"
    source_security_group_id  = "${aws_security_group.application.id}"
}
resource "aws_eip" "ip-test-env" {
	count = 1
	instance = "${aws_instance.ec2-instance.*.id[count.index]}"
	vpc = true
}
#Creating key for S3 encryption

#Creating S3 Bucket for codedeploy
resource "aws_s3_bucket" "bucket" {
	bucket = "codedeploy.${var.domain-name}"
	acl    = "private"
	force_destroy = "true"
	tags = "${
      		map(
     		"Name", "${var.domain-name}",
    		)
  	}"
	lifecycle_rule {
	    id      = "log/"
	    enabled = true
		transition{
			days = 30
			storage_class = "STANDARD_IA"
		}
		expiration{
			days = 60
		}
	}
 
}
resource "aws_s3_bucket" "bucket_image" {
	bucket = "webapp.${var.domain-name}"
	acl    = "private"
	force_destroy = "true"
	tags = "${
      		map(
     		"Name", "${var.domain-name}",
    		)
  	}"
	server_side_encryption_configuration {
    		rule {
			apply_server_side_encryption_by_default {
				sse_algorithm = "aws:kms" 
			}
      		}
    	}
	lifecycle_rule {
	    id      = "log/"
	    enabled = true
		transition{
			days = 30
			storage_class = "STANDARD_IA"
		}
	}
}

# Creating DynamoDB table
resource "aws_dynamodb_table" "basic-dynamodb-table" {
	 name           = "csye6225"
	 hash_key       = "id"
	 read_capacity = "20"
	 write_capacity = "20"
	 attribute {
		name = "id"
		type = "S"
  	}}
data "aws_caller_identity" "current" {}

locals {
  user_account_id = "${data.aws_caller_identity.current.account_id}"
}

resource "aws_codedeploy_app" "csye6225-webapp" {
  name = "csye6225-webapp"
}
resource "aws_codedeploy_deployment_group" "csye6225-webapp-deployment" {
  app_name              = "${aws_codedeploy_app.csye6225-webapp.name}"
  deployment_group_name = "csye6225-webapp-deployment"
  deployment_config_name = "CodeDeployDefault.AllAtOnce"
  service_role_arn      = "${aws_iam_role.codedeploysrv.arn}"

  ec2_tag_filter {
    key   = "Name"
    type  = "KEY_AND_VALUE"
    value = "myEC2Instance"
  }
  deployment_style {
    deployment_option = "WITHOUT_TRAFFIC_CONTROL"
    deployment_type   = "IN_PLACE"
  }

  auto_rollback_configuration {
    enabled = true
    events  = ["DEPLOYMENT_FAILURE"]
  }

  alarm_configuration {
    alarms  = ["Deployment-Alarm"]
    enabled = true
  }
  }

# Creating IAM policies 
# Read instance from S3 Bucket
resource "aws_iam_role" "codedeploysrv" {
  name = "CodeDeployServiceRole"
  path = "/"
  force_detach_policies = "true"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal":
        {"Service": "codedeploy.amazonaws.com"},
      "Effect": "Allow",
	  "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "test-attach-codedeploysrv-policy" {
role      = "${aws_iam_role.codedeploysrv.name}"
policy_arn = "arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole"
}

resource "aws_iam_policy" "CodeDeploy-EC2-S3" {
  name        = "CodeDeploy-EC2-S3"
  path        = "/"
  description = "Allows EC2 instances to read data from S3 buckets"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "s3:*"
	  ],
      "Effect": "Allow",
      "Resource": [
		  "${aws_s3_bucket.bucket.arn}",
<<<<<<< HEAD
		  "${aws_s3_bucket.bucket.arn}/*",
      "${aws_s3_bucket.bucket_image.arn}",
		  "${aws_s3_bucket.bucket_image.arn}/*"
=======
		  "${aws_s3_bucket.bucket.arn}/*"
>>>>>>> b4c0685bc6af0d1e5dc208d556fd578f0c9bd713
		  ]
    }
  ]
}
EOF
}

resource "aws_iam_policy" "CircleCI-Upload-To-S3" {
  name        = "CircleCI-Upload-To-S3"
  path        = "/"
  description = "Allows CircleCI to upload artifacts from latest successful build to dedicated S3 bucket used by code deploy"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {"Action": [
                "s3:PutObject"
            ],
			"Effect": "Allow",
            "Resource": "${aws_s3_bucket.bucket.arn}"
			}
    ]
}
EOF
}
resource "aws_iam_policy" "CircleCI-Code-Deploy" {
  name        = "CircleCI-Code-Deploy"
  path        = "/"
  description = "Allows CircleCI to call CodeDeploy APIs to initiate application deployment on EC2 instances"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "codedeploy:RegisterApplicationRevision",
        "codedeploy:GetApplicationRevision"
      ],
	  "Effect": "Allow",
      "Resource":
        "arn:aws:codedeploy:${var.region}:${local.user_account_id}:application:${aws_codedeploy_app.csye6225-webapp.name}"
    },
    {
      "Effect": "Allow",
      "Action": [
        "codedeploy:CreateDeployment",
        "codedeploy:GetDeployment"
      ],
      "Resource": "${aws_s3_bucket.bucket.arn}"
  },
    {
      "Effect": "Allow",
      "Action": [
        "codedeploy:GetDeploymentConfig"
      ],
      "Resource": [
        "arn:aws:codedeploy:${var.region}:${local.user_account_id}:deploymentconfig:CodeDeployDefault.OneAtATime",
        "arn:aws:codedeploy:${var.region}:${local.user_account_id}:deploymentconfig:CodeDeployDefault.HalfAtATime",
        "arn:aws:codedeploy:${var.region}:${local.user_account_id}:deploymentconfig:CodeDeployDefault.AllAtOnce"
	  ]
    }
  ]
}
EOF
}
resource "aws_iam_policy" "circleci-ec2-ami" {
  name        = "circleci-ec2-ami"
  path        = "/"
  description = "Allows CircleCI to upload artifacts from latest successful build to dedicated S3 bucket used by code deploy"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ec2:AttachVolume",
				"ec2:AuthorizeSecurityGroupIngress",
				"ec2:CopyImage",
				"ec2:CreateImage",
				"ec2:CreateKeypair",
				"ec2:CreateSecurityGroup",
				"ec2:CreateSnapshot",
				"ec2:CreateTags",
				"ec2:CreateVolume",
				"ec2:DeleteKeyPair",
				"ec2:DeleteSecurityGroup",
				"ec2:DeleteSnapshot",
				"ec2:DeleteVolume",
				"ec2:DeregisterImage",
				"ec2:DescribeImageAttribute",
				"ec2:DescribeImages",
				"ec2:DescribeInstances",
				"ec2:DescribeInstanceStatus",
				"ec2:DescribeRegions",
				"ec2:DescribeSecurityGroups",
				"ec2:DescribeSnapshots",
				"ec2:DescribeSubnets",
				"ec2:DescribeTags",
				"ec2:DescribeVolumes",
				"ec2:DetachVolume",
				"ec2:GetPasswordData",
				"ec2:ModifyImageAttribute",
				"ec2:ModifyInstanceAttribute",
				"ec2:ModifySnapshotAttribute",
				"ec2:RegisterImage",
				"ec2:RunInstances",
				"ec2:StopInstances",
				"ec2:TerminateInstances"
            ],
            "Resource": "${aws_s3_bucket.bucket.arn}" 
        }
    ]
}
EOF
}
resource "aws_iam_role" "ec2CodplyRole" {
  name = "CodeDeployEC2ServiceRole"
  path = "/"
  force_detach_policies = "true"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal":
        {"Service": "ec2.amazonaws.com"},
      "Effect": "Allow",
	   "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_instance_profile" "test_profile" {
  name = "test_profile"
  role = "${aws_iam_role.ec2CodplyRole.name}"
}

resource "aws_iam_role_policy_attachment" "ec2CodplyRolePolicyAttach" {
  role       = "${aws_iam_role.ec2CodplyRole.name}"
  policy_arn = "${aws_iam_policy.CodeDeploy-EC2-S3.arn}"
}
resource "aws_iam_role_policy_attachment" "ec2CodplyRolePolicyAttach2" {
  role       = "${aws_iam_role.ec2CodplyRole.name}"
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}

resource "aws_iam_user_policy_attachment" "test-attach1" {
user      = "circleci"
policy_arn = "${aws_iam_policy.circleci-ec2-ami.arn}"
}
resource "aws_iam_user_policy_attachment" "test-attach2" {
user      = "circleci"
policy_arn = "${aws_iam_policy.CircleCI-Code-Deploy.arn}"
}
resource "aws_iam_user_policy_attachment" "test-attach3" {
user      = "circleci"
policy_arn = "${aws_iam_policy.CircleCI-Upload-To-S3.arn}"
}
resource "aws_iam_user_policy_attachment" "test-attach4" {
user      = "circleci"
policy_arn = "${aws_iam_policy.CodeDeploy-EC2-S3.arn}"
<<<<<<< HEAD
}
=======
}
>>>>>>> b4c0685bc6af0d1e5dc208d556fd578f0c9bd713
