package com.scalian.controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.libs.json.{ JsObject, JsValue, Json }

import scala.concurrent.Future
import scala.concurrent.{ ExecutionContext, Future, Promise }

import be.objectify.deadbolt.scala.ActionBuilders

import com.hhandoko.play.pdf.PdfGenerator
import com.scalian.dal.ToolBoxDao
import com.scalian.services.ApiConstants
import com.scalian.services.EncryptionService
import com.scalian.utils.deadbolt.ApiDeadboltHandler
import com.scalian.utils.deadbolt.User
import com.scalian.utils.deadbolt.UserMethods

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ToolBoxController @Inject() (
  cc: ControllerComponents,
  action: DefaultActionBuilder,
  actionBuilder: ActionBuilders,
  encryptionService: EncryptionService,
  val toolBoxDao: ToolBoxDao,
  val pdfGen: PdfGenerator)(implicit ec: ExecutionContext)
  extends AbstractController(cc)
  with UserMethods {

  val logger: Logger = Logger(this.getClass())

  def documentation() = action { request =>
    Ok(views.html.index())
  }

  def find(optionalWordSequence: Option[String], optionalOffset: Option[Int], optionalLimit: Option[Int], optionalsort: Option[String]) = Action.async {
    implicit request: Request[AnyContent] =>
      val wordSequence: String = optionalWordSequence.getOrElse(null)
      val offset: Int = optionalOffset.getOrElse(-1)
      val limit: Int = optionalLimit.getOrElse(-1)
      val sort: String = optionalsort.getOrElse(null)
      toolBoxDao.find(null, wordSequence, offset, limit, sort).map({
        case (total, toolBoxSheets, error) => {
          if (error) {
            InternalServerError(Json.toJson(toolBoxSheets))
              .withSession(request.session)
          } else {
            Ok(Json.toJson(toolBoxSheets))
              .withHeaders(ControllerConstants.HeaderFields.xTotalCount -> total.toString())
              .withSession(request.session)
          }
        }
      })
  }

  def getToolBoxSheet(id: String) =
    actionBuilder.SubjectPresentAction().defaultHandler() {
      request =>
        user(request, encryptionService).flatMap { user =>
          toolBoxDao.findById(user, id).map({
            case (found, toolBoxSheet) => {
              if (found) {
                Ok(Json.toJson(toolBoxSheet))
                  .withSession(request.session)
              } else {
                NotFound(Json.toJson(toolBoxSheet))
                  .withSession(request.session)
              }
            }
          })
        }
    }

  def addToolBoxSheet() =
    actionBuilder.SubjectPresentAction().defaultHandler() {
      request =>
        {
          user(request, encryptionService).flatMap { user =>
            val jsonBody: Option[JsValue] = request.body.asJson
            val json = jsonBody.getOrElse(null)
            // If there is a body we continue
            // else in case of empty body or write error send code error
            if (json != null) {
              val data = json.as[JsObject]
              toolBoxDao.insert(user, data).map({
                case (id, jsonResponse) => {
                  if (id != null) {
                    var returnedLocation = ControllerConstants.HeaderFields.location -> (routes.ToolBoxController.getToolBoxSheet(id).toString())
                    Created(Json.toJson(jsonResponse))
                      .withHeaders(returnedLocation)
                      .withSession(request.session)
                  } else {
                    InternalServerError(Json.toJson(jsonResponse))
                      .withSession(request.session)
                  }
                }
              })
            } else {
              Future.successful(
                BadRequest(Json.parse(ControllerConstants.noJsonMessage))
                  .withSession(request.session))
            }
          }
        }
    }

  def editToolBoxSheet(id: String) =
    actionBuilder.SubjectPresentAction().defaultHandler() {
      request =>
        user(request, encryptionService).flatMap { user =>
          val jsonBody: Option[JsValue] = request.body.asJson
          val json = jsonBody.getOrElse(null)
          // If there is a body we continue
          // else in case of empty body or write error send code error
          if (json != null) {
            val data = json.as[JsObject]
            toolBoxDao.update(user, id, data).map({
              case (updated, jsonResponse) => {
                if (updated) {
                  Ok(Json.toJson(jsonResponse))
                    .withSession(request.session)
                } else {
                  InternalServerError(Json.toJson(jsonResponse))
                    .withSession(request.session)
                }
              }
            })
          } else {
            Future.successful(
              BadRequest(Json.parse(ControllerConstants.noJsonMessage))
                .withSession(request.session))
          }
        }
    }

  def deleteToolBoxSheet(id: String) =
    actionBuilder.SubjectPresentAction().defaultHandler() { request =>
      user(request, encryptionService).flatMap { user =>
        toolBoxDao.remove(user, id).map({
          case (updated, jsonResponse) => {
            if (updated) {
              Ok(Json.toJson(jsonResponse))
                .withSession(request.session)
            } else {
              NotFound(Json.toJson(jsonResponse))
                .withSession(request.session)
            }
          }
        })
      }
    }
}
