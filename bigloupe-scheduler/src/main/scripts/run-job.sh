base_dir=$(dirname $0)/..

for file in $base_dir/lib/*.jar;
do
  CLASSPATH=$CLASSPATH:$file
done

java -cp $CLASSPATH org.bigloupe.web.scheduler.job.CommandLineJobRunner $@