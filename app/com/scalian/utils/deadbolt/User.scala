package com.scalian.utils.deadbolt

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.models.Role
import be.objectify.deadbolt.scala.models.Permission

class User extends Subject {

  override def identifier: String = {
    ""
  }

  override def roles: List[Role] = {
    null
  }

  override def permissions: List[Permission] = {
    null
  }
}