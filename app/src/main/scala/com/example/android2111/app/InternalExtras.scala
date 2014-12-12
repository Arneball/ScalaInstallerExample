package com.example.android2111.app

import android.content.Context
import com.google.gson.Gson

import scala.concurrent.ExecutionContext

/**
 * Created by arneball on 2014-12-12.
 */
trait InternalExtras {
  implicit protected def ece: ExecutionContext = ExecutionContext.Implicits.global
  protected val prefs = App.instance.getSharedPreferences("akka_storage", Context.MODE_PRIVATE)
  protected val gson = new Gson
}
