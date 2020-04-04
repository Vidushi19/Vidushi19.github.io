data "aws_availability_zones" "available" {}

provider "aws" {
  profile = "${var.profile}"
  region = "${var.region}"
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

output "vpcop_id"{
    value = "${aws_vpc.vpc_tf.id}"
}

resource "aws_subnet" "subnet_tf" {
	count = 3
	availability_zone = "${data.aws_availability_zones.available.names[count.index]}"
	cidr_block = "10.0.${count.index}.0/24"
	vpc_id = "${var.vpcop_id}"
	tags = "${
	map(
		"Name","${var.vpc-cluster-name}",
    )
  }"
}

output "subnets" {
    value = flatten(["${aws_subnet.subnet_tf.*.id}"])
}

// output "subnets" {
//     value = ["${aws_subnet.subnet_tf}"]
// }

resource "aws_internet_gateway" "ig_tf" {
	vpc_id = "${aws_vpc.vpc_tf.id}"
	tags = "${
	map(
		"Name","${var.vpc-cluster-name}IG",
	)
	}"
}
output "igop_id"{
    value = "${aws_internet_gateway.ig_tf.id}"
}
resource "aws_route_table" "rt_tf" {
	vpc_id = "${aws_vpc.vpc_tf.id}"
	route {
		cidr_block = "0.0.0.0/0"
		gateway_id = "${aws_internet_gateway.ig_tf.id}"
  }
}
output "rtop_id"{
    value = "${aws_route_table.rt_tf.id}"
}
resource "aws_route_table_association" "rtAsso_tf" {
	count = 3
	subnet_id = "${aws_subnet.subnet_tf.*.id[count.index]}"
	route_table_id = "${aws_route_table.rt_tf.id}"
}
