/* MongoDB libraries and configuration */
REGISTER lib/mongo-java-driver-2.7.1.jar /* MongoDB Java Driver */
REGISTER lib/mongo-hadoop-core-1.1.0-SNAPSHOT.jar
REGISTER lib/mongo-hadoop-pig-1.1.0-SNAPSHOT.jar

/* Set speculative execution off so we don't have the chance of duplicate records in Mongo */
SET mapred.map.tasks.speculative.execution false
SET mapred.reduce.tasks.speculative.execution false

SET default_parallel 5 /* By default, lets have 5 reducers */

/* Shortcut */
DEFINE AVROSTORAGE org.apache.pig.piggybank.storage.avro.AvroStorage();
DEFINE MONGOSTORAGE com.mongodb.hadoop.pig.MongoStorage(); 

avros = load '/tmp/data/referential/MP_NAVETTE_FLTS.avro' using AVROSTORAGE();
store avros into 'mongodb://qvidkro1.france.airfrance.fr:27017/MP_NAVETTE_FLTS.karma' using MONGOSTORAGE();

