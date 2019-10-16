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
* PlayFramework :
 * https://www.playframework.com/ 

## MongoDB
### Docker
#### Créate container :
> docker run --name {container_name} -d -e MONGO_INITDB_ROOT_USERNAME={user} -e MONGO_INITDB_ROOT_PASSWORD={password} -v {host_data_volume}:/etc/mongo -p "{host_port}:{container_port}" mongo:latest

> docker run --name mongo-toolboxapi -d -e MONGO_INITDB_ROOT_USERNAME=toolBoxAdmin -e MONGO_INITDB_ROOT_PASSWORD=toolBoxAdmin -v "C:\Users\JBEAUCOU\docker\volumes\ToolBoxApi\MongoDb\data:/etc/mongo" -p "27017:27017" mongo:latest

#### Enter container
> docker exec -it mongo-toolboxapi "/bin/bash"

## Java
### SSL
Add certificate in case of :
> sun.security.provider.certpath.SunCertPathBuilderException

#### Keytool
> "%JAVA_HOME%\bin\keytool" -cacerts -import -file {nom_fichier} -alias {nom_alias}
Default password : "changeit"

## Play Framework
### Init project
> sbt new playframework/play-scala-seed.g8
Set project name and domain

### Start server
> sbt "run 9001"
Default url : http://localhost:9000/

### Unit Test
Launch unit test :
> sbt test