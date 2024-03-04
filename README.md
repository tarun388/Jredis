# Jredis
Java implementation of redis.

## RESP 
Jredis supports RESP protocol to communicate to client over TCP connection.
Server expects per connection an array of bulk strings. 

## Run
From root directory of the repo, execute `mvn clean install` 
After successful build, you can start the Jredis server on localhost port 6379
> java -jar target/jredis-1.0-SNAPSHOT.jar

*Above command doesn't run* Issue `Exception in thread "main" java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory`

## Future ToDo
Extend support for multiple array of bulk strings per connection.