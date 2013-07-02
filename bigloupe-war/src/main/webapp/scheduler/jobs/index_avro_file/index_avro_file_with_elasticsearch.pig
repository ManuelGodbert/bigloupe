REGISTER lib/avro-1.6.1.jar
REGISTER lib/json-simple-1.1.jar
REGISTER lib/piggybank-0.10.0.jar
REGISTER lib/jackson-core-asl-1.8.8.jar
REGISTER lib/jackson-mapper-asl-1.8.8.jar
REGISTER lib/elasticsearch*.jar
REGISTER lib/lucene*.jar
REGISTER lib/wonderdog*.jar

-- fs -rmr $output;

DEFINE AVROSTORAGE org.apache.pig.piggybank.storage.avro.AvroStorage();

bookingsegments = LOAD '$bookingsegments' USING AVROSTORAGE();

store bookingsegments into '/tmp/data/hadoop/search/json' using JsonStorage();

bookingsegments_json = load '/tmp/data/hadoop/search/json' AS (pnrId:chararray,pnrCreationDateTimeUTC:long,segmentCreationDateUTC:int,segmentUpdateDateTimeUTC:long,originAirport:chararray,destinationAirport:chararray,departureDateLT:int,departureTimeLT:int,arrivalDateLT:int,arrivalTimeLT:int,codeShareAgreement:chararray,dfbIds:bag{PIG_WRAPPER:tuple(ARRAY_ELEM:long)},marketingCarrierCode:chararray,marketingFlightIdentificationNumber:int,marketingOperationalSuffix:chararray,marketingCarrierCabin:chararray,marketingCarrierClassObserved:chararray,operatingCarrierCode:chararray,operatingFlightIdentificationNumber:int,operatingOperationalSuffix:chararray,operatingCarrierCabin:chararray,operatingCarrierClassObserved:chararray,bookingClassUsed:boolean,nbUnaccompaniedMinors:int,nbStaff:int,statusCode:chararray,isTicketed:boolean,ticketedDateTimeUTC:long,segmentCancellationDateUTC:int);
pnrId = foreach bookingsegments_json generate pnrId;
store pnrId into 'es://bookingsegments/bookingsegment?json=true&size=1000' USING
   com.infochimps.elasticsearch.pig.ElasticSearchStorage('/app/KARMA/travail/karma-search/elasticsearch/config/elasticsearch.yml', '/app/KARMA/travail/karma-search/elasticsearch/plugins');


