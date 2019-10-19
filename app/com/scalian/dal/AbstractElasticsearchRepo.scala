package com.scalian.dal

import javax.inject._

import play.api.Logger
import play.api.libs.ws._
import play.api.Configuration
import play.api.libs.json.{JsObject, Json, JsString}

import scala.concurrent.{ ExecutionContext, Future, Promise }

abstract class AbstractElasticsearchRepo @Inject() (
    config: Configuration, 
    ws: WSClient
    ) (implicit ec: ExecutionContext)  {
 
  val logger: Logger = Logger(this.getClass())
  
  private final object routes {
    final val search = "_search"
  }
  
  private final object responseKeys {
    final val took = "took"
    final val timed_out = "timed_out"
    final object _shards {
      final val KEY = "_shards"
      final val total = "total"
      final val successful = "successful"
      final val skipped = "skipped"
      final val failed = "failed"
    }
    final object hits {
      final val KEY = "hits"
      
      final object total {
        final val KEY = "total"
        final val total = "total"
        final val relation = "relation"
      }
      final val max_score = "max_score"
      final val hits = "hits"
    }
  }
  
  var repoUrl = config.get[String]("elasticsearch.url")
  var catalogRoute = config.get[String]("elasticsearch.route.catalog")
  var indexRoute: String = ""
 
  def find(jsonQuery: JsObject) = { 
    val uri = s"${getUri()}${routes.search}"
    logger.debug(s"call find for uri ${uri} with query ${jsonQuery}")
    val request: WSRequest = ws.url(uri)
    request.get().map(response => {
      (response.json \ responseKeys.hits.KEY \ responseKeys.hits.hits).get
    })
  }
  
  def findById(id: String) = { 

  }
  
  def insert(jsonData: JsObject) = { 

  }
  
  def update(id: String, jsonData: JsObject) = { 

  }
  
  def remove(id: String) = { 

  }
  
  private def getUri() = {
    s"${repoUrl}${catalogRoute}${indexRoute}/"
  }
}
