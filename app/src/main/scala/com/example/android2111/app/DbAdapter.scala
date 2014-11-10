package com.example.android2111.app

import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.{TableUtils, DatabaseTable}

import scala.reflect.ClassTag

object DbAdapter extends OrmLiteSqliteOpenHelper(App.instance, "db", null, 1) {
  type Db = SQLiteDatabase
  private def tables = List(classOf[User])

  override def onCreate(database: Db, connectionSource: ConnectionSource): Unit = {
    tables.foreach{
      TableUtils.createTable(connectionSource, _)
    }
  }

  def getDao[T : ClassTag]: Dao[T, Int] = getDao(implicitly[ClassTag[T]].runtimeClass)

  override def onUpgrade(database: Db, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int): Unit = ???
}

@DatabaseTable class User(pname: String, page: Int) extends WithId { // https://issues.scala-lang.org/browse/SI-8975
  @DatabaseField val name = pname
  @DatabaseField val age = page
  private def this() = this(null, -1)
}

trait WithId {
  @DatabaseField(generatedId = true) private var id: Int = _
}