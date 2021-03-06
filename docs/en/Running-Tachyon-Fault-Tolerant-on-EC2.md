---
layout: global
title: Running Tachyon with Fault Tolerance on EC2
nickname: Tachyon on EC2 with Fault Tolerance
group: User Guide
priority: 4
---

Tachyon with Fault Tolerance can be deployed on Amazon EC2 using the
[Vagrant scripts](https://github.com/amplab/tachyon/tree/master/deploy/vagrant) that come with
Tachyon. The scripts let you create, configure, and destroy clusters that come automatically
configured with Apache HDFS.

# Prerequisites

**Install Vagrant and the AWS plugins**

Download [Vagrant](https://www.vagrantup.com/downloads.html)

Install AWS Vagrant plugin:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/install-aws-vagrant-plugin.md %}

**Install Tachyon**

Download Tachyon to your local machine, and unzip it:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/download-tachyon.md %}

**Install python library dependencies**

Install [python>=2.7](https://www.python.org/), not python3.

Under `deploy/vagrant` directory in your home directory, run:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/install-vagrant.md %}

Alternatively, you can manually install [pip](https://pip.pypa.io/en/latest/installing/), and then
in `deploy/vagrant` run:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/install-pip.md %}


# Launch a Cluster

To run a Tachyon cluster on EC2, first sign up for an Amazon EC2 account
on the [Amazon Web Services site](http://aws.amazon.com/).

Then create [access keys](https://aws.amazon.com/developers/access-keys/)
and set shell environment variables `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` by:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/access-keys.md %}

Next generate your EC2
[Key Pairs](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html). Make sure to set
the permissions of your private key file that only you can read it:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/generate-key-pairs.md %}

Copy `deploy/vagrant/conf/ec2.yml.template` to `deploy/vagrant/conf/ec2.yml` by:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/copy-ec2-yml.md %}

In the configuration file `deploy/vagrant/conf/ec2.yml`, set the value of `Keypair` to your keypair
name and `Key_Path` to the path to the pem key.

In the configuration file `deploy/vagrant/conf/tachyon.yml`, set the value of `Masters` to the
number of TachyonMasters you want. In fault tolerant mode, value of `Masters` should be larger than
1.

By default, the Vagrant script creates a
[Security Group](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-network-security.html)
named *tachyon-vagrant-test* at
[Region(**us-east-1**) and Availability Zone(**us-east-1a**)](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html).
The security group will be set up automatically in the region with all inbound/outbound network
traffic opened. You can change the *security group*, *region* and *availability zone* in `ec2.yml`.

Now you can launch the Tachyon cluster with Hadoop2.4.1 as under filesystem in us-east-1a by running
the script under `deploy/vagrant`:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/launch-cluster.md %}

Note that the `<number of machines>` above should be larger than or equal to `Masters` set in
`deploy/vagrant/conf/tachyon.yml`.

Each node of the cluster has a Tachyon worker and each master node has a Tachyon master. The leader
is in one of the master nodes.

# Access the cluster

**Access through Web UI**

After command `./create <number of machines> aws` succeeds, you can see three green lines like below
shown at the end of the shell output:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/shell-output.md %}

The first line shows public IP for current leader of all Tachyon masters.

The second line shows public IP for master of other softwares like Hadoop.

Default port for Tachyon Web UI is **19999**.

Default port for Hadoop Web UI is **50070**.

Visit `http://{MASTER_IP}:{PORT}` in the browser to access the Web UIs.

You can also monitor the instances state through
[AWS web console](https://console.aws.amazon.com/console/home?region=us-east-1).

**Access with ssh**

The nodes created are placed in one of two categories.

One category contains `TachyonMaster`, `TachyonMaster2` and so on, representing all Tachyon masters;
one of them is the leader, and the others are standbys. `TachyonMaster` is also the master for other
software, like Hadoop. Each node also runs workers for Tachyon and other software like Hadoop.

Another group contains `TachyonWorker1`, `TachyonWorker2` and so on. Each node runs workers
for Tachyon and other software like Hadoop.

To ssh into a node, run:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/ssh.md %}

For example, you can ssh into `TachyonMaster` with:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/ssh-TachyonMaster.md %}

All software is installed under the root directory, e.g. Tachyon is installed in `/tachyon`,
Hadoop is installed in `/hadoop`, and Zookeeper is installed in `/zookeeper`.

On the leader master node, you can run tests against Tachyon to check its health:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/runTests.md %}

After the tests finish, visit Tachyon web UI at `http://{MASTER_IP}:19999` again. Click
`Browse File System` in the navigation bar, and you should see the files written to Tachyon by the
above tests.

You can ssh into the current Tachyon master leader, and find process ID of the TachyonMaster
process with:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/jps.md %}

Then kill the leader with:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/kill-leader.md %}

Then you can ssh into `TachyonMaster` where [zookeeper](http://zookeeper.apache.org/) is
running to find out the new leader, and run the zookeeper client via:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/zookeeper-client.md %}

In the zookeeper client shell, you can see the leader with the command:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/see-leader.md %}

The output of the command should show the new leader. You may need to wait for a moment for the
new leader to be elected. You can query the public IP for the new leader based on its name in
[AWS web console](https://console.aws.amazon.com/console/home?region=us-east-1).

Visit Tachyon web UI at `http://{NEW_LEADER_MASTER_IP}:19999`. Click `Browse File System` in the
navigation bar, and you should see all files are still there.

From a node in the cluster, you can ssh to other nodes in the cluster without password with:

{% include Running-Tachyon-Fault-Tolerant-on-EC2/ssh-other-node.md %}

# Destroy the cluster

Under `deploy/vagrant` directory, you can run

{% include Running-Tachyon-Fault-Tolerant-on-EC2/destroy.md %}

to destroy the cluster that you created. Only one cluster can be created at a time. After the
command succeeds, the EC2 instances are terminated.
