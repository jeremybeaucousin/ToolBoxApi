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

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthenticationController @Inject() (
  cc: ControllerComponents,
  config: Configuration,
  encryptionService: EncryptionService,
  action: DefaultActionBuilder)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass())

  private final val sessionMaxAge = config.get[Int](s"${ConfigurationsEnum.play.KEY}.${ConfigurationsEnum.play.http.KEY}.${ConfigurationsEnum.play.http.session.KEY}.${ConfigurationsEnum.play.http.session.maxAge}")
  private final val secretKey = config.get[String](s"${ConfigurationsEnum.play.KEY}.${ConfigurationsEnum.play.http.KEY}.${ConfigurationsEnum.play.http.secret.KEY}.${ConfigurationsEnum.play.http.secret.key}")
  
  this.encryptionService.secretKey = secretKey
  
  private final val userConnectedKey = "connectedUser"
  private final val encryptedUserConnectedKey = this.encryptionService.encrypt(userConnectedKey)
  
  def login() = Action.async { implicit request: Request[AnyContent] =>
    logger.debug(request.toString())
    val authHeader = request.headers.get("Authorization").getOrElse(null)
    
    logger.debug("configuration : " + secretKey)
    logger.debug(request.session.toString())
    val user = Json.toJson(("user", "password"))
    val encryptedText = this.encryptionService.encrypt(Json.stringify(user))
    logger.debug("encryptedText : " + encryptedText)
    val decryptedKey = this.encryptionService.decrypt(encryptedText)
    logger.debug("decryptedKey : " + decryptedKey)
    if (authHeader != null) {
      val baStr = authHeader.replaceFirst("Basic ", "")
      val decoded = BaseEncoding.base64().decode(baStr)
      val Array(user, password) = new String(decoded).split(":")
      Future.successful(Ok(Json.toJson((user, password)))
        .withSession(encryptedUserConnectedKey -> Json.stringify(
          Json.toJson(
            (user, password)))))
    } else {
      val response: play.api.mvc.Result = BadRequest(
        Json.parse("{\"message\": \"No authentication provided\"}")).withSession(
          encryptedUserConnectedKey -> encryptedText)
      Future.successful(response)
    }
  }

  def logout() = Action.async { implicit request: Request[AnyContent] =>
    //  Retrieve Session id from session
    val requestSession: play.api.mvc.Session = request.session
    logger.debug("session : " + request.session)
    
    requestSession.get(encryptedUserConnectedKey)
      .map { user =>
        val decryptedUser = this.encryptionService.decrypt(user)
        logger.debug("user : " + decryptedUser)
        //        Future.successful(Ok.withNewSession)
        Future.successful(Ok.withSession(requestSession))
      }
      .getOrElse {
        Future.successful(Unauthorized("Oops, you are not connected"))
      }
  }
}
