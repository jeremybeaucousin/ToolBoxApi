package com.scalian.utils.enums

object ConfigurationsEnum extends Enumeration {
  final object play {
    final val KEY = "play"

    final object http {
      final val KEY = "http"

      final object session {
        final val KEY = "session"

        final object maxAge {
          final val KEY = "maxAge"
        }
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
  }
}
