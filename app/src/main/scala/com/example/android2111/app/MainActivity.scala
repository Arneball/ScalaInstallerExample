package com.example.android2111.app

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class MainActivity extends Activity {
  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    val message = (1 to 100).mkString(",")
    Toast.makeText(this, message, Toast.LENGTH_LONG).show
  }
}