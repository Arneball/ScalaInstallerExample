package com.example.android2111

import android.database.Cursor
import android.view.View
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
    }

    implicit class TextViewW(val e: TextView) extends AnyVal {
      def text = e.getText.toString
    }

    implicit def str2column(name: String)(implicit cursor: Cursor): Int = cursor.getColumnIndex(name)

    implicit class AnyW[T](val t: T) extends AnyVal {
      def |>[U](f: T => U) = f(t)
    }

    implicit class Resid(val value: Int) extends AnyVal
    implicit class Message(val value: String) extends AnyVal

    implicit def str2Message(value: String): Right[Resid, Message] = Right(value)
    implicit def int2resid(value: Int): Left[Resid, Message] = Left(value)
  }

}