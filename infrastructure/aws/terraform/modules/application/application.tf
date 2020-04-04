data "aws_availability_zones" "available" {}
data "aws_caller_identity" "current" {}

locals {
  user_account_id = "${data.aws_caller_identity.current.account_id}"
}

# ================================= Creating IAM policies ================================
# --------------------- IAM Roles --------------------------
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

// output "ec2CodplyRoleName"{
// 	value = "${aws_iam_role.ec2CodplyRole.name}"
// }

resource "aws_iam_instance_profile" "test_profile" {
  name = "test_profile"
  role = "${aws_iam_role.ec2CodplyRole.name}"
}
// output "instance_profile_op"{
// 	value = "${aws_iam_instance_profile.test_profile.name}"
// }

 # ------------------------------------------------- IAM Policies ------------------------------------------
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
        "s3:PutObject",
        "s3:PutObjectAcl",
        "s3:GetObject",
        "s3:GetObjectAcl",
        "s3:DeleteObject"
	  ],
      "Effect": "Allow",
      "Resource": ["${aws_s3_bucket.bucket.arn}",
	   			"${aws_s3_bucket.bucket_image.arn}",
		 	    "${aws_s3_bucket.bucket.arn}/*",
		      "${aws_s3_bucket.bucket_image.arn}/*"]
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
            "s3:PutObject",
            "s3:PutObjectAcl",
            "s3:GetObject",
            "s3:GetObjectAcl",
            "s3:DeleteObject"
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

# --------------------------------- Role Policy attachments ------------------------------
resource "aws_iam_role_policy_attachment" "test-attach-codedeploysrv-policy" {
role      = "${aws_iam_role.codedeploysrv.name}"
policy_arn = "arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole"
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
}

# ============================ EC2 instance creation ================================

/* resource "aws_instance" "ec2-instance" {
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
	} */

# ============================= Security group creation ===========================

#----------Application security Group ---------------------

resource "aws_security_group" "application" {
	name = "Application security group"
	description = "Allow traffic for Webapp"
	vpc_id = "${var.vpcop_id}"
	ingress {
		cidr_blocks = ["0.0.0.0/0"]
		from_port = 22
		to_port = 22
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

// output "aws_security_group_application"{
// 	value = "${aws_security_group.application}"
// }

// output "app_sec_id"{
// 	value = "${aws_security_group.application.id}"
// }
#-------------------------database security group---------------------------
resource "aws_security_group" "database" {
  name        = "Database Security Group"
  description = "Allow TLS inbound traffic"
  vpc_id = "${var.vpcop_id}"
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

#========================= RDS instances ============================

resource "aws_db_subnet_group" "rds_subgroup" {
  //count = "3"
  name        = "rds_subnetgrp"
  description = "${var.vpc-cluster-name} db subnet group"
  subnet_ids  =  var.subnets
  //subnet_ids = ["subnet-0fcc59475c0a022b2","subnet-081d2a6aed65db062","subnet-06e6bf25dd2c3c952"]
  //subnet_ids  = ["${element(data.aws_subnet_ids.private.ids, 3)}","${element(data.aws_subnet_ids.private.ids, 2)}","${element(data.aws_subnet_ids.private.ids, 1)}"]
}

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
	db_subnet_group_name = "${aws_db_subnet_group.rds_subgroup.name}"
}

#====================== S3 Bucket ======================

#----------------for codedeploy --------------
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

// output "S3_bucket_arn"{
// 	value = "${aws_s3_bucket.bucket.arn}"
// }
# ----------- S3 Bucket for Image --------------------

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


#----------------for lambda --------------
resource "aws_s3_bucket" "lambda_bucket" {
	bucket = "lambda.${var.domain-name}"
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

// output "S3_bucket_image_arn"{
// 	value = "${aws_s3_bucket.bucket_image.arn}"
// }
# ====================== DynamoDB table ===========================

resource "aws_dynamodb_table" "basic-dynamodb-table" {
	 name           = "csye6225"
	 hash_key       = "id"
	 read_capacity  = "20"
	 write_capacity = "20"
	 attribute {
		name = "id"
		type = "S"
  	}
  }

# =================== Codedeploy App and Group ==============================

resource "aws_codedeploy_app" "csye6225-webapp" {
  name = "csye6225-webapp"
}

resource "aws_codedeploy_deployment_group" "csye6225-webapp-deployment" {
  app_name              = "${aws_codedeploy_app.csye6225-webapp.name}"
  deployment_group_name = "csye6225-webapp-deployment"
  deployment_config_name = "CodeDeployDefault.AllAtOnce"
  service_role_arn      = "${aws_iam_role.codedeploysrv.arn}"
  autoscaling_groups = ["${aws_autoscaling_group.ec2_asg.name}"]
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
  load_balancer_info{
	  target_group_info{
		  name = "${aws_lb_target_group.lb_tg.name}"
	  }
  }
  }

# ======================== EC2 NAT Gateway ===========================
/*resource "aws_eip" "nat_1" {
  vpc                       = true
  associate_with_private_ip = "${var.subnet_ids}"
}
resource "aws_nat_gateway" "gw_1" {
  allocation_id = "${aws_eip.nat_1.id}"
  subnet_id     = "${aws_subnet.subnet_ids[0].id}"

  tags = {
    Name = "gw NAT 1"
  }
}*/
# ========================= Load Balancer ================================

resource "aws_lb" "alb" {
  name = "alb"
  subnets = "${var.subnets}"
  load_balancer_type = "application"
  security_groups = ["${aws_security_group.application.id}"]
  internal = false
  enable_deletion_protection = true
  tags = {
    Name = "terraform-alb"
  }
}
resource "aws_lb_listener" "lb_listener1" {
  load_balancer_arn = "${aws_lb.alb.arn}"
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = "${var.certiArn}"
  default_action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.lb_tg.arn}"
  }
}

// output "aws_lb_listenerARN"{
//   value = "${aws_lb_listener.lb_listener1.arn}"
// }
/* Professor said not to do this, but then where do we have to handle http?
resource "aws_lb_listener" "lb_listener2" {
  load_balancer_arn = "${aws_lb.alb.arn}"
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}*/

resource "aws_lb_target_group" "lb_tg" {
  name        = "tf-lb-tg"
  port        = "8080"
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = "${var.vpcop_id}"
  tags        = {
      name    = "tf-lb-tg"
  }
  health_check {
      healthy_threshold = 3
      unhealthy_threshold = 5
      timeout = 5
      interval = 30
      path = "/apphealthstatus"
      port = "8080"
      matcher = "200"
  }

}


resource "aws_alb_listener_rule" "listener_rule" {
  listener_arn = "${aws_lb_listener.lb_listener1.arn}"  
  priority     = 100   
  action {    
    type             = "forward"    
    target_group_arn = "${aws_lb_target_group.lb_tg.arn}"  
  }   
  condition {    
    field  = "path-pattern"    
    values = ["/api*"]  
  }
}

// resource "aws_lb_target_group" "lb_tg" {
//   name     = "lb-tg"
//   port     = 443
//   protocol = "HTTPS"
//   vpc_id   = "${var.vpcop_id}"

//   health_check {
//     healthy_threshold   = 3
//     unhealthy_threshold = 10
//     timeout             = 3
//     path              = "HTTPS:8080/"
//     interval            = 30
//   }
// }

#Autoscaling Attachment
resource "aws_autoscaling_attachment" "asg_targetgroup" {
  alb_target_group_arn   = "${aws_lb_target_group.lb_tg.arn}"
  autoscaling_group_name = "${aws_autoscaling_group.ec2_asg.id}"
}
resource "aws_lb_target_group_attachment" "test" {
  target_group_arn = "${aws_lb_target_group.lb_tg.arn}"
  target_id        = "${aws_lambda_function.func_lambda.id}"
  port             = 80
}
  
# ====================== EC2 Launch Configuration ===========================
resource "aws_launch_configuration" "ec2_lc" {
  name   = "asg_launch_config"
  image_id      = "${var.ami_id}"
  instance_type = "${var.instance_type}"
  key_name      = "${var.ami_key_pair_name}"
  associate_public_ip_address = true
  security_groups = ["${aws_security_group.application.id}"]
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

  iam_instance_profile = "${aws_iam_instance_profile.test_profile.name}"
  root_block_device {
		volume_size = "${var.volume_size}"
		volume_type = "${var.volume_type}"
	}
  lifecycle {
    create_before_destroy = true
  }
  depends_on = [
    "aws_security_group.application"
  ]
}

# ============================ Autoscaling group =========================
resource "aws_autoscaling_group" "ec2_asg" {
  name                 = "ec2_asg"
  launch_configuration = "${aws_launch_configuration.ec2_lc.name}"
  min_size             = 3
  max_size             = 10
  health_check_type    = "ELB"
  vpc_zone_identifier  = var.subnets
  lifecycle {
    create_before_destroy = true
  }
  tag {
    key                 = "env"
    value               = "dev"
    propagate_at_launch = true
  }
  depends_on = [
    "aws_launch_configuration.ec2_lc",
    "var.subnets",
    "aws_lb_target_group.lb_tg"
  ]
}


#---------------------------- Autoscaling Policies ---------------------------
# SCALE - UP Policy
resource "aws_autoscaling_policy" "asg_scaleUp" {
  name                   = "WebServerScaleUpPolicy"
  scaling_adjustment     = "1"
  adjustment_type        = "ChangeInCapacity"
  cooldown               = "60"
  autoscaling_group_name = "${aws_autoscaling_group.ec2_asg.name}"
  policy_type = "SimpleScaling"
}

resource "aws_cloudwatch_metric_alarm" "up-cpu-alarm" {
  alarm_name = "up-cpu-alarm"
  alarm_description = "Scale-up if CPU > 90% for 10 minutes"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods = "2"
  metric_name = "CPUUtilization"
  namespace = "AWS/EC2"
  period = "300"
  statistic = "Average"
  threshold = "90"
  dimensions = "${
      		map(
     		"AutoScalingGroupName", "${aws_autoscaling_group.ec2_asg.name}",
    		)
  	}"
}
 

# SCALE - DOWN Policy
resource "aws_autoscaling_policy" "asg_scaleDwn" {
  name                   = "WebServerScaleDownPolicy"
  scaling_adjustment     = "-1"
  adjustment_type        = "ChangeInCapacity"
  cooldown               = "60"
  autoscaling_group_name = "${aws_autoscaling_group.ec2_asg.name}"
  policy_type = "SimpleScaling"
}
resource "aws_cloudwatch_metric_alarm" "down-cpu-alarm" {
  alarm_name = "down-cpu-alarm"
  alarm_description = "Scale-down if CPU < 70% for 10 minutes"
  comparison_operator = "LessThanThreshold"
  evaluation_periods = "2"
  metric_name = "CPUUtilization"
  namespace = "AWS/EC2"
  period = "300"
  statistic = "Average"
  threshold = "70"
  dimensions = "${
      		map(
     		"AutoScalingGroupName", "${aws_autoscaling_group.ec2_asg.name}",
    		)
  	}"
}

# ============================== SNS Topic ===================================

resource "aws_sns_topic" "email_request" {
  name = "email_request"
  delivery_policy = <<EOF
{
  "http": {
    "defaultHealthyRetryPolicy": {
      "minDelayTarget": 20,
      "maxDelayTarget": 20,
      "numRetries": 3,
      "numMaxDelayRetries": 0,
      "numNoDelayRetries": 0,
      "numMinDelayRetries": 0,
      "backoffFunction": "linear"
    },
    "disableSubscriptionOverrides": false,
    "defaultThrottlePolicy": {
      "maxReceivesPerSecond": 1
    } }}
EOF
}

resource "aws_iam_policy" "sns_policy" {
  name        = "test_policy"
  path        = "/"
  description = "My test policy"
  policy = <<EOF
{
  "Version": "2008-10-17",
  "Id": "sns_policy_ID",
  "Statement": [
    {
      "Sid": "sns_statement_ID",
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": [
        "SNS:GetTopicAttributes",
        "SNS:SetTopicAttributes",
        "SNS:AddPermission",
        "SNS:RemovePermission",
        "SNS:DeleteTopic",
        "SNS:Subscribe",
        "SNS:ListSubscriptionsByTopic",
        "SNS:Publish",
        "SNS:Receive"
      ],
      "Resource": "${aws_sns_topic.email_request.arn}",
      "Condition": {
        "StringEquals": {
          "AWS:SourceOwner": "${var.account_id}"
        }
      }
    }
  ]
}
EOF
}
/*resource "aws_iam_role" "iam_for_sns" {
  name = "iam_for_sns"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ssm.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}*/
resource "aws_iam_role_policy_attachment" "snspolicy_role_attach1" {
  role       = "${aws_iam_role.iam_for_lambda.name}"
  policy_arn = "${aws_iam_policy.sns_policy.arn}"
}
resource "aws_iam_role_policy_attachment" "snspolicy_role_attach2" {
  role       = "${aws_iam_role.ec2CodplyRole.name}"
  policy_arn = "${aws_iam_policy.sns_policy.arn}"
}
#================================ Lamda Function ======================================

#--------------------------- Create Lambda Policy -------------------

resource "aws_iam_policy" "lambda_logging" {
  name = "lambda_logging"
  path = "/"
  description = "IAM policy for logging from a lambda"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "logs:DescribeLogStreams"
      ],
      "Resource": "arn:aws:logs:*:*:*",
      "Effect": "Allow"
    },
    {
      "Action": [
        "ses:*"
      ],
      "Resource": "*",
      "Effect": "Allow"
    },
    {
      "Effect": "Allow",
      "Action": [
        "dynamodb:DescribeStream",
        "dynamodb:GetRecords",
        "dynamodb:GetShardIterator",
        "dynamodb:ListStreams","dynamodb:GetItem",
        "dynamodb:DeleteItem",
        "dynamodb:PutItem",
        "dynamodb:Scan",
        "dynamodb:Query",
        "dynamodb:UpdateItem",
        "dynamodb:BatchWriteItem",
        "dynamodb:BatchGetItem",
        "dynamodb:DescribeTable"
      ],
      "Resource": "${aws_dynamodb_table.snslambda_table.arn}"
  },
  {
     "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": ["${aws_s3_bucket.bucket.arn}",
		              "${aws_s3_bucket.bucket.arn}/*",
                  "${aws_s3_bucket.bucket_image.arn}",
		              "${aws_s3_bucket.bucket_image.arn}/*"]
  }
  ]
}
EOF
}

#----------------------- Create IAM Lambda Role ------------------

resource "aws_iam_role" "iam_for_lambda" {
  name = "iam_for_lambda"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

#--------------------- Attach IAM Lambda Role with Log Policies ------------

resource "aws_iam_role_policy_attachment" "lambda_role_attach1" {
  role = "${aws_iam_role.iam_for_lambda.name}"
  policy_arn = "${aws_iam_policy.lambda_logging.arn}"
}
resource "aws_iam_role_policy_attachment" "lambda_role_attach2" {
  role       = "${aws_iam_role.ec2CodplyRole.name}"
  policy_arn = "${aws_iam_policy.lambda_logging.arn}"
}
#--------------------- Create Lambda Function ---------------------

resource "aws_lambda_function" "func_lambda" {
  filename      = "csye6225_lambda0.0.1-SNAPSHOT"
  function_name = "func_lambda"
  role          = "${aws_iam_role.iam_for_lambda.arn}"
  handler       = "LogEvent"
  runtime	    = "java8"
  s3_bucket 	= "${aws_s3_bucket.lambda_bucket.bucket_domain_name}"
  timeout        = 900
  reserved_concurrent_executions = 1
  depends_on     = ["aws_sns_topic.email_request"]
   # Pass the SNS topic ARN and DynamoDB table name in the environment.
  environment {
      variables = "${
      		map(
     		"sns_arn", "${aws_sns_topic.email_request.arn}",
            "dynamo_table_name", "${aws_dynamodb_table.snslambda_table.name}"
    		)
  	}"
  }
}

  #-------------SNS permissions to invoke lambda function ---------------

resource "aws_lambda_permission" "sns" {
  statement_id  = "AllowExecutionFromSNSToLambda"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.func_lambda.arn}"
  principal     = "sns.amazonaws.com"
  source_arn = "${aws_sns_topic.email_request.arn}"
}

#---------------- Subscribe Lambda function to SNS topic ---------------

resource "aws_sns_topic_subscription" "sns_subscription" {
  depends_on = ["aws_lambda_function.func_lambda"]
  topic_arn = "${aws_sns_topic.email_request.arn}"
  protocol = "lambda"
  endpoint = "${aws_lambda_function.func_lambda.arn}"
}

#---------------------- Lambda source Mapping ------------------------

// resource "aws_lambda_event_souasic-dynamodb-tableg" "lambda_dynamo_mapping" {
//   event_source_arn  = "${aws_dynamodb_table.snslambda_table.stream_arn}"
//   function_name     = "${aws_lambda_function.func_lambda.arn}"
//   starting_position = "LATEST"
// }

resource "aws_lambda_event_source_mapping" "lambda_dynamo_mapping" {
  event_source_arn  = "${aws_dynamodb_table.snslambda_table.stream_arn}"
  function_name     = "${aws_lambda_function.func_lambda.arn}"
  starting_position = "LATEST"
}

# ----------------------- Lambda SNS DynamoDB table ---------------------------------

resource "aws_dynamodb_table" "snslambda_table" {
	 name           = "snslambda"
	 hash_key       = "id"
	 read_capacity = "20"
	 write_capacity = "20"
     stream_enabled = true
     stream_view_type = "KEYS_ONLY"
	 attribute {
		name = "id"
		type = "S"
  	}
     ttl {
      enabled = true
      attribute_name = "TTL"
    }
  } 

# ================================ ROUTE 53 =========================================
resource "aws_route53_zone" "routezone" {
  name = "${var.domain-name}"
  
}

resource "aws_route53_record" "route" {
  zone_id = "${aws_route53_zone.routezone.zone_id}"
  name    = "${var.domain-name}"
  type    = "A"

  alias {
    name                   = "${aws_lb.alb.dns_name}"
    zone_id                = "${aws_lb.alb.zone_id}"
    evaluate_target_health = true
  }
}