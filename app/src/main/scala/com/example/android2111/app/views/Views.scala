package com.example.android2111.app.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.{EditText, Button, TextView}
import com.example.android2111.Font
import com.example.android2111.app.R

trait WithFont extends TextView {
  def init(context: Context, attrs: AttributeSet) = {
    val a = context.obtainStyledAttributes(attrs, R.styleable.WithFont)
    a.getInt(R.styleable.WithFont_font, 2) match {
      case 0 => setTypeface(WithFont(context, Font.Awesome))
      case 1 => setTypeface(WithFont(context, Font.Candy))
      case 2 => setTypeface(WithFont(context, Font.Inconsolata))
    }
  }
}

class FontTextView(context: Context, attrs: AttributeSet) extends TextView(context, attrs) with WithFont {
  init(context, attrs)
}

class FontButton(context: Context, attrs: AttributeSet) extends Button(context, attrs) with WithFont {
  init(context, attrs)
}

class FontEditText(context: Context, attrs: AttributeSet) extends EditText(context, attrs) with WithFont {
  init(context, attrs)
}


object WithFont {
  private val typefaceMap = collection.mutable.Map[String, Typeface]()
  def apply(context: Context, fname: Font) = typefaceMap.getOrElseUpdate(fname.fontName, {
    Typeface.createFromAsset(context.getAssets, s"font/${fname.fontName}")
  })
}