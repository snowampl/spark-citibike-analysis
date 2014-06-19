spark-bikeshare
===============

Some analysis on Citibike's published data using Spark on Amazon EC2.

## Setup

 1. Follow instructions [here][spark-ec2] to set up a Spark cluster on Amazon EC2.
 2. `export AWS_CLUSTER=<your_cluster_url>` (e.g. `ec2-<numbers>.compute-1.amazonaws.com`)
 3. `./gradlew build`
 4. `./submit.sh`
 
The `submit.sh` script simply `scp`s the built jar file to the cluster and runs `spark-submit` via `ssh`.

[spark-ec2]: http://spark.apache.org/docs/1.0.0/ec2-scripts.html
