package controllers

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
  
  
}
