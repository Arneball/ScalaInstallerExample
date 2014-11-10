package com.example.android2111.app.model

import com.j256.ormlite.field.{ DatabaseField => Field }
import com.j256.ormlite.table.{ DatabaseTable => Table }

@Table class User(pname: String, page: Int) extends WithId { // https://issues.scala-lang.org/browse/SI-8975
  @Field val name = pname
  @Field val age = page
  private def this() = this(null, -1)
}

trait WithId {
  @Field(generatedId = true) private var id: Int = _
}