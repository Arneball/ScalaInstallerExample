package com.example.android2111.app.util

import akka.persistence.snapshot.SnapshotStore
import akka.persistence.{SelectedSnapshot, SnapshotMetadata, SnapshotSelectionCriteria}
import com.example.android2111.app.model.Snapshot
import com.example.android2111.app.{DbAdapter, InternalExtras}
import com.j256.ormlite.dao.Dao
import collection.JavaConversions._
import scala.concurrent.Future

/**
 * Created by arneball on 2014-12-12.
 */
class MySnapshotStore extends SnapshotStore with InternalExtras {
  override def loadAsync(persistenceId: String, criteria: SnapshotSelectionCriteria): Future[Option[SelectedSnapshot]] = Future {
    val SnapshotSelectionCriteria(maxseq, maxts) = criteria
    val preparedQ = getDao.queryBuilder()
    preparedQ.where.le("seqNr", maxseq)
               .and.le("timestamp", maxts)
               .and.eq("persistenceId", persistenceId)
    preparedQ.orderBy("timestamp", false)
    getDao.query(preparedQ.prepare()).headOption.map{ r =>
      SelectedSnapshot(SnapshotMetadata(persistenceId, r.seqNr, r.timestamp), r.toObject)
    }
  }

  override def saveAsync(metadata: SnapshotMetadata, snapshot: Any): Future[Unit] = Future {
    getDao.createOrUpdate(new Snapshot(metadata.persistenceId, metadata.sequenceNr, metadata.sequenceNr, snapshot))
  }

  private def getDao: Dao[Snapshot, Int] = DbAdapter.getDao[Snapshot]

  override def saved(metadata: SnapshotMetadata): Unit = ()

  override def delete(metadata: SnapshotMetadata): Unit = {
    val builder = getDao.deleteBuilder()
    builder.where().eq("persistenceId", metadata.persistenceId)
               .and.eq("timestamp", metadata.timestamp)
               .and.eq("seqNr", metadata.sequenceNr)
    getDao.delete(builder.prepare)
  }

  override def delete(persistenceId: String, criteria: SnapshotSelectionCriteria): Unit = {
    val builder = getDao.deleteBuilder()
    builder.where.eq("persistenceId", persistenceId)
             .and.le("timestamp", criteria.maxTimestamp)
             .and.le("seqNr", criteria.maxSequenceNr)
    getDao.delete(builder.prepare())

  }
}
