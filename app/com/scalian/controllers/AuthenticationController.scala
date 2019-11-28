package com.scalian.controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.cache._

import play.api.libs.json.{ JsObject, JsValue, Json }

import scala.concurrent.Future
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.Random
import scala.concurrent.duration._

import java.util.Arrays

import com.google.common.io.BaseEncoding

import com.scalian.utils.enums.ConfigurationsEnum
import com.scalian.services.EncryptionService
import com.scalian.services.AuthenticationService
import com.scalian.services.ApiConstants

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthenticationController @Inject() (
  cc: ControllerComponents,
  config: Configuration,
  encryptionService: EncryptionService,
  authenticationService: AuthenticationService,
  action: DefaultActionBuilder)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass())

  private final val sessionMaxAge = config.get[Int](s"${ConfigurationsEnum.play.KEY}.${ConfigurationsEnum.play.http.KEY}.${ConfigurationsEnum.play.http.session.KEY}.${ConfigurationsEnum.play.http.session.maxAge}")
  private final val secretKey = config.get[String](s"${ConfigurationsEnum.play.KEY}.${ConfigurationsEnum.play.http.KEY}.${ConfigurationsEnum.play.http.secret.KEY}.${ConfigurationsEnum.play.http.secret.key}")

  this.encryptionService.secretKey = secretKey

  private final val encryptedUserConnectedKey = this.encryptionService.encrypt(ApiConstants.Session.userConnectedKey)

  def documentation() = action { request =>
    Ok(views.html.index())
  }

  def login() = action.async { implicit request: Request[AnyContent] =>
    logger.debug(request.toString())
    val authHeader = request.headers.get("Authorization").getOrElse(null)

    if (authHeader != null) {
      // Get Basic auth value
      val baStr = authHeader.replaceFirst("Basic ", "")
      // decode value
      val decoded = BaseEncoding.base64().decode(baStr)
      // Store user password into variables
      val Array(user, password) = new String(decoded).split(":")
      authenticationService.login(user, password).map(
        jsonResponse => {
          if (jsonResponse != null) {
            // Create Json object
            val userConnected = Json.obj(
              ApiConstants.Session.UserKeys.login -> user,
              ApiConstants.Session.UserKeys.password -> password)
            // Encrypt object
            val encryptedUser = this.encryptionService.encrypt(
              Json.stringify(userConnected))
            // Store in session
            //        TODO CALL elasticsearch for authentication
            Ok(jsonResponse)
              .withSession(encryptedUserConnectedKey -> encryptedUser)
          } else {
            val response: play.api.mvc.Result = Unauthorized(
              Json.parse("{\"message\": \"Couple login/password invalid\"}"))
            response
          }
        })

    } else {
      val response: play.api.mvc.Result = BadRequest(
        Json.parse("{\"message\": \"No authentication provided\"}"))
      Future.successful(response)
    }
  }

  def logout() = action.async { implicit request: Request[AnyContent] =>
    val requestSession: play.api.mvc.Session = request.session
    requestSession.get(encryptedUserConnectedKey)
      .map { user =>
        Future.successful(Ok.withNewSession)
      }
      .getOrElse {
        Future.successful(Unauthorized)
      }
  }
}
