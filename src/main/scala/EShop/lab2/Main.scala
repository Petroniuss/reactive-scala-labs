package EShop.lab2

import akka.actor.typed.ActorSystem

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val system = ActorSystem(TypedCartActor(), "cart-main-actor")

  system ! TypedCartActor.AddItem("Foo")
  system ! TypedCartActor.AddItem("Bar")
  system ! TypedCartActor.StartCheckout

  Await.result(system.whenTerminated, Duration.Inf)
}
