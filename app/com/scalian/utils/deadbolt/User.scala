package com.scalian.utils.deadbolt

import play.api.libs.json.Json

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.models.Role
import be.objectify.deadbolt.scala.models.Permission


class User(
    val login: String, 
    val password: String
    ) extends Subject {

  override def identifier: String = {
    this.login
  }

  override def roles: List[Role] = {
    null
  }

  override def permissions: List[Permission] = {
    null
  }
}