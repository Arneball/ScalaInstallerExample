package com.example.android2111.app

import java.io.InputStream

import akka.actor.ActorSystem
import com.android.volley.toolbox.Volley
import com.typesafe.config.ConfigFactory

/**
 * Created by arneball on 2014-11-10.
 */
class App extends android.app.Application {
  override def onCreate() = {
    super.onCreate()
    App.instance = this
  }
}

object App {
  var instance: App = _
  lazy val system = ActorSystem("da_system", cfg)
  def cfg = {
    val resource: InputStream = instance.getResources.openRawResource(R.raw.reference)
    try {
      val str = io.Source.fromInputStream(resource).getLines().mkString("\n")
      ConfigFactory.parseString(str)
    } finally {
      resource.close()
    }
  }
  lazy val requestQueue = Volley.newRequestQueue(instance)
}
