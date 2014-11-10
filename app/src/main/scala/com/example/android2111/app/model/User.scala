package com.example.android2111.app.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable class User(pname: String, page: Int) extends WithId { // https://issues.scala-lang.org/browse/SI-8975
  @DatabaseField val name = pname
  @DatabaseField val age = page
  private def this() = this(null, -1)
}

trait WithId {
  @DatabaseField(generatedId = true) private var id: Int = _
}