REGISTER lib/avro-1.6.1.jar
REGISTER lib/json-simple-1.1.jar
REGISTER lib/piggybank-0.10.0.jar
REGISTER lib/jackson-core-asl-1.8.8.jar
REGISTER lib/jackson-mapper-asl-1.8.8.jar
REGISTER lib/elasticsearch*.jar
REGISTER lib/lucene*.jar
REGISTER lib/wonderdog*.jar


fs -rmr /tmp/data/hadoop/search/json;

DEFINE AVROSTORAGE org.apache.pig.piggybank.storage.avro.AvroStorage();

bookingsegments = LOAD '$bookingsegments' USING AVROSTORAGE();

store bookingsegments into '/tmp/data/hadoop/search/json' using JsonStorage();

bookingsegments_json = load '/tmp/data/hadoop/search/json' AS (createDateTimeUTC:long,id:long,cabinCombination:chararray,cancellationDateTimeUTC:long,demandDateTimeUTC:long,classCombination:chararray,connectionIndicator:chararray,datedFlowId:long,datedRouteId:long,departureDateTimeLT:long,dominantLegDestinationAirport:chararray,dominantLegOriginAirport:chararray,regularGoShowCount:int,isGroup:boolean,numberOfNames:int,numberOfPassengers:int,regularNoShowCount:int,outboundFlownDateLT:int,pnrId:chararray,finalizationDateTimeUTC:long,pricingClass:chararray,regularShowCount:int,showUpProbability:double,slidingGoShowCount:int,slidingNoShowCount:int,ticketedDateTimeUTC:long,trueODDepartureDateTimeLT:long,trueODDestinationAirport:chararray,trueODLocalIndicator:boolean,trueODOriginAirport:chararray,truePointOfOriginCountry:chararray,trueODPointOfSaleCountry:chararray,routeOriginAirport:chararray,isEvent:boolean,returnType:chararray,saturdayNightStay:boolean,lengthOfStay:int,odfs:bag{ARRAY_ELEM:tuple(airline:chararray,departureDateLT:int,destinationAirport:chararray,flightNumber:int,originAirport:chararray,arrivalDateTimeLT:long,departureTimeLT:int,operationalSuffix:chararray)});
-- bookingsegments_json_limit = limit bookingsegments_json 5;
-- pnrId = foreach bookingsegments_json_limit generate pnrId;
pnrId = foreach bookingsegments_json generate pnrId;
store pnrId into 'es://bookingsegments/bookingsegment?json=true&size=1000' USING
   com.infochimps.elasticsearch.pig.ElasticSearchStorage();


