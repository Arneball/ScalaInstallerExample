package com.example.android2111.app

import android.app.Activity
import android.os.{AsyncTask, Bundle}
import android.view.View
import android.view.View.OnClickListener
import android.widget.{Toast, TextView}
import com.example.android2111.app.Implicits.ViewWrapper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import scala.concurrent.{Future, ExecutionContext}

import Implicits._
trait ActivityExtras extends Activity {
  implicit def exe: ExecutionContext = ExecutionContext.Implicits.global
  implicit def act2viewW(a: Activity): ViewWrapper = getWindow.getDecorView
  def postUi(f: =>Unit) = runOnUiThread(new Runnable {
    override def run = f
  })
  def toast(str: String) = Toast.makeText(this, str, Toast.LENGTH_LONG).show()
}
object Implicits {
  implicit class ViewWrapper(val v: View) extends AnyVal {
    def gtTxt(id: Int) = v.findViewById(id).asInstanceOf[TextView]
    def fid(id: Int) = v.findViewById(id)
    def setCl(f: => Unit) = setClick((v) => f)
    def setClick(f: View => Unit) = v.setOnClickListener(new OnClickListener {
      override def onClick(v: View) = f(v)
    })
  }
  implicit class StringW(val str: String) extends AnyVal {
    def httpGet = new DefaultHttpClient().execute(new HttpGet(str))
    def httpHeaders = httpGet.getAllHeaders.map{ header =>
      s"${header.getName} -> ${header.getValue}"
    }.mkString("\n")
  }
}

class MainActivity extends Activity with ActivityExtras {
  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_main)
    this.fid(R.id.button).setCl{
      MyAsync("http://www.google.com".httpHeaders)(toast)
    }
    for {
      tim1 <- futTimeStamp
    } postUi(this.gtTxt(R.id.text).setText(tim1.toString))
  }

  def futTimeStamp = Future{
    System.currentTimeMillis()
  }
}

object MyAsync {
  def apply[T](producer: =>T)(callback: T => Unit) = new AsyncTask[Nothing, Nothing, T] {
    override def doInBackground(params: Nothing*) = producer
    override def onPostExecute(t: T) = callback(t)
  }.execute()
}