package com.scalian.utils.deadbolt

import javax.inject.Inject

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{ AuthenticatedRequest, DynamicResourceHandler, DeadboltHandler }
import play.api.mvc.{ Results, Result, Request }
import play.api.mvc.Results._

import play.api.libs.json.{ JsObject, JsValue, Json }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.scalian.services.ApiConstants
import com.scalian.services.EncryptionService
import play.api.Logger


class ApiDeadboltHandler @Inject() (
  encryptionService: EncryptionService)
  extends DeadboltHandler {
  
  val logger: Logger = Logger(this.getClass())
   
  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future { None }

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future { None }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] =
    Future {
      request.subject.orElse {
        request.session.get(this.encryptionService.encrypt(ApiConstants.Session.userConnectedKey)) match {
          case Some(encryptedUser) =>
            // get from database, identity platform, cache, etc, if some
            // identifier is present in the request
            val decryptedUser = Json.parse(this.encryptionService.decrypt(encryptedUser))
            val login = (decryptedUser \ ApiConstants.Session.UserKeys.login).asOpt[String]
            val password = (decryptedUser \ ApiConstants.Session.UserKeys.password).asOpt[String]
            var user: User = new User(login.getOrElse(""), password.getOrElse(""))
            Some(user)
          case _ => None
        }
      }
    }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful(Unauthorized)
  }
}