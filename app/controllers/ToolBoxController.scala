package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.mvc.Controller
import play.api.libs.json.{JsObject, JsValue, Json}

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
class ToolBoxController @Inject() (
    cc: ControllerComponents, 
    val toolBoxDao: ToolBoxDao)
    (implicit ec: ExecutionContext) 
  extends AbstractController(cc) {
  
  val logger: Logger = Logger(this.getClass())
  
  def documentation() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  
  def index() = Action.async { implicit request: Request[AnyContent] =>
    toolBoxDao.find().map({
      case (toolBoxSheets) => {
        Ok(Json.toJson(toolBoxSheets))
      }
    })
  }
  
  def getToolBoxSheet(id: String) = Action.async { implicit request: Request[AnyContent] =>
    render.async {
      case Accepts.Json() => {
        toolBoxDao.findById(id).map({
          case (toolBoxSheet) => {
            logger.debug(s"Result for id : $id; $toolBoxSheet")
            if(toolBoxSheet != None) {
              Ok(Json.toJson(toolBoxSheet))  
            } else {
              NotFound(Json.toJson(Json.obj()))
            }
          }
        })
      }
      case ControllerConstants.AcceptsPdf() => {
        logger.debug(s"Test pdf")
        Future.successful(Ok("Test pdf"))
      }
    }
  }
  
  def addToolBoxSheet() = Action.async { implicit request: Request[AnyContent] =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val json = jsonBody.getOrElse(null)
    // If there is a body we continue  
    // else in case of empty body or write error send code error     
    if(json != null) {
      val data = json.as[JsObject]
      toolBoxDao.insert(data).map({
        case (writeOk) => {
          if(writeOk) {
            var returnedLocation = ControllerConstants.HeaderFields.location -> (routes.ToolBoxController.getToolBoxSheet("").absoluteURL())
            Created.withHeaders(returnedLocation)
          } else {
            UnprocessableEntity
          }
        }
      })
    } else {
      Future.successful(UnprocessableEntity)
    }
  }
  
  def deleteToolBoxSheet(id: String) = Action.async { implicit request: Request[AnyContent] =>
    toolBoxDao.remove(id).map({
      case (writeOk) => {
        if(writeOk) {
          Ok
        } else {
          UnprocessableEntity
        }
      }
    })
  }
}
