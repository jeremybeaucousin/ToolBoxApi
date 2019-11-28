package com.scalian.controllers

import play.api.mvc.Accepting

final object ControllerConstants {
  final val AcceptsPdf = Accepting("application/pdf")

  final object HeaderFields {
    final val xApiKey = "X-Api-Key"
    final val xAuthToken = "X-Auth-Token"
    final val location: String = "Location"
    final val xTotalCount: String = "X-Total-Count"
    final val link: String = "link"
  }

  final object HeadersKey {
    final val Authorization = "Authorization"
    final val ApiKey = "ApiKey"
    final val Basic = "Basic"
  }

  final val noJsonMessage = "{\"message\": \"no body\"}"
}