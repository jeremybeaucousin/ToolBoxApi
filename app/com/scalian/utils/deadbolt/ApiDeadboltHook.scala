package com.scalian.utils.deadbolt

import be.objectify.deadbolt.scala.cache.HandlerCache
import play.api.inject.{ Binding, Module }
import play.api.{ Configuration, Environment }

class ApiDeadboltHook extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(
      bind[HandlerCache].to[ApiHandlerCache])
}