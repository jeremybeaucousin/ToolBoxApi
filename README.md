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

### Mongo connector
#### Create container
> docker run -d -it --name mongoConnectorToolBox --net ToolBoxNetwork python:3.6-stretch

#### installation
> pip install --trusted-host pypi.org --trusted-host files.pythonhosted.org mongo-connector

#### start
 mongo-connector -m mongo-toolboxapi:27017 -t elasticsearchToolBox:9200 -d elastic_doc_manager

#### Enter container
> docker exec -it mongo-toolboxapi "/bin/bash"

## Elastic search
### Docker 
Create container :
> docker run -d --name elasticsearchToolBox --net ToolBoxNetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.4.0
> docker run -d --name elasticsearchToolBoxApi -p 9200:9200 -p 9300:9300 -v "C:\Users\a\DockerVolume\ToolBoxApi\elasticsearch\data:/usr/share/elasticsearch/data" -e "discovery.type=single-node" elasticsearch:7.4.0

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

### Secret key
generate key :
> sbt :
in console
> playGenerateSecret

### Deployments
#### universal
packaging 
> sbt
in console
> dist

deploy
> unzip toolboxapi-1.0-SNAPSHOT.zip

launch :
 * linux
toolboxapi-1.0-SNAPSHOT/bin/toolboxapi -Dplay.http.secret.key=ad31779d4ee49d5ad5162bf1429c32e2e9933f3b
 * windows
no -Dhttp.address= for remote connexion
toolboxapi-1.0-SNAPSHOT\bin\toolboxapi.bat -Dplay.http.secret.key="ZEy5cO:YgQ@Bb^P5/P[Y?HD^arVgZYJ25dEybZR;n9bfDAL3VXky5NMN><=YehlJ" -Dhttp.port=disabled -Dhttps.port=9001 -Dconfig.file=conf/application.conf -Dhttp.address=127.0.0.1

issue :
```
The input line is too long.
The syntax of the command is incorrect.
Line 83 : 
set "APP_CLASSPATH=%APP_LIB_DIR%\..\conf\;%APP_LIB_DIR%\*"
```

#### sbt assembly plugin
> sbt
in console
> assembly
launch 
> java -Dplay.http.secret.key="ZEy5cO:YgQ@Bb^P5/P[Y?HD^arVgZYJ25dEybZR;n9bfDAL3VXky5NMN><=YehlJ" -Dhttp.port=9000 -Dhttp.address=127.0.0.1 -jar scala-2.13\toolboxapi_2.13-1.0-SNAPSHOT-web-assets.jar

### Unit Test
Launch unit test :
> sbt test

### Integration with IDE
#### Eclipse 
> sbt compile
> sbt eclipse
> 