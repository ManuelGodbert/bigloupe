/*******
* mongo_upload_diff_flows.pig 
*******/
REGISTER lib/mongo-java-driver-2.7.1.jar /* MongoDB Java Driver */
REGISTER lib/mongo-hadoop-core-1.1.0-SNAPSHOT.jar
REGISTER lib/mongo-hadoop-pig-1.1.0-SNAPSHOT.jar
REGISTER lib/avro-1.6.1.jar
REGISTER lib/json-simple-1.1.jar

define avrostorage org.apache.pig.piggybank.storage.avro.AvroStorage();
define mongostorage com.mongodb.hadoop.pig.MongoStorage();
-- define evalBoolean com.afklm.karma.pig.udf.EVAL_BOOLEAN();

-- raw = LOAD 'mongodb://localhost/demo.excitelog' USING com.mongodb.hadoop.pig.MongoLoader('user:chararray, time:chararray, query:chararray') AS (user, time, query);

flowsWithDifferentTotalDbds = load '/user/karma/hadoop/pig_kro1/karnonreg/stats/details/flowsWithDifferentTotalDbds' using avrostorage();

store flowsWithDifferentTotalDbds into 'mongodb://qvidkro1:27017/karadec.flowsDbd' using mongostorage();
-- using com.mongodb.hadoop.pig.MongoStorage('update [time, servername, hostname]', '{time : 1, servername : 1, hostname : 1}, {unique:false, dropDups: false}');
