REGISTER lib/avro-1.6.1.jar
REGISTER lib/json-simple-1.1.jar
REGISTER lib/piggybank*.jar
REGISTER lib/jackson-core-asl-1.8.8.jar
REGISTER lib/jackson-mapper-asl-1.8.8.jar
REGISTER lib/elasticsearch*.jar
REGISTER lib/lucene*.jar
REGISTER lib/wonderdog*.jar

genericfile = LOAD '$fileToIndex' USING org.apache.pig.piggybank.storage.avro.AvroStorage();

 store genericfile into 'es://$index/$object?avro=true&size=1000' USING
   com.infochimps.elasticsearch.pig.ElasticSearchStorage();


