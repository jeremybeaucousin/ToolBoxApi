package controllers

import javax.inject._

import play.api.Logger
import play.api.libs.json.{JsObject, Json}

import play.modules.reactivemongo._

import scala.concurrent.{ ExecutionContext, Future, Promise }

import reactivemongo.play.json._, collection._
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter

import reactivemongo.api.Cursor
import reactivemongo.api.ReadPreference
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONRegex}

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
  
  
}
