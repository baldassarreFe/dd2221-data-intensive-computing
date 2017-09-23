docker run -it --name Hadoop \
  -v "$(pwd)":/usr/local/hadoop/workdir \
  -p 8088:8088 -p 50070:50070 \
  sequenceiq/hadoop-docker:2.7.1 /etc/bootstrap.sh -bash
