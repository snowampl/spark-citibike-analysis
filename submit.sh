if [ -z "$AWS_CLUSTER" ]; then
  echo "[error]: must set AWS_CLUSTER to the location of your AWS cluster"
  exit 1
fi

APPLICATION_JAR=build/libs/bikeshare.jar

if [ ! -f $APPLICATION_JAR ]; then
  echo '[error]: must run "./gradlew build" first'
  exit 1
fi

echo "Copying jar to cluster"
scp $APPLICATION_JAR root@$AWS_CLUSTER:/root/
ssh root@$AWS_CLUSTER /root/spark/bin/spark-submit --master spark://$AWS_CLUSTER:7077 --class Bikeshare /root/$(basename $APPLICATION_JAR)

