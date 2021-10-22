package EShop.lab2

import akka.actor.{ActorSystem, Props}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object EShopApp extends App {
  val system    = ActorSystem("Reactive2")
  val mainActor = system.actorOf(Props[CartActor], "mainActor")

  mainActor ! CartActor.AddItem("Foo")
  mainActor ! CartActor.AddItem("Bar")
  mainActor ! CartActor.StartCheckout

  Await.result(system.whenTerminated, Duration.Inf)
}
