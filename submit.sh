if [ -z "$AWS_CLUSTER" ]; then
  echo "[error]: must set AWS_CLUSTER to the location of your AWS cluster"
  exit 1
fi

APPLICATION_JAR=build/libs/spark-citibike-analysis.jar
MAIN_CLASS=Bikeshare

if [ ! -f $APPLICATION_JAR ]; then
  echo '[error]: must run "./gradlew build" first'
  exit 1
fi

echo "Copying jar to cluster"
scp build/libs/* root@$AWS_CLUSTER:/root/
ssh root@$AWS_CLUSTER '/root/spark/bin/spark-submit --master spark://$AWS_CLUSTER:7077 --class $MAIN_CLASS --jars "\$(find /root -maxdepth 1 -name *.jar -print0 | tr "\0" ",")" /root/\$(basename $APPLICATION_JAR)'

