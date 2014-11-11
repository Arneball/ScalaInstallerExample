package com.example.android2111.app

import android.database.sqlite.{ SQLiteDatabase => Db }
import com.example.android2111.app.model.{WithId, User}
import com.j256.ormlite.android.AndroidDatabaseResults
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.{TableUtils, DatabaseTable}

import scala.reflect.ClassTag

object DbAdapter extends OrmLiteSqliteOpenHelper(App.instance, "db", null, 1) {
  private def tables = List(classOf[User])

  override def onCreate(database: Db, connectionSource: ConnectionSource): Unit = {
    tables.foreach{
      TableUtils.createTable(connectionSource, _)
    }
  }

  def getDao[T <: WithId : ClassTag]: Dao[T, Int] = getDao(implicitly[ClassTag[T]].runtimeClass)

  implicit def dao2cursor[T <: WithId : ClassTag] = {
    getDao[User].iterator.getRawResults.asInstanceOf[AndroidDatabaseResults].getRawCursor
  }

  override def onUpgrade(database: Db, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int): Unit = ???
}