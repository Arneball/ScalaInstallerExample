package com.example.android2111.app.util

import akka.persistence.journal.AsyncWriteJournal
import akka.persistence.{PersistentConfirmation, PersistentId, PersistentRepr}
import com.example.android2111.app.model.PersistedEvent
import com.example.android2111.app.{DbAdapter, InternalExtras}
import com.j256.ormlite.stmt.QueryBuilder

import scala.collection.JavaConversions._
import scala.collection.immutable.Seq
import scala.concurrent.Future

class MyJournal extends AsyncWriteJournal with InternalExtras {

  override def asyncWriteMessages(messages: Seq[PersistentRepr]): Future[Unit] = Future {
    val dao = DbAdapter.getDao[PersistedEvent]
    messages.foreach { m =>
      dao.create(new PersistedEvent(m.persistenceId, m.sequenceNr, m.payload))
    }
  }

  override def asyncDeleteMessagesTo(persistenceId: String, toSequenceNr: Long, permanent: Boolean): Future[Unit] = Future {
    val dao = DbAdapter.getDao[PersistedEvent]
    val builder = dao.deleteBuilder()
    builder.where().le("seqNr", toSequenceNr)
    dao.delete(builder.prepare())
  }

  override def asyncReplayMessages(persistenceId: String, fromSequenceNr: Long, toSequenceNr: Long, max: Long)(replayCallback: (PersistentRepr) => Unit): Future[Unit] = Future {
    val dao = DbAdapter.getDao[PersistedEvent]
    val q = dao.queryBuilder().where().eq("persistenceId", persistenceId)
      .and.le("seqNr", Math.min(toSequenceNr, max))
      .and.ge("seqNr", fromSequenceNr).prepare()
    dao.query(q).iterator().foreach{ p =>
      replayCallback(PersistentRepr(p.toObject, p.seqNr, p.persistenceId))
    }
  }

  override def asyncReadHighestSequenceNr(persistenceId: String, fromSequenceNr: Long): Future[Long] = Future {
    val dao = DbAdapter.getDao[PersistedEvent]
    val builder: QueryBuilder[PersistedEvent, Int] = dao.queryBuilder()
    builder.where.eq("persistenceId", persistenceId)
             .and.ge("seqNr", fromSequenceNr)
    builder.orderBy("seqNr", false)
    builder.limit(1L)
    dao.query(builder.prepare()).headOption.map{ _.seqNr }.getOrElse(fromSequenceNr)
  }


  @deprecated("bla", "bla")
  override def asyncWriteConfirmations(confirmations: Seq[PersistentConfirmation]): Future[Unit] = ???

  @deprecated("bla", "bla")
  override def asyncDeleteMessages(messageIds: Seq[PersistentId], permanent: Boolean): Future[Unit] = ???
}