package com.example.android2111.app

import akka.actor.ActorSystem
import com.android.volley.toolbox.Volley

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
  val system = ActorSystem("da_system")
  lazy val requestQueue = Volley.newRequestQueue(instance)
}
