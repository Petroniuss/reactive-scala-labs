package EShop.lab2

import EShop.lab2.TypedCartActor.Command
import EShop.lab3.OrderManager
import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.language.postfixOps
import scala.concurrent.duration._

// cleanup?
object TypedCartActor {

  sealed trait Command
  case class AddItem(item: String)                                          extends Command
  case class RemoveItem(item: String)                                       extends Command
  case object ExpireCart                                                    extends Command
  case object StartCheckout                                                 extends Command
  case object ConfirmCheckoutCancelled                                      extends Command
  case object ConfirmCheckoutClosed                                         extends Command
  case class GetItems(sender: ActorRef[Cart])                               extends Command

  sealed trait Event
  case class CheckoutStarted(checkoutRef: ActorRef[Command]) extends Event

  case object ExpireCartTimerKey

  def apply(orderManager: ActorRef[OrderManager.Command]): Behavior[Command] =
    Behaviors.withTimers(timers => new TypedCartActor(timers, orderManager).start)

}

class TypedCartActor(timers: TimerScheduler[Command],
                     orderManager: ActorRef[OrderManager.Command]) {
  import TypedCartActor._

  val cartTimerDuration: FiniteDuration = 5 seconds
  private val log                       = LoggerFactory.getLogger(getClass)

  private def scheduleExpireCart(): Unit =
    timers.startSingleTimer(ExpireCartTimerKey, ExpireCart, cartTimerDuration)

  private def cancelExpireCartTimer(): Unit =
    timers.cancel(ExpireCartTimerKey)

  def start: Behavior[Command] = empty

  def empty: Behavior[Command] =
    Behaviors.receive(
      (context, msg) =>
        msg match {
          case AddItem(item) =>
            scheduleExpireCart()
            nonEmpty(Cart.empty.addItem(item))
          case GetItems(replyTo) =>
            replyTo ! Cart.empty
            Behaviors.same
          case _msg =>
            log.warn(s"[Empty] Received unexpected message ${_msg}")
            Behaviors.same
      }
    )

  def nonEmpty(cart: Cart): Behavior[Command] =
    Behaviors.receive(
      (context, msg) =>
        msg match {
          case AddItem(item) =>
            scheduleExpireCart()
            nonEmpty(cart.addItem(item))
          case ExpireCart =>
            empty
          case RemoveItem(item) =>
            val newCart = cart.removeItem(item)
            if (newCart.isEmpty) {
              cancelExpireCartTimer()
              empty
            } else {
              scheduleExpireCart()
              nonEmpty(newCart)
            }
          case StartCheckout =>
            cancelExpireCartTimer()
            val checkout = context.spawnAnonymous(TypedCheckout(context.self, orderManager))
            checkout ! TypedCheckout.StartCheckout
            orderManager ! OrderManager.ConfirmCheckoutStarted(checkout)
            inCheckout(cart)
          case GetItems(replyTo) =>
            replyTo ! cart
            Behaviors.same
          case _msg =>
            log.warn(s"[NonEmpty] Received unexpected message ${_msg}")
            Behaviors.same
      }
    )

  def inCheckout(cart: Cart): Behavior[Command] =
    Behaviors.receive(
      (context, msg) =>
        msg match {
          case ConfirmCheckoutCancelled =>
            nonEmpty(cart)
          case ConfirmCheckoutClosed =>
            empty
          case GetItems(replyTo) =>
            replyTo ! cart
            Behaviors.same
          case _msg =>
            log.warn(s"[InCheckout] Received unexpected message ${_msg}")
            Behaviors.same
      }
    )

}
