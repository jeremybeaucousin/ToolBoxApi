# General presentation

## Prerequirements
* Java JDK 1.8 or later :
  * https://www.java.com/fr/download/
* SBT : 
  * https://www.scala-sbt.org/download.html
* GRADLE : 
 * https://gradle.org/releases/
* MongoDB : 
 * https://www.mongodb.com/what-is-mongodb
 * Docker : https://hub.docker.com/_/mongo

## MongoDB
### Docker
#### Créate container :
> docker run --name {container_name} -d -e MONGO_INITDB_ROOT_USERNAME={user} -e MONGO_INITDB_ROOT_PASSWORD={password} -v {host_data_volume}:/etc/mongo mongo:latest
