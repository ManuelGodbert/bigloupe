java  -DHADOOP_HOME=D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\hdfs\cluster\conf_lh-karmanfsqua-st.france.airfrance.fr -Dpig.notification.listener=org.bigloupe.web.monitor.pig.BigLoupePigProgressNotificationListener -Dbigloupe.http.server=http://localhost:9090 -Dpig.temp.dir=pig/tmp -Xms64M -Xmx256M -cp D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\hdfs\cluster\conf_lh-karmanfsqua-st.france.airfrance.fr\;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\ant-1.6.5.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\asm-3.2.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\avro-1.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\bigloupe-hadoop-security-0.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\commons-cli-1.2.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\commons-codec-1.4.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\commons-el-1.0.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\commons-httpclient-3.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\commons-lang-2.5.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\commons-logging-1.0.4.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\commons-net-1.4.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\core-3.1.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\elasticsearch-0.20.2.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\guava-13.0.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\guava-r09-jarjar.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\hadoop-core-0.20.2-cdh3u4.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\hdfsdu-pig-0.1.0.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\hsqldb-1.8.0.7.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jackson-core-asl-1.8.8.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jackson-mapper-asl-1.8.8.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jasper-compiler-5.5.23.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jets3t-0.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jetty-6.1.26.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jetty-util-6.1.26.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jline-0.9.94.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\json-simple-1.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jsp-2.1-6.1.14.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jsp-api-2.0.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jsp-api-2.1-6.1.14.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\jsp-api-2.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\junit-4.8.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\kfs-0.3.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\log4j-1.2.16.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\lucene-analyzers-3.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\lucene-core-3.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\lucene-highlighter-3.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\lucene-memory-3.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\lucene-queries-3.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\mongo-hadoop-core-1.1.0-SNAPSHOT-sources.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\mongo-hadoop-core-1.1.0-SNAPSHOT.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\mongo-hadoop-pig-1.1.0-SNAPSHOT-sources.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\mongo-hadoop-pig-1.1.0-SNAPSHOT.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\mongo-java-driver-2.7.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\oro-2.0.8.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\paranamer-2.3.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\paranamer-ant-2.2.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\paranamer-generator-2.2.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\pig-0.10.0-withouthadoop.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\piggybank-0.10.0.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\qdox-1.10.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\servlet-api-2.5-20081211.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\servlet-api-2.5-6.1.14.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\servlet-api-2.5.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\slf4j-api-1.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\slf4j-log4j12-1.6.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\snappy-java-1.0.4.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\stringtemplate-3.2.1.jar;D:\Workspace\Workspace-BigData\bigloupe\java\bigloupe-war\src\main\webapp\scheduler\jobs\index_avro_file\lib\wonderdog-1.1.jar org.apache.pig.Main -param fileToIndex=hdfs://qvidkar7.france.airfrance.fr:3300/user/karma002/int1_pdd01/referential/MP_JVPLUS_INIT_PARAMS.avro -param object=/mp_jvplus_init_params -param elasticSearchDirectory=/app/KARMA/travail/karma-search/elasticsearch-0.20.2 index_generic_avro_file_with_elasticsearch.pig