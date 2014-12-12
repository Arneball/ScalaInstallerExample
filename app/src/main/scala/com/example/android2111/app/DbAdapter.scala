package com.example.android2111.app

import android.database.sqlite.{ SQLiteDatabase => Db }
import com.example.android2111.app.model.{Snapshot, PersistedEvent, User, WithId}
import com.j256.ormlite.android.AndroidDatabaseResults
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

import scala.collection.concurrent.TrieMap
import scala.reflect.ClassTag

object DbAdapter extends OrmLiteSqliteOpenHelper(App.instance, "db", null, 1) {
  private def tables: List[Class[_ <: WithId]] = List(classOf[User], classOf[PersistedEvent], classOf[Snapshot])

  override def onCreate(database: Db, connectionSource: ConnectionSource): Unit = {
    tables.foreach{
      TableUtils.createTable(connectionSource, _)
    }
  }

  private val daoCache = collection.mutable.Map[ClassTag[_], Dao[_, _]]()

  def getDao[T <: WithId : ClassTag]: Dao[T, Int] = {
    val tag  = implicitly[ClassTag[T]]
    def mkDao = getDao(tag.runtimeClass)
    daoCache.getOrElseUpdate(tag, mkDao).asInstanceOf[Dao[T, Int]]
  }

  override def close(): Unit = {
    super.close()
    daoCache.clear()
  }

  implicit def dao2cursor[T <: WithId : ClassTag] = {
    getDao[User].iterator.getRawResults.asInstanceOf[AndroidDatabaseResults].getRawCursor
  }

  override def onUpgrade(database: Db, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) = ???
}