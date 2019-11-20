package com.scalian.utils.deadbolt

import play.api.mvc.{ Request, AbstractController }

import be.objectify.deadbolt.scala.AuthenticatedRequest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.scalian.services.EncryptionService


trait UserMethods { self: AbstractController =>
  def user(req: AuthenticatedRequest[_], encryptionService: EncryptionService): Future[User] =
    ((new ApiDeadboltHandler(encryptionService)).
      getSubject(req).
      asInstanceOf[Future[Option[User]]]).map(
        maybeUser => maybeUser.get)
}