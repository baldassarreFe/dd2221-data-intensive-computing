# Top 10 users by reputation

## Folder structure
```
topten
├── input
│   └── users.xml
├── src
│   └── lab1code
│       ├── TopTen.java
│       ├── TopTenMapper.java
│       ├── TopTenReducer.java
│       └── XmlUtils.java
└── topten.sh
```

## Prepare files, compile, package and submit

Run inside the Docker container:
```sh
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
```

## Stats of the MapReduce job
```
Counters: 49
	File System Counters
		FILE: Number of bytes read=7244
		FILE: Number of bytes written=245861
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=5856430
		HDFS: Number of bytes written=7180
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=2759
		Total time spent by all reduces in occupied slots (ms)=2631
		Total time spent by all map tasks (ms)=2759
		Total time spent by all reduce tasks (ms)=2631
		Total vcore-seconds taken by all map tasks=2759
		Total vcore-seconds taken by all reduce tasks=2631
		Total megabyte-seconds taken by all map tasks=2825216
		Total megabyte-seconds taken by all reduce tasks=2694144
	Map-Reduce Framework
		Map input records=13995
		Map output records=10
		Map output bytes=7199
		Map output materialized bytes=7244
		Input split bytes=127
		Combine input records=10
		Combine output records=10
		Reduce input groups=1
		Reduce shuffle bytes=7244
		Reduce input records=10
		Reduce output records=10
		Spilled Records=20
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=47
		CPU time spent (ms)=1820
		Physical memory (bytes) snapshot=432439296
		Virtual memory (bytes) snapshot=1473417216
		Total committed heap usage (bytes)=349175808
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters
		Bytes Read=5856303
	File Output Format Counters
		Bytes Written=7180
```

## Output
Pulled from `hdfs` with:
```sh
"$HADOOP_PREFIX"/bin/hdfs dfs -get /user/root/lab1/topten/output/* .
```

Output (formatted):
```xml
<row Id="2452" Reputation="4503" CreationDate="2014-07-11T08:54:54.627"
     DisplayName="Aleksandr Blekh" LastAccessDate="2016-05-31T21:43:46.237" AccountId="3422261"/>
<row Id="381" Reputation="3638" CreationDate="2014-06-08T06:51:59.140"
     DisplayName="Emre" LastAccessDate="2016-06-11T16:03:39.437" AccountId="441850"/>
<row Id="11097" Reputation="2824" CreationDate="2015-08-05T12:46:59.553"
     DisplayName="Dawny33" LastAccessDate="2016-06-11T17:42:13.167" AccountId="6444670"/>
<row Id="21" Reputation="2586" CreationDate="2014-05-13T23:30:57.300"
     DisplayName="Sean Owen" LastAccessDate="2016-06-11T09:56:06.363" AccountId="25066"/>
<row Id="548" Reputation="2289" CreationDate="2014-06-10T16:31:10.057"
     DisplayName="indico" LastAccessDate="2014-09-12T23:38:15.757" AccountId="4594431"/>
<row Id="84" Reputation="2179" CreationDate="2014-05-14T03:30:28.107"
     DisplayName="Rubens" LastAccessDate="2016-06-10T09:09:53.607" AccountId="1822136"/>
<row Id="434" Reputation="2131" CreationDate="2014-06-10T01:39:44.867"
     DisplayName="Steve Kallestad" LastAccessDate="2016-06-03T01:31:38.707" AccountId="2317167"/>
<row Id="108" Reputation="2127" CreationDate="2014-05-14T06:55:01.283"
     DisplayName="rapaio" LastAccessDate="2016-06-10T14:28:17.343" AccountId="2030885"/>
<row Id="9420" Reputation="1878" CreationDate="2015-05-03T00:14:00.677"
     DisplayName="AN6U5" LastAccessDate="2016-06-11T15:10:55.553" AccountId="1938103"/>
<row Id="836" Reputation="1846" CreationDate="2014-06-14T16:31:59.410"
     DisplayName="Neil Slater" LastAccessDate="2016-06-11T19:56:03.033" AccountId="2274369"/>

```
