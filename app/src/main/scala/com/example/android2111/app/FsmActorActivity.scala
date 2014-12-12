package com.example.android2111.app

import akka.actor.FSM
import akka.actor.FSM.Event
import android.app.Activity
import android.os.Bundle
import android.widget.{Button, ImageView}
import Implicits._
import scala.reflect.ClassTag
import akka.pattern._
import concurrent.ExecutionContext.Implicits._

class FsmActorActivity extends Activity with ActivityExtras with FsmActivity {
  type State = DoorState
  type Data = Int
  def actorClass = implicitly[ClassTag[DoorActor]]
  lazy val door = findViewById(R.id.door).asInstanceOf[ImageView]
  lazy val List(openButton, closeButton, stateButton) = List(R.id.open, R.id.close, R.id.state).map{ this.gtTxt }

  private def handleActorResponse(a: Any): Unit = a match {
    case newState: State => newState match {
      case Open => ui { door.setImageResource(R.drawable.door_open) }
      case Closed => ui { door.setImageResource(R.drawable.door_closed) }
    }
    case Already(str) => ui { toast(s"Door is already $str, why would you do that")}
    case (state: DoorState, openCount: Int) => ui{
      toast(s"Door is $state, it was opened $openCount times")
    }
    case str: String => ui { toast(str) }
    case i: Data => ui { toast(s"Door opened $i times") }
    case that => ui { toast(s"Got strange response from actor: $that") }
  }

  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_door)
    List(openButton -> Open, closeButton -> Closed, stateButton -> "state").foreach{
      case (button, message) => button.setCl {
        actor ? message foreach handleActorResponse
      }
    }
  }
}


sealed trait DoorState
case object Open extends DoorState
case object Closed extends DoorState

case class Already(string: DoorState)

class DoorActor extends FSM[DoorState, Int] {
  startWith(Closed, 0)
  when(Closed) {
    case Event(Open, sd) =>
      sender ! Open
      goto(Open) using sd + 1
  }
  when(Open) {
    case Event(Closed, sd) =>
      sender ! Closed
      goto(Closed)
  }

  whenUnhandled {
    case Event(e: DoorState, _) => sender ! Already(e); stay()
    case Event("openCount", _) => sender ! stateData; stay()
    case Event("state", _) => sender ! (stateName, stateData); stay()
  }
}
