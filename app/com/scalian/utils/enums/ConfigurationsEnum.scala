package com.scalian.utils.enums

object ConfigurationsEnum extends Enumeration {
    final object elasticsearch {
      final val KEY = "elasticsearch"
      final val url = "url"
      final val apikey = "apikey"
      
      final object certificat {
        final val KEY = "certificat"
        final val virtualhost = "virtualhost"
      }
      
      final object routes {
        final val KEY = "routes"
        final val catalog = "catalog"
        final val toolboxsheets = "toolboxsheets"
      }
  }
}
