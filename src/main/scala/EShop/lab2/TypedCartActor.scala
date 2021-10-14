package EShop.lab2

import EShop.lab2
import EShop.lab2.TypedCartActor.Command
import akka.actor.{Actor, Cancellable, Timers}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.concurrent.duration._

object TypedCartActor {

  sealed trait Command
  case class AddItem(item: Any)        extends Command
  case class RemoveItem(item: Any)     extends Command
  case object ExpireCart               extends Command
  case object StartCheckout            extends Command
  case object ConfirmCheckoutCancelled extends Command
  case object ConfirmCheckoutClosed    extends Command

  sealed trait Event
  case class CheckoutStarted(checkoutRef: ActorRef[Command]) extends Event

  case object ExpireCartTimerKey

  def apply(): Behavior[Command] = {
    Behaviors.withTimers(timers => new TypedCartActor(timers).start)
  }
}

class TypedCartActor(timers: TimerScheduler[Command]) {
  import TypedCartActor._

  val cartTimerDuration: FiniteDuration = 5 seconds
  private val log = LoggerFactory.getLogger(getClass)

  // guarantees to reschedule timer or create a new one,
  // events received from previous one will not be processed.
  private def scheduleExpireCart(): Unit =
    timers.startSingleTimer(ExpireCartTimerKey, ExpireCart, cartTimerDuration)

  private def cancelExpireCartTimer(): Unit =
    timers.cancel(ExpireCartTimerKey)

  def start: Behavior[Command] = empty

  def empty: Behavior[Command] = Behaviors.receive((context, msg) => msg match {
    case AddItem(item) =>
      scheduleExpireCart()
      nonEmpty(Cart.empty.addItem(item))
    case _msg =>
      log.warn("Received unexpected message", msg)
      Behaviors.same
  })

  def nonEmpty(cart: Cart): Behavior[Command] = Behaviors.receive((context, msg) => msg match {
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
      context.spawnAnonymous(TypedCheckout(context.self))
      inCheckout(cart)
    case _msg =>
      log.warn("Received unexpected message", msg)
      Behaviors.same
  })

  def inCheckout(cart: Cart): Behavior[Command] = Behaviors.receive((context, msg) => msg match {
    case ConfirmCheckoutCancelled =>
      nonEmpty(cart)
    case ConfirmCheckoutClosed =>
      empty
    case _msg =>
      log.warn("Received unexpected message", msg)
      Behaviors.same
  })

}
