package com.scalian.utils

import javax.inject.Inject
import play.api.http._
import play.api.mvc._

import play.api.Logger
import play.api.routing.Router
import play.api.Configuration
import play.api.libs.ws.WSRequestFilter
import play.api.libs.ws.WSRequestExecutor
import com.scalian.utils.enums.ConfigurationsEnum
import play.api.libs.ws.WSRequest
import java.util.Base64

class ElasticSearchRequestFilter(
  config: Configuration) extends WSRequestFilter {
  val logger: Logger = Logger(this.getClass())
  
  private final val elasticSearchKey = ConfigurationsEnum.elasticsearch.KEY
  
  private final object HeadersKey {
    final val Authorization = "Authorization" 
    final val ApiKey = "ApiKey" 
  }
  
  def apply(executor: WSRequestExecutor): WSRequestExecutor = {
    WSRequestExecutor { request =>
      
      // Authentication 
      val apiKey = config.get[String](s"${elasticSearchKey}.${ConfigurationsEnum.elasticsearch.apikey}")
      val encodedApiKey = new String(Base64.getEncoder.encode(apiKey.getBytes()))
      var newRequest = request.addHttpHeaders(HeadersKey.Authorization -> s"${HeadersKey.ApiKey} ${encodedApiKey}");

      // In case of certificate name error SSLHandshakeException: No name matching {host} found
      val virtualhost = config.get[String](s"${elasticSearchKey}.${ConfigurationsEnum.elasticsearch.certificat.KEY}.${ConfigurationsEnum.elasticsearch.certificat.virtualhost}")
      newRequest = newRequest.withVirtualHost(virtualhost)
      executor(newRequest)
    }
  }
}

object ElasticSearchRequestFilter {

  def apply(config: Configuration): ElasticSearchRequestFilter = {
    new ElasticSearchRequestFilter(config)
  }

}