package com.scalian.controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.cache._

import play.api.libs.json.{ JsObject, JsValue, Json }

import scala.concurrent.Future
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.Random
import scala.concurrent.duration._

import com.google.common.io.BaseEncoding
import java.security.{ SecureRandom }

import com.scalian.utils.enums.ConfigurationsEnum
import play.cache.NamedCache

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthenticationController @Inject() (
  cc: ControllerComponents,
  @NamedCache("user-cache") cache: AsyncCacheApi,
  config: Configuration,
  action: DefaultActionBuilder)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass())

  private final val SECURE_RANDOM: SecureRandom = new SecureRandom()
  private final val RANDOM: Random = new Random()

  private final val sessionMaxAge = config.get[Int](s"${ConfigurationsEnum.play.KEY}.${ConfigurationsEnum.play.http.KEY}.${ConfigurationsEnum.play.http.session.KEY}.${ConfigurationsEnum.play.http.session.maxAge.KEY}")

  def login() = Action.async { implicit request: Request[AnyContent] =>
    logger.debug(request.toString())
    val authHeader = request.headers.get("Authorization").getOrElse(null)
    
    logger.debug("configuration : " + config.get[String](s"play.http.secret.key"))
    
    val sessionId = newSessionId
    val secret = newSecretKey

    val requestSession = request.session
    if (requestSession != null) {
      requestSession.get("sessionId")
    }

    logger.debug(sessionId)
    logger.debug(request.session.toString())

    if (authHeader != null) {
      val baStr = authHeader.replaceFirst("Basic ", "")
      val decoded = BaseEncoding.base64().decode(baStr)
      val Array(user, password) = new String(decoded).split(":")
      Future.successful(Ok(Json.toJson((user, password)))
        .withSession("connectedUser" -> Json.stringify(
          Json.toJson(
            (user, password)))))
    } else {
      // Save user Tuplle2 in cache with session maxAge duration
      //  cache.set(sessionId, ("user", "password"), Duration(sessionMaxAge, SECONDS))
      cache.set(sessionId, ("user", "password"), Duration(10, SECONDS))
      // Save id in session (cookie)
      val response: play.api.mvc.Result = BadRequest(
        Json.parse("{\"message\": \"No authentication provided\"}")).withSession(
          "sessionId" -> sessionId)
      Future.successful(response)
    }
  }

  def logout() = Action.async { implicit request: Request[AnyContent] =>
    //  Retrieve Session id from session
    val requestSession: play.api.mvc.Session = request.session
    logger.debug("session : " + request.session)
    
    requestSession.get("sessionId")
      .map { sessionId =>
        logger.debug("sessionId : " + sessionId)
        cache.get(sessionId).map {
          optUser =>
            {
              if (optUser.nonEmpty) {
                val user: Tuple2[String, String] = optUser.get
                logger.debug(user._1)
                logger.debug(user._2)
              }

            }
        }
        //        Future.successful(Ok.withNewSession)
        Future.successful(Ok.withSession(requestSession))
      }
      .getOrElse {
        Future.successful(Unauthorized("Oops, you are not connected"))
      }
  }

  def newSessionId = {
    Random.alphanumeric.take(100).mkString
  }

  def newSecretKey = {
    // Key must be 32 bytes for secretbox
    val array: Array[Byte] = SECURE_RANDOM.generateSeed(32)
    val stringBuilder = new StringBuilder();
    for (byte <- array) {
      stringBuilder.append(Integer.toString((byte & 0xff) + 0x100, 16).substring(1));
    }
    stringBuilder.toString();
  }

}
