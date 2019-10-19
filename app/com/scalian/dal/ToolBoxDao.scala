package com.scalian.dal

import javax.inject._
import play.api.libs.json.{JsObject, Json}

import play.api.Configuration

import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future, Promise }

@Singleton
class ToolBoxDao @Inject() (
    config: Configuration, 
    ws: WSClient) (implicit ec: ExecutionContext) 
  extends AbstractElasticsearchRepo(config, ws) {
  
  this.indexRoute = config.get[String]("elasticsearch.route.catalog") + config.get[String]("elasticsearch.route.toolboxsheets")
  
  override def find(jsonQuery: JsObject) = {
    super.find(jsonQuery)
  }
  
  override def findById(id : String) = {
    super.findById(id)
  }
  
  override def insert(jsonData: JsObject) = {
    super.insert(jsonData)
  }
  
  override def update(id : String, jsonData: JsObject) = {
    super.update(id, jsonData)
  }
  
  override def remove(id : String) = {
    super.remove(id)
  }
  
  
}
