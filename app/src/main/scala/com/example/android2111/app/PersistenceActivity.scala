package com.example.android2111.app

import akka.pattern._
import akka.persistence._
import android.os.Bundle
import com.example.android2111.app.Implicits._

import scala.concurrent.ExecutionContext.Implicits._
import scala.reflect.ClassTag

class PersistenceActivity extends ActivityExtras with ActorExtras {
  private val callback: PartialFunction[Any, Unit] = {
    case s: MyActorState => ui {
      this.gtTxt(R.id.text).setText(s.toString)
    }
  }
  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_persistence)
    this.fid(R.id.add).setCl {
      actor ? CmdAddUser(user) collect callback
    }
    this.fid(R.id.rem).setCl {
      actor ? CmdRemUser(user) collect callback
    }
    this.fid(R.id.snap).setCl {
      actor ! "snapshot"
    }
    actor ? "state" collect callback
  }

  private def user = this.gtTxt(R.id.username).text

  def actorClass = implicitly[ClassTag[MyActor]]
}

import scala.collection.JavaConversions._
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
    case "state" =>
      sender ! state
    case "snapshot" =>
      saveSnapshot(state)
      sender ! state
  }

  private def processEvent(evt: Evt) = {
    evt match {
      case EvtUserAdded(u) => state += u
      case EvtUserRemoved(u) => state -= u
    }
    sender ! state
  }

  override def persistenceId: String = "myPersistentActor"
}





