package EShop.lab4

import EShop.lab3.OrderManager
import EShop.lab3.OrderManager.Ack
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val system = ActorSystem(Root(), "root")

  system ! "Init"

  Await.result(system.whenTerminated, Duration.Inf)
}

object Root {
  def apply(): Behavior[Any] =
    Behaviors.receive(
      (context, msg) =>
        msg match {
          case "Init" =>
            val orderManager = context.spawnAnonymous(OrderManager.persistent("ala"))
            val listener     = context.spawnAnonymous(Listener())

//            orderManager ! OrderManager.AddItem("Foo", listener)
//            orderManager ! OrderManager.AddItem("Bar", listener)
//
//            orderManager ! OrderManager.Buy(listener)
//            orderManager ! OrderManager.SelectDeliveryAndPaymentMethod("delivery-1", "payment-1", listener)
//            orderManager ! OrderManager.Pay(listener)

            Behaviors.same
          case "Terminate" =>
            context.system.terminate
            Behaviors.stopped
          case _ =>
            Behaviors.same
      }
    )
}

object Listener {
  val log: Logger = LoggerFactory.getLogger(Listener.getClass)

  def apply(): Behavior[Ack] = Behaviors.receiveMessage {
    case OrderManager.Done =>
      log.info("[Done]")
      Behaviors.same
  }
}
