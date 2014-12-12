package com.example.android2111.app

import akka.actor.{ActorRef, Props}
import akka.persistence._
import akka.persistence.journal.AsyncWriteJournal
import akka.persistence.snapshot.SnapshotStore
import android.content.{SharedPreferences, Context}
import android.util.Log
import concurrent.ExecutionContext.Implicits._
import android.os.Bundle
import com.google.gson.Gson
import akka.pattern._
import scala.collection.immutable
import scala.collection.immutable.Seq
import scala.concurrent.Future
import Implicits._
class PersistenceActivity extends ActivityExtras with ActorExtras {
  lazy val actor = App.system.actorOf(Props[MyActor])

  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_persistence)
    this.fid(R.id.add).setCl {
      actor ! CmdAddUser(user)
    }
    this.fid(R.id.rem).setCl {
      actor ! CmdRemUser(user)
    }
    actor ? "state" foreach {
      case s: MyActorState => ui {
        toast(s"current state: $s")
      }
    }
  }

  private def user = this.gtTxt(R.id.username).text
}

import collection.JavaConversions._
case class MyActorState(users: java.util.Set[String] = new java.util.HashSet) {
  def +(str: String) = copy(users + str)
  def -(str: String) = copy(users - str)
}

sealed trait Cmd
case class CmdAddUser(user: String) extends Cmd
case class CmdRemUser(user: String) extends Cmd

sealed trait Evt
case class EvtUserAdded(user: String) extends Evt
case class EvtUserRemoved(user: String) extends Evt

class MyActor extends PersistentActor {
  var state = MyActorState()
  override def receiveRecover: Receive = {
    case SnapshotOffer(_, s: MyActorState) =>
      state = s
    case e: Evt =>
      processEvent(e)
  }

  override def receiveCommand: Receive = {
    case CmdAddUser(u) =>
      persist(EvtUserAdded(u)){ processEvent }
    case CmdRemUser(u) =>
      persist(EvtUserRemoved(u)) { processEvent }
    case "state" => sender ! state
  }

  private def processEvent(evt: Evt) = evt match {
    case EvtUserAdded(u) => state += u
    case EvtUserRemoved(u) => state += u
  }

  override def persistenceId: String = "myPersistentActor"
}
trait InternalExtras {
  protected val prefs = App.instance.getSharedPreferences("akka_storage", Context.MODE_PRIVATE)
  protected val gson = new Gson
}

class MySnapshotStore extends SnapshotStore with InternalExtras {
  def snapshotClass(i: Long) = Option(prefs.getString(s"snapshotclass$i", null)).map{ Class.forName }
  def saveSnapshotClass(i: Long, any: Any) = prefs.edit().putString(s"snapshotclass$i", any.getClass.toString).apply()

  def snapShot(i: Long) = Option(prefs.getString(s"snapshot$i", null))
  def deletSnap(i: Long) = prefs.edit().remove(s"snapshot$i").apply()
  def saveSnapShot(i: Long, any: Any) = prefs.edit.putString(s"snapshot$i", gson.toJson(any)).apply()

  def snapShotMeta(i: Long): Option[SnapshotMetadata] = Option(prefs.getString(s"snapshotmeta$i", null)).map{
    gson.fromJson(_, classOf[SnapshotMetadata])
  }
  def saveSnapShotMeta(i: Long, meta: SnapshotMetadata) = prefs.edit.putString(s"snapshotmeta$i", gson.toJson(meta)).apply()

  def maxSnapShot = prefs.getInt("snapshotId", -1)
  override def loadAsync(persistenceId: String, criteria: SnapshotSelectionCriteria): Future[Option[SelectedSnapshot]] = {
    maxSnapShot match {
      case 0 => Future{ None }
      case n =>
        val snapshot = for {
          claz <- snapshotClass(n)
          meta <- snapShotMeta(n)
          snapstr <- snapShot(n)
          snap = gson.fromJson(snapstr, claz)
        } yield SelectedSnapshot(meta, snap)
        Future { snapshot }
    }
  }

  override def saveAsync(metadata: SnapshotMetadata, snapshot: Any): Future[Unit] = {
    saveSnapShotMeta(metadata.sequenceNr, metadata)
    saveSnapShot(metadata.sequenceNr, snapshot)
    Future { Unit }
  }

  override def saved(metadata: SnapshotMetadata): Unit = ()

  override def delete(metadata: SnapshotMetadata): Unit = {
    deletSnap(metadata.sequenceNr)
  }

  override def delete(persistenceId: String, criteria: SnapshotSelectionCriteria): Unit = ()
}

class MyJournal extends AsyncWriteJournal with InternalExtras {

  private def saveClass(id: String, seq: Long, clazz: String)(ed: SharedPreferences.Editor) = {
    ed.putString(getClassKey(id, seq), clazz)
  }

  def seqNr(id: String) = s"seq:$id"
  private def setSeqNr(id: String, seq: Long, edit: SharedPreferences.Editor) = {
    val shit = prefs.getLong(seqNr(id), 0)
    if(seq > shit) {
      edit.putLong(seqNr(id), seq)
    }
  }

  private def getSeqnr(id: String) = prefs.getLong(seqNr(id), 0l)

  private def getClassKey(id: String, seq: Long) = s"mess:clazz:$id:$seq"
  private def getKey(id: String, seq: Long) = s"mess:$id:$seq"
  override def asyncWriteMessages(messages: Seq[PersistentRepr]): Future[Unit] = {
    val edit = prefs.edit()
    messages.foreach{ m =>
      val payload = m.payload
      val deleted = m.deleted
      val prefsKey = getKey(m.persistenceId, m.sequenceNr)
      if(deleted) {
        edit.remove(prefsKey)
      } else {
        edit.putString(prefsKey, gson.toJson(payload))
        val plClz = payload.getClass.getName
        saveClass(m.persistenceId, m.sequenceNr, plClz)(edit)
      }
    }
    val ids = messages.groupBy(_.persistenceId).mapValues(_.maxBy{ _.persistenceId })
    ids.foreach{
      case (persistanceId, maxSeq) => setSeqNr(persistanceId, maxSeq.sequenceNr, edit)
    }
    edit.apply()
    Future{ () }
  }

  override def asyncDeleteMessagesTo(persistenceId: String, toSequenceNr: Long, permanent: Boolean): Future[Unit] = {
    val edit = prefs.edit()
    (0l until toSequenceNr).foreach{ i =>
      edit.remove(getKey(persistenceId, i))
    }
    edit.apply()
    Future { () }
  }

  override def asyncReplayMessages(persistenceId: String, fromSequenceNr: Long, toSequenceNr: Long, max: Long)(replayCallback: (PersistentRepr) => Unit): Future[Unit] = {
    fromSequenceNr to Math.min(getSeqnr(persistenceId), toSequenceNr) foreach{ id =>
      for {
        className <- Option(prefs.getString(getClassKey(persistenceId, id), null))
        clazz = Class.forName(className)
        key = getKey(persistenceId, id)
        str <- Option(prefs.getString(key, null))
        value = gson.fromJson(str, clazz)
        repr = PersistentRepr.apply(value, id, persistenceId)
      } {
        Log.e("Arne", s"Replaying $persistenceId with seqNr: $id, data: $value")
        replayCallback(repr)
      }
    }
    Future{ () }
  }

  override def asyncReadHighestSequenceNr(persistenceId: String, fromSequenceNr: Long): Future[Long] = Future {
    getSeqnr(persistenceId)
  }


  override def asyncWriteConfirmations(confirmations: Seq[PersistentConfirmation]): Future[Unit] = ???

  override def asyncDeleteMessages(messageIds: Seq[PersistentId], permanent: Boolean): Future[Unit] = ???
}
