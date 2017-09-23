# Copy files to HDFS and remove previous output
"$HADOOP_PREFIX"/bin/hdfs dfs -mkdir -p /user/root/lab1/wordcount/input
"$HADOOP_PREFIX"/bin/hdfs dfs -put input/file* /user/root/lab1/wordcount/input
"$HADOOP_PREFIX"/bin/hdfs dfs -ls /user/root/lab1/wordcount/input
"$HADOOP_PREFIX"/bin/hdfs dfs -rm -r /user/root/lab1/wordcount/output

# Compile and package
mkdir classes
CLASSPATH="$HADOOP_PREFIX"/share/hadoop/common/hadoop-common-2.7.1.jar:"$HADOOP_PREFIX"/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.1.jar:"$HADOOP_PREFIX"/share/hadoop/common/lib/commons-cli-1.2.jar​
javac -classpath "$CLASSPATH" -d classes src/lab1code/*.java
jar -cvf wordcount.jar -C classes .

# Submit job
"$HADOOP_PREFIX"/bin/hadoop jar wordcount.jar lab1code.WordCount \
  /user/root/lab1/wordcount/input /user/root/lab1/wordcount/output

"$HADOOP_PREFIX"/bin/hdfs dfs -ls /user/root/lab1/wordcount/output
"$HADOOP_PREFIX"/bin/hdfs dfs -cat /user/root/lab1/wordcount/output/*
