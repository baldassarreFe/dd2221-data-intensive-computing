docker run -it \
  --rm --name mySpark \
  -v "$(pwd)":/home/jovyan/work \
  -p 8888:8888 \
  -w /home/jovyan/work \
  jupyter/all-spark-notebook
