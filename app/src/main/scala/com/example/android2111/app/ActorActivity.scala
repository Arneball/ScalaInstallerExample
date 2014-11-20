package com.example.android2111.app

import akka.actor.{Actor, Props}
import akka.util.Timeout
import android.app.Activity
import android.os.{Message, Handler, Bundle}
import Implicits._
import akka.pattern._
import concurrent.ExecutionContext.Implicits._
import concurrent.duration._
class ActorActivity extends Activity with ActivityExtras {

  implicit val timeout: Timeout = 2 seconds

  lazy val actor = App.as.actorOf(Props[PongActor])
  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_actor)
    this.fid(R.id.button).setCl {
      actor ? "ping" collect{
        case str: String=> ui{ toast(str.toUpperCase) }

      }
    }
  }
}

class PongActor extends Actor {
  def receive = {
    case "ping" => sender ! "pong from actor"
  }
}