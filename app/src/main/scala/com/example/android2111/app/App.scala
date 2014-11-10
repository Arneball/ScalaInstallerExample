package com.example.android2111.app

/**
 * Created by arneball on 2014-11-10.
 */
class App extends android.app.Application {
  override def onCreate = {
    super.onCreate
    App.instance = this
  }
}

object App {
  var instance: App = _
}
