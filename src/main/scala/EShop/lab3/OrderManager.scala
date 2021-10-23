package EShop.lab3

import EShop.lab2.{TypedCartActor, TypedCheckout}
import akka.actor.typed.scaladsl.{Behaviors, StashBuffer}
import akka.actor.typed.{ActorRef, Behavior}
import org.slf4j.{Logger, LoggerFactory}

object OrderManager {

  sealed trait Command
  case class AddItem(id: String, sender: ActorRef[Ack])                                               extends Command
  case class RemoveItem(id: String, sender: ActorRef[Ack])                                            extends Command
  case class SelectDeliveryAndPaymentMethod(delivery: String, payment: String, sender: ActorRef[Ack]) extends Command
  case class Buy(sender: ActorRef[Ack])                                                               extends Command
  case class Pay(sender: ActorRef[Ack])                                                               extends Command
  case class ConfirmCheckoutStarted(checkoutRef: ActorRef[TypedCheckout.Command])                     extends Command
  case class ConfirmPaymentStarted(paymentRef: ActorRef[Payment.Command])                             extends Command
  case object ConfirmPaymentReceived                                                                  extends Command

  sealed trait Ack
  case object Done extends Ack //trivial ACK

  def apply(): Behavior[OrderManager.Command] = Behaviors.withStash(100)(stash => {
    new OrderManager(stash).start
  })

  val log: Logger = LoggerFactory.getLogger(OrderManager.getClass)
}

class OrderManager(stash: StashBuffer[OrderManager.Command]) {
  import OrderManager._

  def start: Behavior[OrderManager.Command] = Behaviors.setup(context => {
    val cartActor = context.spawnAnonymous(TypedCartActor(context.self))
    open(cartActor)
  })

  def open(cartActor: ActorRef[TypedCartActor.Command]): Behavior[OrderManager.Command] =
    Behaviors.receive((_context, msg) => {
      msg match {
        case AddItem(id, sender) =>
          cartActor ! TypedCartActor.AddItem(id)
          sender ! Done
          Behaviors.same
        case RemoveItem(id, sender) =>
          cartActor ! TypedCartActor.RemoveItem(id)
          sender ! Done
          Behaviors.same
        case Buy(sender) =>
          cartActor ! TypedCartActor.StartCheckout
          sender ! Done
          Behaviors.same
        case ConfirmCheckoutStarted(checkoutRef) =>
          log.info("Checkout started")
          stash.unstashAll(inCheckout(checkoutRef))
        case _msg =>
          stash.stash(_msg)
          log.warn(s"[open] Received unexpected message ${_msg}")
          Behaviors.same
      }
    })

  def inCheckout(checkoutActorRef: ActorRef[TypedCheckout.Command]): Behavior[OrderManager.Command] =
    Behaviors.receive((_context, msg) => {
      msg match {
        case SelectDeliveryAndPaymentMethod(delivery, payment, sender) =>
          checkoutActorRef ! TypedCheckout.SelectDeliveryMethod(delivery)
          checkoutActorRef ! TypedCheckout.SelectPayment(payment)
          sender ! Done
          Behaviors.same
        case ConfirmPaymentStarted(paymentRef) =>
          stash.unstashAll(inPayment(paymentRef))
        case _msg =>
          log.warn(s"[inCheckout] Received unexpected message ${_msg}")
          stash.stash(_msg)
          Behaviors.same
      }
    })

  def inPayment(paymentActorRef: ActorRef[Payment.Command]): Behavior[OrderManager.Command] =
    Behaviors.receive((context, msg) => {
      msg match {
        case Pay(sender) =>
          paymentActorRef ! Payment.DoPayment
          sender ! Done
          Behaviors.same
        case ConfirmPaymentReceived =>
          finished
        case _msg =>
          stash.stash(_msg)
          log.warn(s"[inPayment] Received unexpected message ${_msg}")
          Behaviors.same
      }
    })

  def finished: Behavior[OrderManager.Command] = {
    log.info("Finished ordering..")
    start
  }
}
