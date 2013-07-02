REGISTER lib/avro-1.6.2.jar
REGISTER lib/json-simple-1.1.jar
REGISTER lib/piggybank-0.10.0.jar
REGISTER lib/jackson-core-asl-1.8.8.jar
REGISTER lib/jackson-mapper-asl-1.8.8.jar
REGISTER lib/elasticsearch*.jar
REGISTER lib/lucene*.jar
REGISTER lib/wonderdog*.jar

DEFINE AVROSTORAGE org.apache.pig.piggybank.storage.avro.AvroStorage();

bookings = LOAD '/hadoop/adt/booking/bookings/bookingloader_bookings_0.avro' USING AVROSTORAGE();
store bookings into 'es://bookings/booking?json=false&size=1000' USING
    com.infochimps.elasticsearch.pig.ElasticSearchStorage('/app/KARMASHA/travail/karma-search/elasticsearch-0.19.9/config/elasticsearch.yml', '/app/KARMASHA/travail/karma-search/elasticsearch-0.19.9/plugins');
