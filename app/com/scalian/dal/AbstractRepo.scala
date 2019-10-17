package com.scalian.dal

import javax.inject._

import play.api.Logger
import play.api.libs.json.{JsObject, Json, JsString}

import play.modules.reactivemongo._

import scala.concurrent.{ ExecutionContext, Future, Promise }

import reactivemongo.play.json._, collection._
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter

import reactivemongo.api.Cursor
import reactivemongo.api.ReadPreference
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONRegex}
import reactivemongo.api.commands.WriteResult

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AbstractRepo @Inject()
  (val reactiveMongoApi: ReactiveMongoApi)
  (implicit ec: ExecutionContext) {
  
  val logger: Logger = Logger(this.getClass())
  
  var collectionName: String = ""
  val collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection](collectionName))
 
  def find(jsonQuery: JsObject) = { 
    logger.debug(s"Call find for collection : $collectionName")
    val query = collection.map(_.find(jsonQuery).projection(Json.obj()))
    val cursor = query.map(_.cursor[JsObject]())
    cursor.flatMap(_.collect[List](Int.MaxValue, Cursor.FailOnError()))
  }
  
  def findById(id: String) = { 
    logger.debug(s"Call find By id for collection : $collectionName; with id : $id")
    val query = collection.map(_.find(createIdObject(id)).projection(Json.obj()))
    query.flatMap(_.one[JsObject])
  }
  
  def insert(jsonData: JsObject) = { 
    logger.debug(s"Call insert for collection : $collectionName; with data : $jsonData")
    val futureWriteResult = collection.flatMap(_.insert(jsonData))
    handleWriteResult(futureWriteResult)
  }
  
  def update(id: String, jsonData: JsObject) = { 
    logger.debug(s"Call update for collection : $collectionName; with id: $id; data : $jsonData")
    val futureWriteResult = collection.flatMap(_.update(createIdObject(id), jsonData))
    handleWriteResult(futureWriteResult)
  }
  
  def remove(id: String) = { 
    logger.debug(s"Call remove for collection : $collectionName; with id : $id")
    val futureWriteResult = collection.flatMap(_.remove(createIdObject(id)))
    handleWriteResult(futureWriteResult)
  }
  
  private def createIdObject(id: String) = {
    JsObject(Seq(
      "_id" -> JsObject(Seq(
              "$oid" -> JsString(id)
      ))
    ))
  }
  
  private def handleWriteResult(futureWriteResult: Future[WriteResult]): Future[Boolean] = {
    futureWriteResult.map(writeResult => {
      logger.debug(s"Return after writing is : $writeResult")
      WriteResult.lastError(writeResult) == None
    })
  }
  
}
