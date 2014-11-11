package com.example.android2111.app.util

import android.content.Context
import android.database.Cursor
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.CursorAdapter

/**
 * Created by arneball on 2014-11-11.
 */
abstract class MyCursorAdapter[T : DbReader](ctx: Context, c: Cursor, childResid: Int) extends CursorAdapter(ctx, c, true) {
  override def newView(p1: Context, p2: Cursor, p3: ViewGroup): View = {
    LayoutInflater.from(p1).inflate(childResid, p3, false)
  }

  override def bindView(p1: View, p2: Context, p3: Cursor): Unit = {
    val t: T = implicitly[DbReader[T]].read(p3)
    doBind(p1, t)
  }
  def doBind(v: View, t: T): View
}
