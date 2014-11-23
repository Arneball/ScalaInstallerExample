package com.example.android2111.app.model

/**
 * Created by arneball on 2014-11-23.
 */
case class CurrentTime(tz: String, hour: Int, second: Int, minute: Int) {
  override def toString = s"tz: $tz, $hour:$minute:$second"
}
