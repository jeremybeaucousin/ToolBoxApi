package com.scalian.dal

import javax.inject._
import play.api.libs.json.{JsObject, Json, JsValue}

import play.api.Configuration

import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future, Promise }
import com.scalian.utils.enums.ConfigurationsEnum

@Singleton
class ToolBoxDao @Inject() (
    config: Configuration, 
    ws: WSClient) (implicit ec: ExecutionContext) 
  extends AbstractElasticsearchRepo(config, ws) {
  
  private final val elasticSearchKey = ConfigurationsEnum.elasticsearch.KEY
  private final val routeKey = s"${elasticSearchKey}.${ConfigurationsEnum.elasticsearch.routes.KEY}"
  
  this.indexRoute = config.get[String](s"${routeKey}.${ConfigurationsEnum.elasticsearch.routes.catalog}") + config.get[String](s"${routeKey}.${ConfigurationsEnum.elasticsearch.routes.toolboxsheets}")
  
  override def find(wordSequence: String, offset: Int, limit: Int, sort: String) = {
    super.find(wordSequence, offset, limit, sort)
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
