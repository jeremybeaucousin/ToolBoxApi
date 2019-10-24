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

### Docker
#### Create network
> docker network create ToolBoxNetwork
## MongoDB
### Docker
#### Créate container :
> docker run --name {container_name} -d --net ToolBoxNetwork -e MONGO_INITDB_ROOT_USERNAME={user} -e MONGO_INITDB_ROOT_PASSWORD={password} -v {host_data_volume}:/etc/mongo -p "{host_port}:{container_port}" mongo:latest

Primary :
> docker run --name mongo-toolboxapi -d -e MONGO_INITDB_ROOT_USERNAME=toolBoxAdmin -e MONGO_INITDB_ROOT_PASSWORD=toolBoxAdmin -v "C:\Users\JBEAUCOU\docker\volumes\ToolBoxApi\MongoDb\primary:/etc/mongo" -p "27017:27017" mongo:latest

### Indexes
#### Create index for text search on all fields
> db.collection.createIndex( { "$**": "text" } )

#### Search exemple
> db.articles.find( { $text: { $search: "Coffee test", $caseSensitive: true } } )
Search in values the word Coffee or test

> db.articles.find( { $text: { $search: "\"Café Con Leche\"", $caseSensitive: true } } )
Search in values the phrase

> db.toolBoxSheets.find( { $text: { $search: "jbeaucousin" } }, { score: { $meta: "textScore" } } ).sort( { score: { $meta: "textScore" } } )
> Search by revelance

## Elastic search
### Docker 
> docker run -d --name elasticsearchToolBox --net ToolBoxNetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.4.0

### Mongo connector
#### Create container
> docker run -d -it --name mongoConnectorToolBox --net ToolBoxNetwork python:3.6-stretch

#### installation
> pip install --trusted-host pypi.org --trusted-host files.pythonhosted.org mongo-connector

#### start
 mongo-connector -m mongo-toolboxapi:27017 -t elasticsearchToolBox:9200 -d elastic_doc_manager

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

### Start dev server
> sbt "run 9001"
Default url : http://localhost:9000/
in https
> sbt "run -Dhttp.port=disabled -Dhttps.port=9001 -Dhttps.keyStore=C:\Users\JBEAUCOU\catalogapi.jks -Dhttps.keyStorePassword=jbeauc
ou"
Default url : https://localhost:9001/
Production :
> ./bin/your-app -Dhttp.port=disabled -Dhttps.port=9443 -Dplay.server.https.keyStore.path=/path/to/keystore -Dplay.server.https.keyStore.password=changeme

### Unit Test
Launch unit test :
> sbt test

### Integration with IDE
#### Eclipse 
> sbt compile
> sbt eclipse