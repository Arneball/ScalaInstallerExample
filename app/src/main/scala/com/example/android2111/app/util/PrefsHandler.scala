package com.example.android2111.app.util

import android.content.Context
import com.example.android2111.app._
import Implicits._
import com.google.gson.Gson

import scala.reflect.ClassTag

object PrefsHandler {
  private val prefs = App.instance.getSharedPreferences("prefs", Context.MODE_PRIVATE)
  private val gson = new Gson

  @inline private def clazz[T](implicit ct: ClassTag[T]) = ct.runtimeClass.asInstanceOf[Class[T]]

  def apply[T : ClassTag](): T = {
    prefs.getString(clazz.getSimpleName, null) |> (gson.fromJson(_, clazz))
  }

  def apply[T : ClassTag](t: T): Unit = {
    prefs.edit().putString(clazz.getSimpleName, gson.toJson(t)).apply()
  }
}