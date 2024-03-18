# Jredis
Java implementation of redis. Started this project after reading through the challenge [Build Your Own Redis Server](https://codingchallenges.fyi/challenges/challenge-redis)

## RESP support
Jredis supports very basic RESP protocol to communicate with client over TCP connection.
Server expects expects an array of RESP bulk strings per request. 

## How to build
`mvn clean install`

## How to Run
Start Jredis server on localhost port 6379
> java -jar target/jredis-1.0-SNAPSHOT.jar

## Redis benchmark

```
$ redis-benchmark -t SET,GET -q
    WARNING: Could not fetch server CONFIG
    SET: 12084.59 requests per second, p50=3.703 msec                   
    GET: 17376.19 requests per second, p50=2.703 msec                   
```