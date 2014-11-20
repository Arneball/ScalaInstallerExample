package com.example.android2111.app

import akka.actor.ActorSystem
import android.content.Context
import android.support.multidex.{MultiDex, MultiDexApplication}

/**
 * Created by arneball on 2014-11-10.
 */
class App extends MultiDexApplication {
  override def onCreate = {
    super.onCreate
    App.instance = this
    App.as = ActorSystem("kalle")
  }

  override def attachBaseContext(ctx: Context) = {
    super.attachBaseContext(ctx)
    MultiDex.install(this)
  }
}

object App {
  var as: ActorSystem = _

  var instance: App = _
}
