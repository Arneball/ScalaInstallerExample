package com.example.android2111.app

import akka.actor.{Actor, FSM, PoisonPill, Props}
import akka.util.Timeout
import android.app.Activity
import concurrent.duration._
import scala.reflect.ClassTag

trait FsmActivity extends ActorExtras {
  type State
  type Data
  def actorClass: ClassTag[_ <: FSM[State, Data]]
}

trait ActorExtras extends ActivityExtras {
  implicit val timeout: Timeout = 2 seconds
  def actorClass: ClassTag[_ <: Actor]
  lazy val actor = App.system.actorOf(Props(actorClass.runtimeClass))

  abstract override def onDestroy() = {
    super.onDestroy()
    actor ! PoisonPill
  }
}