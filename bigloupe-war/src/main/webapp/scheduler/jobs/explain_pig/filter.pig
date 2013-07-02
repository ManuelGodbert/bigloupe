REGISTER lib/avro-1.6.1.jar
REGISTER lib/json-simple-1.1.jar
REGISTER lib/piggybank*.jar
REGISTER lib/jackson-core-asl-1.8.8.jar
REGISTER lib/jackson-mapper-asl-1.8.8.jar

genericfile = LOAD '$fileToFilter' USING org.apache.pig.piggybank.storage.avro.AvroStorage();

select = filter genericfile by ( $filterParam == $filterValue) ;

store select into '$fileFiltered' USING org.apache.pig.piggybank.storage.avro.AvroStorage('same', '$fileToFilter');
