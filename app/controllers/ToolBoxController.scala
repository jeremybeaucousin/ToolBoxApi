package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.mvc.Controller
import play.api.libs.json.{JsObject, Json}

import play.modules.reactivemongo._

import scala.concurrent.Future
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
class ToolBoxController @Inject() (cc: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi)(
    implicit ec: ExecutionContext) 
  extends AbstractController(cc) 
    with MongoController with ReactiveMongoComponents {
  
  val collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("toolBoxSheets"))
 
  def index() = Action.async { implicit request: Request[AnyContent] =>
    val query = collection.map(_.find(Json.obj()))
    val cursor = query.map(_.cursor[JsObject]())
    val result = cursor.flatMap(_.collect[List](Int.MaxValue, Cursor.FailOnError()))
    result.map({
      case (toolBoxSheets) => {
        Ok(Json.toJson(toolBoxSheets))
      }
    })
  }
  
  
}
