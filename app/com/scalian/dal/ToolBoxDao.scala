package com.scalian.dal

import javax.inject._
import play.api.libs.json.{JsObject, Json, JsValue}

import play.api.Configuration

import play.api.libs.ws._

import scala.concurrent.{ ExecutionContext, Future, Promise }
import com.scalian.utils.enums.ConfigurationsEnum
import com.scalian.utils.deadbolt.User

@Singleton
class ToolBoxDao @Inject() (
    config: Configuration, 
    ws: WSClient) (implicit ec: ExecutionContext) 
  extends AbstractElasticsearchRepo(config, ws) {
  
  private final val elasticSearchKey = ConfigurationsEnum.elasticsearch.KEY
  private final val routeKey = s"${elasticSearchKey}.${ConfigurationsEnum.elasticsearch.routes.KEY}"
  
  this.indexRoute = config.get[String](s"${routeKey}.${ConfigurationsEnum.elasticsearch.routes.catalog}") + config.get[String](s"${routeKey}.${ConfigurationsEnum.elasticsearch.routes.toolboxsheets}")
  
  override def find(user: User, wordSequence: String, offset: Int, limit: Int, sort: String) = {
    super.find(user, wordSequence, offset, limit, sort)
  }
  
  override def findById(user: User, id : String) = {
    super.findById(user, id)
  }
  
  override def insert(user: User, jsonData: JsObject) = {
    super.insert(user, jsonData)
  }
  
  override def update(user: User, id : String, jsonData: JsObject) = {
    super.update(user, id, jsonData)
  }
  
  override def remove(user: User, id : String) = {
    super.remove(user, id)
  }
  
  
}
