package com.example.android2111.app
import akka.actor.{Actor, Props, Status}
import akka.pattern._
import akka.util.Timeout
import android.os.Bundle
import com.android.volley.Request.Method
import com.android.volley._
import com.android.volley.toolbox.{HttpHeaderParser, RequestFuture}
import com.example.android2111.app.Implicits._
import com.example.android2111.app.model.CurrentTime
import com.google.gson.Gson

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.reflect.ClassTag

class ActorActivity extends ActivityExtras {
  implicit val timeout: Timeout = 10 seconds
  lazy val actor = App.system.actorOf(Props.apply(classOf[PongActor], "Bajskorv"))
  lazy val pingButton = this.fid(R.id.button1)
  lazy val reqButton = this.fid(R.id.button2)

  override def onCreate(b: Bundle) = {
    super.onCreate(b)
    setContentView(R.layout.activity_actor)
    reqButton.setCl {
      (actor ? Url("http://json-time.appspot.com/time.json")).foreach{
        case ct: CurrentTime => ui{ toast(s"The current time is $ct") }
      }
    }
    pingButton.setCl {
      (actor ? "ping").foreach {
        case "pong" => ui {
          toast("got a pong")
        }
      }
    }
  }
}

class PongActor(name: String) extends Actor with VolleyActor {
  def receive = {
    case "ping" => sender ! "pong"
    case Url(datUrl) =>
      val s = sender
      request[CurrentTime](url = datUrl).foreach{ s ! _ }
  }
}

case class Url(value: String) extends AnyVal

trait VolleyActor extends Actor {
  def request[T : ClassTag](url: String): Future[T] = {
    val f = RequestFuture.newFuture[T]()
    val r = new GsonRequest[T](url = url)(ErrListener(e => sender ! Status.Failure(e)) )
    f.setRequest(r)
    App.requestQueue.add(r)
    r.future
  }
}

case class ErrListener(f: VolleyError => Unit) extends Response.ErrorListener {
  override def onErrorResponse(error: VolleyError): Unit = f(error)
}

case class GsonRequest[T : ClassTag](method: Int = Method.GET, url: String)(listener: ErrListener) extends Request[T](method, url, listener) {
  private lazy val promise = Promise[T]()
  def parseNetworkResponse(response: NetworkResponse): Response[T] = {
    val str = new String(response.data, HttpHeaderParser.parseCharset(response.headers))
    val t = GsonRequest.gson.fromJson[T](str, implicitly[ClassTag[T]].runtimeClass)
    promise.success(t)
    Response.success(t, HttpHeaderParser.parseCacheHeaders(response))
  }
  def future = promise.future
  def deliverResponse(response: T) = ()
}

object GsonRequest {
  val gson = new Gson
}

