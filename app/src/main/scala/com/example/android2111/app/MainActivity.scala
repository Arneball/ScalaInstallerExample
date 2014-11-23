package com.example.android2111.app

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.{EditText, ImageView, TextView, Toast}

import Implicits._

import scala.reflect.ClassTag

trait ActivityExtras extends Activity {

  def startActivity[T <: Activity : ClassTag]: Unit = startActivity(new Intent(this, implicitly[ClassTag[T]].runtimeClass))

  implicit def activity2view(a: Activity): ViewWrapper = a.getWindow.getDecorView
  def ui(fun: =>Unit) = runOnUiThread(new Runnable {
    def run = fun
  })
  private def toastImpl(str: String) = Toast.makeText(this, str, Toast.LENGTH_LONG).show()
  def toast(resid: Int) = toastImpl(getString(resid))
  def toast(string: String) = toastImpl(string)

}

class MainActivity extends Activity with ActivityExtras {
  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_main)
    this.fid(R.id.button_list_activity).setCl{
      startActivity[ListActivity]
    }
    this.fid(R.id.button_actor_activity).setCl{
      startActivity[ActorActivity]
    }
  }
}

object Implicits {
  implicit class ViewWrapper(val v: View) extends AnyVal {
    def setCl(f: => Unit) = v.setOnClickListener(new OnClickListener {
      override def onClick(p1: View): Unit = f
    })
    def fid(id: Int) = v.findViewById(id)
    def gtTxt(id: Int) = fid(id).asInstanceOf[TextView]
    def gtImg(id: Int) = fid(id).asInstanceOf[ImageView]
    def tag[T] = v.getTag.asInstanceOf[T]
  }

  implicit class EditTextW(val e: TextView) extends AnyVal {
    def getRealText = e.getText.toString
  }

  implicit def str2column(name: String)(implicit cursor: Cursor): Int = cursor.getColumnIndex(name)
}