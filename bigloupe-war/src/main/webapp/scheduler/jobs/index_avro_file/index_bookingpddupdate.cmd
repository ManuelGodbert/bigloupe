java  -DHADOOP_HOME=D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\hdfs\cluster\conf_filesystem -Dpig.notification.listener=org.bigloupe.web.monitor.pig.BigLoupePigProgressNotificationListener -Dbigloupe.http.server=http://localhost:9090 -Dpig.temp.dir=/user/karma/tmp/pig -Xms64M -Xmx256M -cp D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\hdfs\cluster\conf_filesystem\;lib\ant-1.6.5.jar;lib\asm-3.2.jar;lib\avro-1.6.1.jar;lib\bigloupe-hadoop-security-0.1.jar;lib\commons-cli-1.2.jar;lib\commons-codec-1.4.jar;lib\commons-el-1.0.jar;lib\commons-httpclient-3.1.jar;lib\commons-lang-2.5.jar;lib\commons-logging-1.0.4.jar;lib\commons-net-1.4.1.jar;lib\core-3.1.1.jar;lib\elasticsearch-0.20.2.jar;lib\guava-13.0.jar;lib\guava-r09-jarjar.jar;lib\hadoop-core-0.20.2-cdh3u4.jar;lib\hdfsdu-pig-0.1.0.jar;lib\hsqldb-1.8.0.7.jar;lib\jackson-core-asl-1.8.8.jar;lib\jackson-mapper-asl-1.8.8.jar;lib\jasper-compiler-5.5.23.jar;lib\jets3t-0.6.1.jar;lib\jetty-6.1.26.jar;lib\jetty-util-6.1.26.jar;lib\jline-0.9.94.jar;lib\json-simple-1.1.jar;lib\jsp-2.1-6.1.14.jar;lib\jsp-api-2.0.jar;lib\jsp-api-2.1-6.1.14.jar;lib\jsp-api-2.1.jar;lib\junit-4.8.1.jar;lib\kfs-0.3.jar;lib\log4j-1.2.16.jar;lib\lucene-analyzers-3.6.1.jar;lib\lucene-core-3.6.1.jar;lib\lucene-highlighter-3.6.1.jar;lib\lucene-memory-3.6.1.jar;lib\lucene-queries-3.6.1.jar;lib\mongo-hadoop-core-1.1.0-SNAPSHOT-sources.jar;lib\mongo-hadoop-core-1.1.0-SNAPSHOT.jar;lib\mongo-hadoop-pig-1.1.0-SNAPSHOT-sources.jar;lib\mongo-hadoop-pig-1.1.0-SNAPSHOT.jar;lib\mongo-java-driver-2.7.1.jar;lib\oro-2.0.8.jar;lib\paranamer-2.3.jar;lib\paranamer-ant-2.2.jar;lib\paranamer-generator-2.2.jar;lib\pig-0.10.0-withouthadoop.jar;lib\piggybank-0.10.0.jar;lib\qdox-1.10.1.jar;lib\servlet-api-2.5-20081211.jar;lib\servlet-api-2.5-6.1.14.jar;lib\servlet-api-2.5.jar;lib\slf4j-api-1.6.1.jar;lib\slf4j-log4j12-1.6.1.jar;lib\snappy-java-1.0.4.1.jar;lib\stringtemplate-3.2.1.jar;lib\wonderdog-1.1.jar org.apache.pig.Main -x local -param fileToIndex=hdfs://qvirkfs1.france.airfrance.fr:3300/user/karma/hadoop/DBLive13-SVT/rsu/dco/bookingpddupdate/Final/YO_BKG_SEGS -param object=yo_bkg_segs -param elasticSearchDirectory=/app/KARMA/travail/karma-search index_generic_avro_file_with_elasticsearch.pig