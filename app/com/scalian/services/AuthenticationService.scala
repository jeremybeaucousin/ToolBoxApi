package com.scalian.services

import javax.inject.Singleton
import javax.inject.Inject
import java.util.Base64

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.http.Status

import play.api.Logger
import play.api.Configuration

import play.api.libs.ws.WSClient

import com.scalian.utils.enums.ConfigurationsEnum
import com.scalian.controllers.ControllerConstants.HeadersKey

@Singleton
class AuthenticationService @Inject() (
  config: Configuration,
  ws: WSClient)(implicit ec: ExecutionContext) {

  private val logger: Logger = Logger(this.getClass())

  val authApi = config.get[String](s"${ConfigurationsEnum.elasticsearch.KEY}.${ConfigurationsEnum.elasticsearch.url}")
  val securityRoute = config.get[String](s"${ConfigurationsEnum.elasticsearch.KEY}.${ConfigurationsEnum.elasticsearch.routes.KEY}.${ConfigurationsEnum.elasticsearch.routes._security}")
  val authenticationRoute = config.get[String](s"${ConfigurationsEnum.elasticsearch.KEY}.${ConfigurationsEnum.elasticsearch.routes.KEY}.${ConfigurationsEnum.elasticsearch.routes._authenticate}")

  def login(user: String, password: String): Future[Boolean] = {
    val uri = s"${authApi}${securityRoute}${authenticationRoute}"
    var request = ws.url(uri)

    val encodedCredentials = new String(Base64.getEncoder.encode(s"${user}:${password}".getBytes()))
    var newRequest = request.addHttpHeaders(HeadersKey.Authorization -> s"${HeadersKey.Basic} ${encodedCredentials}");

    // In case of certificate name error SSLHandshakeException: No name matching {host} found
    val virtualhost = config.get[String](s"${ConfigurationsEnum.elasticsearch.KEY}.${ConfigurationsEnum.elasticsearch.certificat.KEY}.${ConfigurationsEnum.elasticsearch.certificat.virtualhost}")
    newRequest = newRequest.withVirtualHost(virtualhost)

    logger.debug(s"try to autenticate for user ${user} with request ${newRequest}")

    newRequest.get().map(response => {
      val json = response.json
      if (response.status == Status.OK) {
        logger.debug("authenticated")
        true
      } else {
        logger.debug("not authenticated")
        false
      }
    })
  }
}