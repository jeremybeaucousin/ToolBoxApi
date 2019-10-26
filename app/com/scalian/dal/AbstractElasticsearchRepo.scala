package com.scalian.dal

import javax.inject._

import play.api.Logger
import play.api.http.Status
import play.api.libs.ws._
import play.api.Configuration

import play.api.libs.json.{JsObject, Json, JsString, JsValue}

import scala.concurrent.{ ExecutionContext, Future, Promise }
import play.api.http.Status
import com.scalian.utils.enums.ConfigurationsEnum
import play.libs.ws.ahc.AhcCurlRequestLogger
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import com.scalian.utils.ElasticSearchRequestFilter

abstract class AbstractElasticsearchRepo @Inject() (
    config: Configuration, 
    ws: WSClient
    ) (implicit ec: ExecutionContext)  {
 
  val logger: Logger = Logger(this.getClass())
  
  private final object routes {
    final val search = "_search"
  }
  
  private final object sortType {
    final val asc = "asc"
    final val desc = "desc"
  }
  
  private final object queryParams {
    final val q = "q"
    final val from = "from"
    final val size = "size"
    final val sort = "sort"
  }
  
  private final object searchResponseKeys {
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
  
  private final object errorResponseKeys {
    // in array  
    final val error = "error"
    final object root_cause {
      final val KEY = "root_cause"
      final val TYPE = "type"
      final val reason = "reason"
    }
    final val TYPE = "type"
    final val reason = "reason"
    final val phase = "phase"
    final val grouped = "grouped"
    // in array    
    final object failed_shards {
      final val KEY = "failed_shards"
      final val shard = "shard"
      final val index = "index"
      final val node = "node"
      final object reason {
        final val KEY = "reason"
        final val TYPE = "type"
        final val reason = "reason"
      }
    }
  }
   
  private final object responseDocKeys {
    final val _index = "_index"
    final val _type = "_type"
    final val _id = "_id"
    final val found = "found"
    // if found    
    final val _version = "_version"
    final val _seq_no = "_seq_no"
    final val _primary_term = "_primary_term"
    final val _source = "_source"
  }
  
  private final val elasticSearchKey = ConfigurationsEnum.elasticsearch.KEY
  
  val repoUrl = config.get[String](s"${elasticSearchKey}.${ConfigurationsEnum.elasticsearch.url}")
  var indexRoute: String = ""
 
  def find(wordSequence: String, offset: Int, limit: Int, sort: String): Future[(Int, JsValue, Boolean)] = { 
    val uri = s"${getUri()}${routes.search}"
    var request: WSRequest = ws.url(uri)
    request = request.withRequestFilter(ElasticSearchRequestFilter(config))
    
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
    
    if(sort != null) {
      val lastChar = sort takeRight 1
      var sortValue: String = ""
      val field = sort.subSequence(0, (sort.length() - 1))
      if(lastChar.equals("-")) {
        sortValue = s"${field}:${sortType.desc}"
      } else if (lastChar.equals("+")) {
        sortValue = s"${field}:${sortType.asc}"
      } else {
        sortValue = s"${sort}:${sortType.asc}"
      }
      request = request.addQueryStringParameters(queryParams.sort -> sortValue)
    }

    logger.debug(s"call find for uri ${uri} with request ${request}")
    request.get().map(response => {
      val json = response.json
      if(response.status == Status.OK) {
        parseSearchResponse(json)
      } else {
        (0, json, true)

      }
    })
  }
  
  def findById(id: String) = { 
    val uri = s"${getUri()}${id}"
    var request: WSRequest = ws.url(uri)
    request = request.withRequestFilter(ElasticSearchRequestFilter(config))
    
    logger.debug(s"call find for uri ${uri} with request ${request}")
    request.get().map(response => {
      handleIdResponse(response)
    })
  }
  
  def insert(jsonData: JsObject): Future[(String, JsValue)] = { 
    val uri = s"${getUri()}"
    
    var request: WSRequest = ws.url(uri)
    request = request.withRequestFilter(ElasticSearchRequestFilter(config))
    
    logger.debug(s"call save for uri ${uri} with data ${jsonData}; request ${request}")
    request.post(jsonData).map(response => {
      val json = response.json
      if(response.status == Status.CREATED) {
        val id = (json \ responseDocKeys._id).as[String]
        (id, json)  
      } else {
        (null, json) 
      }
    })
  }
  
  def update(id: String, jsonData: JsObject): Future[(Boolean, JsValue)]  = { 
    val uri = s"${getUri()}${id}"
    
    var request: WSRequest = ws.url(uri)
    request = request.withRequestFilter(ElasticSearchRequestFilter(config))
    
    logger.debug(s"call edit for uri ${uri} with data ${jsonData}; with request ${request}")
    request.post(jsonData).map(response => {
      val json = response.json
      if(response.status == Status.OK) {
        (true, json)  
      } else {
        (false, json) 
      }
    })
  }
  
  def remove(id: String): Future[(Boolean, JsValue)] = { 
    val uri = s"${getUri()}${id}"
    var request: WSRequest = ws.url(uri)
    request = request.withRequestFilter(ElasticSearchRequestFilter(config))
    
    logger.debug(s"call delete for uri ${uri} with request ${request}")
    request.delete().map(response => {
     var updated = true
     if(response.status != Status.OK ) {
       updated = false
     }
     (updated, response.json)
    })
  }
  
  private def getUri() = {
    s"${repoUrl}${indexRoute}/"
  }
  
  private def parseSearchResponse(jsonResponse: JsValue): (Int, JsValue, Boolean) = {
    val hits = (jsonResponse \ searchResponseKeys.hits.KEY).get
    var total = (hits \ searchResponseKeys.hits.total.KEY \ searchResponseKeys.hits.total.value).as[Int]
    var results = (hits \ searchResponseKeys.hits.hits).get
    (total, results, false)
  }
  
  private def handleIdResponse(response: WSResponse): (Boolean, JsValue) = {
    val jsonResponse = response.json
    val found = (jsonResponse \ responseDocKeys.found).as[Boolean]
    val result = jsonResponse
    (found, result)
  }
}
