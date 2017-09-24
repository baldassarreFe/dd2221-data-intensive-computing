# Copy files to HDFS and remove previous output
"$HADOOP_PREFIX"/bin/hdfs dfs -mkdir -p /user/root/lab1/topten/input
"$HADOOP_PREFIX"/bin/hdfs dfs -put input/users.xml /user/root/lab1/topten/input
"$HADOOP_PREFIX"/bin/hdfs dfs -ls /user/root/lab1/topten/input
"$HADOOP_PREFIX"/bin/hdfs dfs -rm -r /user/root/lab1/topten/output

# Compile and package
mkdir classes
CLASSPATH="$HADOOP_PREFIX"/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.1.jar
CLASSPATH="$CLASSPATH":"$HADOOP_PREFIX"/share/hadoop/common/hadoop-common-2.7.1.jar
CLASSPATH="$CLASSPATH":"$HADOOP_PREFIX"/share/hadoop/common/lib/commons-cli-1.2.jar​
CLASSPATH="$CLASSPATH":"$HADOOP_PREFIX"/share/hadoop/tools/lib/log4j-1.2.17.jar

javac -classpath "$CLASSPATH" -d classes src/lab1code/*.java
jar -cvf topten.jar -C classes .

# Submit job
"$HADOOP_PREFIX"/bin/hadoop jar topten.jar lab1code.TopTen \
  /user/root/lab1/topten/input /user/root/lab1/topten/output

# Results
"$HADOOP_PREFIX"/bin/hdfs dfs -ls /user/root/lab1/topten/output
"$HADOOP_PREFIX"/bin/hdfs dfs -cat /user/root/lab1/topten/output/*
