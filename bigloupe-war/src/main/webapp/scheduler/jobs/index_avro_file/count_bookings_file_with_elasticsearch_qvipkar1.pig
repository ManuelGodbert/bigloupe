REGISTER lib/avro-1.6.1.jar
REGISTER lib/json-simple-1.1.jar
REGISTER lib/piggybank-0.10.0.jar
REGISTER lib/jackson-core-asl-1.8.8.jar
REGISTER lib/jackson-mapper-asl-1.8.8.jar
REGISTER lib/elasticsearch*.jar
REGISTER lib/lucene*.jar
REGISTER lib/wonderdog*.jar

DEFINE AVROSTORAGE org.apache.pig.piggybank.storage.avro.AvroStorage();

bookings = LOAD '/hadoop/adt/booking/bookings/bookingloader_bookings_0.avro' USING AVROSTORAGE();
describe bookings;
bookings_grp = GROUP bookings ALL;
describe bookings_grp;
nb_bookings = FOREACH bookings_grp GENERATE COUNT(bookings);

STORE nb_bookings INTO '/tmp/cedric/count_nb_bookings' using PigStorage(';');  