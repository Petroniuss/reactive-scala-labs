package EShop.lab2

import akka.actor.typed.Behavior
import akka.actor.{Actor, ActorRef, Cancellable, Props, Timers}
import akka.event.{Logging, LoggingReceive}

import scala.concurrent.duration._
import scala.language.postfixOps

object CartActor {

  sealed trait Command
  case class AddItem(item: Any)        extends Command
  case class RemoveItem(item: Any)     extends Command
  case object ExpireCart               extends Command
  case object StartCheckout            extends Command
  case object ConfirmCheckoutCancelled extends Command
  case object ConfirmCheckoutClosed    extends Command

  sealed trait Event
  case class CheckoutStarted(checkoutRef: ActorRef) extends Event

  // imho cart actor should create checkout actor
  case class ItemAdded(item: Any)   extends Event
  case class ItemRemoved(item: Any) extends Event

  def props: Props = Props(new CartActor())

  case object ExpireCartTimerKey
}

// this actor should create checkout actor
class CartActor extends Actor with Timers {
  import CartActor._

  private val log                       = Logging(context.system, this)
  val cartTimerDuration: FiniteDuration = 5.seconds

  // guarantees to reschedule timer or create a new one,
  // events received from previous one will not be processed.
  private def scheduleExpireCart(): Unit =
    timers.startSingleTimer(ExpireCartTimerKey, ExpireCart, cartTimerDuration)

  private def cancelExpireCartTimer(): Unit =
    timers.cancel(ExpireCartTimerKey)

  def receive: Receive = empty

  def empty: Receive = LoggingReceive {
    case AddItem(item) =>
      scheduleExpireCart()
      context become nonEmpty(Cart.empty.addItem(item))
    case other =>
      log.warning(s"CartActor[Empty] received $other")
  }

  def nonEmpty(cart: Cart): Receive = LoggingReceive {
    case AddItem(item) =>
      scheduleExpireCart()
      context become nonEmpty(cart.addItem(item))

    case RemoveItem(item) =>
      val newCart = cart.removeItem(item)
      if (newCart.isEmpty) {
        cancelExpireCartTimer()
        context become empty
      } else {
        scheduleExpireCart()
        context become nonEmpty(newCart)
      }

    case ExpireCart =>
      context become empty

    case StartCheckout =>
      cancelExpireCartTimer()
      context.actorOf(Checkout.props(self))
      context become inCheckout(cart)

    case other =>
      log.warning(s"CartActor[NonEmpty] received: $other")
  }

  def inCheckout(cart: Cart): Receive = LoggingReceive {
    case ConfirmCheckoutCancelled =>
      scheduleExpireCart()
      context become nonEmpty(cart)

    case ConfirmCheckoutClosed =>
      context become empty

    case other =>
      log.warning(s"CartActor[inCheckout] nonEmpty received: $other")
  }
}
