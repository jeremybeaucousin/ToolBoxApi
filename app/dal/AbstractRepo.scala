package controllers

import javax.inject._

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

  val collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("toolBoxSheets"))
 
  def find(jsonQuery: JsObject) = { 
    val query = collection.map(_.find(jsonQuery))
    val cursor = query.map(_.cursor[JsObject]())
    cursor.flatMap(_.collect[List](Int.MaxValue, Cursor.FailOnError()))
  }
  
  
}
