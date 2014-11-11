package com.example.android2111.app.util

import android.database.Cursor

/**
 * Created by arneball on 2014-11-11.
 */
trait DbReader[T] {
  def read(c: Cursor): T
}
