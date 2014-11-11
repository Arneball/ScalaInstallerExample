package com.example.android2111.app.model

import com.example.android2111.app.Implicits._
import android.database.Cursor
import com.example.android2111.app.util.DbReader
import com.j256.ormlite.field.{ DatabaseField => Field }
import com.j256.ormlite.table.{ DatabaseTable => Table }

@Table class User(pname: String, page: Int) extends WithId { // https://issues.scala-lang.org/browse/SI-8975
  @Field val name = pname
  @Field val age = page
  private def this() = this(null, -1)
}
object User {
  implicit val read: DbReader[User] = new DbReader[User] {

    def read(c: Cursor) = {
      implicit val cur = c
      val age = c.getInt("age")
      val name = c.getString("name")
      new User(page = age, pname = name)
    }
  }
}
trait WithId {
  @Field(generatedId = true, columnName = "_id") private var _id: Int = _
}