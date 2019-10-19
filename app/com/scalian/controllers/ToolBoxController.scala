package com.scalian.controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.mvc.Controller
import play.api.libs.json.{ JsObject, JsValue, Json }

import play.modules.reactivemongo._

import scala.concurrent.Future
import scala.concurrent.{ ExecutionContext, Future, Promise }

import reactivemongo.play.json._, collection._
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter

import reactivemongo.api.Cursor
import reactivemongo.api.ReadPreference
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{ BSONDocument, BSONRegex }

import com.hhandoko.play.pdf.PdfGenerator

import com.scalian.dal.ToolBoxDao

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ToolBoxController @Inject() (
  cc: ControllerComponents,
  val toolBoxDao: ToolBoxDao,
  val pdfGen: PdfGenerator)
  (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass())

  def documentation() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def find(optionalWordSequence: Option[String], optionalOffset: Option[Int], optionalLimit: Option[Int]) = Action.async { implicit request: Request[AnyContent] =>
    val wordSequence: String = optionalWordSequence.getOrElse(null)
    val offset: Int = optionalOffset.getOrElse(-1)
    val limit: Int = optionalLimit.getOrElse(-1)
    toolBoxDao.find(wordSequence, offset, limit).map({
      case (total: Int, toolBoxSheets: JsValue) => {
        Ok(Json.toJson(toolBoxSheets)).withHeaders(ControllerConstants.HeaderFields.xTotalCount -> total.toString())
      }
    })
  }

  def getToolBoxSheet(id: String) = Action.async { implicit request: Request[AnyContent] =>
    render.async {
      case Accepts.Json() => {
        toolBoxDao.findById(id).map({
          case (found, toolBoxSheet) => {
            if (found) {
              Ok(Json.toJson(toolBoxSheet))
            } else {
              NotFound(Json.toJson(toolBoxSheet))
            }
          }
        })
      }
      case ControllerConstants.AcceptsPdf() => {
        Future.successful(pdfGen.ok(views.html.index(), request.host))
      }
    }
  }
//
//  def editToolBoxSheet(id: String) = Action.async { implicit request: Request[AnyContent] =>
//    val jsonBody: Option[JsValue] = request.body.asJson
//    val json = jsonBody.getOrElse(null)
//    // If there is a body we continue
//    // else in case of empty body or write error send code error
//    if (json != null) {
//      val data = json.as[JsObject]
//      toolBoxDao.update(id, data).map({
//        case (writeOk) => {
//          if (writeOk) {
//            Ok
//          } else {
//            UnprocessableEntity
//          }
//        }
//      })
//    } else {
//      Future.successful(UnprocessableEntity)
//    }
//  }
//
//  def addToolBoxSheet() = Action.async { implicit request: Request[AnyContent] =>
//    val jsonBody: Option[JsValue] = request.body.asJson
//    val json = jsonBody.getOrElse(null)
//    // If there is a body we continue
//    // else in case of empty body or write error send code error
//    if (json != null) {
//      val data = json.as[JsObject]
//      toolBoxDao.insert(data).map({
//        case (writeOk) => {
//          if (writeOk) {
//            var returnedLocation = ControllerConstants.HeaderFields.location -> (routes.ToolBoxController.getToolBoxSheet("").absoluteURL())
//            Created.withHeaders(returnedLocation)
//          } else {
//            UnprocessableEntity
//          }
//        }
//      })
//    } else {
//      Future.successful(UnprocessableEntity)
//    }
//  }
//
//  def deleteToolBoxSheet(id: String) = Action.async { implicit request: Request[AnyContent] =>
//    toolBoxDao.remove(id).map({
//      case (writeOk) => {
//        if (writeOk) {
//          Ok
//        } else {
//          UnprocessableEntity
//        }
//      }
//    })
//  }
}
