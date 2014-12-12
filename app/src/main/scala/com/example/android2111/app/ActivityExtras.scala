package com.example.android2111.app

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.android2111.app.Implicits._

import scala.reflect.ClassTag

/**
 * Created by arneball on 2014-11-23.
 */
trait ActivityExtras extends Activity {
  implicit def activity2view(a: Activity): ViewWrapper = a.getWindow.getDecorView

  /** syntax friendly startActivity method
    * {{{
    *   startActivity[ListActivity]
    * }}}
    */
  def startActivity[T <: Activity : ClassTag](): Unit = startActivity(new Intent(this, implicitly[ClassTag[T]].runtimeClass))

  /** Run block on ui thread */
  def ui(fun: =>Unit) = runOnUiThread(new Runnable {
    def run() = fun
  })

  def toast(int: Int): Unit = toast(getString(int))
  def toast(str: String) = Toast.makeText(this, str, Toast.LENGTH_LONG).show()
}
