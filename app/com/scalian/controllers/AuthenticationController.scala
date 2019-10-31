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

import java.util.Arrays
import java.util.Base64

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

import com.google.common.io.BaseEncoding

import com.scalian.utils.enums.ConfigurationsEnum


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AuthenticationController @Inject() (
  cc: ControllerComponents,
  config: Configuration,
  action: DefaultActionBuilder)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass())

  private final val sessionMaxAge = config.get[Int](s"${ConfigurationsEnum.play.KEY}.${ConfigurationsEnum.play.http.KEY}.${ConfigurationsEnum.play.http.session.KEY}.${ConfigurationsEnum.play.http.session.maxAge}")
  private final val secretKey = config.get[String](s"${ConfigurationsEnum.play.KEY}.${ConfigurationsEnum.play.http.KEY}.${ConfigurationsEnum.play.http.secret.KEY}.${ConfigurationsEnum.play.http.secret.key}")
  
  private final val Algorithm = "AES/CBC/PKCS5Padding"
  private final val secretKeyDigest = MessageDigest.getInstance("SHA-1").digest(secretKey.getBytes)
  private final val Key = new SecretKeySpec(Arrays.copyOf(secretKeyDigest, 16), "AES")
  private final val IvSpec = new IvParameterSpec(new Array[Byte](16))

  private final val userConnectedKey = "connectedUser"
  private final val encryptedUserConnectedKey = encrypt(userConnectedKey)
  
  def login() = Action.async { implicit request: Request[AnyContent] =>
    logger.debug(request.toString())
    val authHeader = request.headers.get("Authorization").getOrElse(null)
    
    logger.debug("configuration : " + secretKey)
    logger.debug(request.session.toString())
    logger.debug(new String(Arrays.copyOf(secretKeyDigest, 16)))
    val user = Json.toJson(("user", "password"))
    val encryptedText = encrypt(Json.stringify(user))
    logger.debug("encryptedText : " + encryptedText)
    val decryptedKey = decrypt(encryptedText)
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
        val decryptedUser = decrypt(user)
        logger.debug("user : " + decryptedUser)
        //        Future.successful(Ok.withNewSession)
        Future.successful(Ok.withSession(requestSession))
      }
      .getOrElse {
        Future.successful(Unauthorized("Oops, you are not connected"))
      }
  }
  
  def encrypt(text: String) = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)

    new String(Base64.getEncoder.encode(cipher.doFinal(text.getBytes("utf-8"))), "utf-8")
  }
  
  def decrypt(encodedText: String) = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)

    new String(cipher.doFinal(Base64.getDecoder.decode(encodedText.getBytes("utf-8"))), "utf-8")
  }
}
