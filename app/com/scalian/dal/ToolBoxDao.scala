package com.scalian.dal

import javax.inject._
import play.api.libs.json.{JsObject, Json}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ToolBoxDao @Inject()
  (val abstractRepo: AbstractRepo) {
  
  abstractRepo.collectionName =  "toolBoxSheets"

  def find() = {
    abstractRepo.find(Json.obj())
  }
  
  def findById(id : String) = {
    abstractRepo.findById(id)
  }
  
  def insert(jsonData: JsObject) = {
    abstractRepo.insert(jsonData)
  }
  
  def update(id : String, jsonData: JsObject) = {
    abstractRepo.update(id, jsonData)
  }
  
  def remove(id : String) = {
    abstractRepo.remove(id)
  }
  
  
}