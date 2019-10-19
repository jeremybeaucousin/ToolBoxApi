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
  
  private final object queryParams {
    final val q = "q"
    final val from = "from"
    final val size = "size"
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
        final val value = "value"
        final val relation = "relation"
      }
      final val max_score = "max_score"
      final val hits = "hits"
    }
  }
  
  val repoUrl = config.get[String]("elasticsearch.url")
  var indexRoute: String = ""
 
  def find(wordSequence: String, offset: Int, limit: Int) = { 
    val uri = s"${getUri()}${routes.search}"
    
    var request: WSRequest = ws.url(uri)
    // Add query param   
    if(wordSequence != null && !wordSequence.isBlank()) {
      request = request.addQueryStringParameters(queryParams.q -> s"*${wordSequence}*")
    }
    
    if(offset > -1) {
      request = request.addQueryStringParameters(queryParams.from -> offset.toString())
    }
        
    if(limit > -1) {
      request = request.addQueryStringParameters(queryParams.size -> limit.toString())
    }
    logger.debug(s"call find for uri ${uri} with request ${request}")
    request.get().map(response => {
      handleResponse(response)
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
    s"${repoUrl}${indexRoute}/"
  }
  
  private def handleResponse(response: WSResponse) = {
    val jsonResponse = response.json
    val hits = (jsonResponse \ responseKeys.hits.KEY).get
    val total = (hits \ responseKeys.hits.total.KEY \ responseKeys.hits.total.value).get.as[Int]
    val results = (hits \ responseKeys.hits.hits).get
    (total, results)
  }
}