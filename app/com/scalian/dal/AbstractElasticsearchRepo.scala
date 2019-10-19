package com.scalian.dal

import javax.inject._

import play.api.Logger
import play.api.Configuration
import play.api.libs.json.{JsObject, Json, JsString}

import scala.concurrent.Future

//import scala.concurrent.{ ExecutionContext, Future, Promise }

abstract class AbstractElasticsearchRepo @Inject() (config: Configuration) {
 
  val logger: Logger = Logger(this.getClass())
  
  var repoUrl = config.get[String]("elasticsearch.url")
  var catalogRoute = config.get[String]("elasticsearch.route.catalog")
  var indexRoute: String = ""
 
  def find(jsonQuery: JsObject) = { 
    logger.debug(s"call find for uri ${getUri()} with query ${jsonQuery}")
    jsonQuery
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
    repoUrl + catalogRoute + indexRoute
  }
}
