package EShop.lab2

import EShop.lab3.OrderManager
import EShop.lab3.OrderManager.Ack
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import org.slf4j.{Logger, LoggerFactory}

import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val system = ActorSystem(Root(), "root")
  Await.result(system.whenTerminated, Duration.Inf)
}

object Root {
  def apply(): Behavior[Any] = Behaviors.setup(context => {
    val orderManager = context.spawn(OrderManager(), OrderManager.actorName())
    val sender = context.spawn(SimpleSender(), SimpleSender.actorName())
    
    orderManager ! OrderManager.AddItem("Foo", sender)
    orderManager ! OrderManager.AddItem("Bar", sender)

    orderManager ! OrderManager.Buy(sender)
    orderManager ! OrderManager.SelectDeliveryAndPaymentMethod("delivery-1", "payment-1", sender)
    orderManager ! OrderManager.Pay(sender)

    Behaviors.same
  })

}

object SimpleSender {
  val log: Logger = LoggerFactory.getLogger(SimpleSender.getClass)

  def apply(): Behavior[Ack] = Behaviors.receiveMessage {
    case OrderManager.Done =>
      log.info("[Done]")
      Behaviors.same
  }

  def actorName(): String = s"simple-sender-${UUID.randomUUID.toString}"
}
