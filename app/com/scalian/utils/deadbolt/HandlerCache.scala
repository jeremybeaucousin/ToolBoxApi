package com.scalian.utils.deadbolt

import javax.inject.Singleton

import be.objectify.deadbolt.scala.HandlerKey
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.DeadboltHandler


@Singleton
class ApiHandlerCache extends HandlerCache {
   val defaultHandler: DeadboltHandler = new ApiDeadboltHandler

    // HandlerKeys is an user-defined object, containing instances 
    // of a case class that extends HandlerKey  
    val handlers: Map[Any, DeadboltHandler] = Map("defaultHandler" -> defaultHandler)

    // Get the default handler.
    override def apply(): DeadboltHandler = defaultHandler

    // Get a named handler
    override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}