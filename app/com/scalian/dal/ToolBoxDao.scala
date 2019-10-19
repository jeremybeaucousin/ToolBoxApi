package com.scalian.dal

import javax.inject._
import play.api.libs.json.{JsObject, Json}

import play.api.Configuration

@Singleton
class ToolBoxDao @Inject() (config: Configuration) extends AbstractElasticsearchRepo(config) {
  
  this.indexRoute =  "toolBoxSheets"
  
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
