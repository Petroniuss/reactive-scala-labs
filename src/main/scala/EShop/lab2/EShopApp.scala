package EShop.lab2

import akka.actor.{ActorSystem, Props}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class EShopApp extends App {
  val system    = ActorSystem("Reactive2")
  val mainActor = system.actorOf(Props[CartActor], "mainActor")

  mainActor ! "Init"

  Await.result(system.whenTerminated, Duration.Inf)
  // todo: implement app and add a couple of events happening between cart and checkout
  // todo: implement typed actors.
}
