package com.example.android2111

import android.database.Cursor
import android.view.{ViewGroup, View}
import android.view.View.OnClickListener
import android.widget.{ImageView, TextView}

package object app {
  object Implicits {
    implicit class ViewWrapper(val v: View) extends AnyVal {
      def setCl(f: => Unit) = v.setOnClickListener(new OnClickListener {
        override def onClick(p1: View): Unit = f
      })
      def fid(id: Int) = v.findViewById(id)
      def gtTxt(id: Int) = fid(id).asInstanceOf[TextView]
      def gtImg(id: Int) = fid(id).asInstanceOf[ImageView]
      def tag[T] = v.getTag.asInstanceOf[T]
      def getViews: Traversable[View] = v match {
        case vg: ViewGroup => vg.flatMap{ _.getViews }
        case v: View => List(v)
      }
    }

    implicit class TextViewW(val e: TextView) extends AnyVal {
      def text = e.getText.toString
    }

    implicit class ViewGroupWrapper(val v: ViewGroup) extends Traversable[View] {
      def foreach[U](f: View => U) = {
        0 until v.getChildCount foreach (v.getChildAt _ andThen f)
      }
    }

    implicit def str2column(name: String)(implicit cursor: Cursor): Int = cursor.getColumnIndex(name)

    implicit class AnyW[T](val t: T) extends AnyVal {
      def |>[U](f: T => U) = f(t)
    }
  }

}