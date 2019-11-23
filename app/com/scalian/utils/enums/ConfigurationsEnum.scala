package com.scalian.utils.enums

object ConfigurationsEnum extends Enumeration {
  final object play {
    final val KEY = "play"

    final object http {
      final val KEY = "http"

      final object secret {
        final val KEY = "secret"
        final val key = "key"
      }
      
      final object session {
        final val KEY = "session"
        final val maxAge = "maxAge"
      }
    }

  }

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
    
    final object defaultUser {
        final val KEY = "defaultUser"
        final val login = "login"
        final val password = "password"
      }
  }
}
