package com.example.android2111.app

import akka.actor.Actor.Receive
import akka.actor.{PoisonPill, Actor, Props}
import akka.pattern._
import concurrent.ExecutionContext.Implicits._
import android.os.{Message, Handler, Bundle}
import scala.concurrent.duration._
class ActorActivity extends ActivityExtras {
  implicit lazy val timeout = akka.util.Timeout(3 seconds)
  lazy val actor = App.as.actorOf(Props[MyActor])
  lazy val h = new Handler {
    override def handleMessage(m: Message) = {
      actor ? "ping" foreach handleReply
    }
  }

  override def onDestroy = {
    super.onDestroy
    actor ! PoisonPill
  }
  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    h.sendEmptyMessage(0)
  }

  def handleReply(a: Any): Unit = a match {
    case "pong" => ui {
      toast("pong")
      h.sendEmptyMessageDelayed(0, 2500l)
    }
  }

}

class MyActor extends Actor {
  def receive = {
    case "ping" => sender ! "pong"
  }
}