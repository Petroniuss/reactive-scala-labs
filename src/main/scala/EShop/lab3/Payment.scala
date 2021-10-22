package EShop.lab3

import EShop.lab2.TypedCheckout
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.util.UUID

object Payment {

  sealed trait Command
  case object DoPayment extends Command

  def apply(method: String,
            orderManager: ActorRef[OrderManager.Command],
            checkout: ActorRef[TypedCheckout.Command]): Behavior[Payment.Command] =
    new Payment(method, orderManager, checkout).start

  def actorName(): String = s"payment-actor-${UUID.randomUUID.toString}"
}

class Payment(
  method: String,
  orderManager: ActorRef[OrderManager.Command],
  checkout: ActorRef[TypedCheckout.Command]
) {

  import Payment._

  def start: Behavior[Payment.Command] = Behaviors.receive((context, msg) => {
    msg match {
      case DoPayment =>
        orderManager ! OrderManager.ConfirmPaymentReceived
        checkout ! TypedCheckout.ConfirmPaymentReceived
        Behaviors.stopped
    }
  })

}
