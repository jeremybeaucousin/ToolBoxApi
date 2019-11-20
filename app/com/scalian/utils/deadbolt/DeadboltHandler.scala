package com.scalian.utils.deadbolt

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{ AuthenticatedRequest, DynamicResourceHandler, DeadboltHandler }
import play.api.mvc.{ Results, Result, Request }
import play.api.mvc.Results._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiDeadboltHandler extends DeadboltHandler {
  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future { None }

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future { None }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] =
    Future {
      request.subject.orElse {
        // replace request.session.get("userId") with how you identify the user
        request.session.get("userId") match {
          case Some(userId) =>
            // get from database, identity platform, cache, etc, if some
            // identifier is present in the request
            None
          case _ => None
        }
      }
    }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    Future.successful(Unauthorized("Oops, you are not connected"))
    //
    //    def toContent(maybeSubject: Option[Subject]): (Boolean, HtmlFormat.Appendable) =
    //      maybeSubject.map(subject =>
    //        {
    //          (true, None)
    //          //          (true, denied(Some(subject)))
    //        })
    //        .getOrElse {
    //          (false, None)
    //          //          (false, login(clientId, domain, redirectUri))
    //        }
    //
    //    getSubject(request).map(
    //      maybeSubject => toContent(maybeSubject))
    //      .map(
    //        subjectPresentAndContent =>
    //          if (subjectPresentAndContent._1) {
    //            Results.Forbidden(subjectPresentAndContent._2)
    //          } else {
    //            Results.Unauthorized(subjectPresentAndContent._2)
    //          })
  }
}