package com.example.android2111.app

import akka.actor.{FSM, PoisonPill, Props}
import akka.util.Timeout
import concurrent.duration._
import scala.reflect.ClassTag

trait FsmActivity extends ActivityExtras with ActorExtras {
  type State
  type Data
  def actorClass: ClassTag[_ <: FSM[State, Data]]
  lazy val actor = App.system.actorOf(Props(actorClass.runtimeClass))
  override def onDestroy() = {
    super.onDestroy()
    actor ! PoisonPill
  }
}

trait ActorExtras {
  implicit def timeout: Timeout = 2 seconds
}