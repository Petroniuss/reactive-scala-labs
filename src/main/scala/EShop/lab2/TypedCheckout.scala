package EShop.lab2

import EShop.lab2.TypedCheckout.Command
import EShop.lab3.{OrderManager, Payment}
import akka.actor.Cancellable
import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.concurrent.duration._

object TypedCheckout {

  sealed trait Data
  case object Uninitialized                               extends Data
  case class SelectingDeliveryStarted(timer: Cancellable) extends Data
  case class ProcessingPaymentStarted(timer: Cancellable) extends Data

  sealed trait Command
  case object StartCheckout                       extends Command
  case class SelectDeliveryMethod(method: String) extends Command
  case object CancelCheckout                      extends Command
  case object ExpireCheckout                      extends Command
  case class SelectPayment(payment: String)       extends Command
  case object ExpirePayment                       extends Command
  case object ConfirmPaymentReceived              extends Command

  sealed trait Event
  case object CheckOutClosed                        extends Event
  case class PaymentStarted(payment: ActorRef[Any]) extends Event

  case object ExpireCheckoutTimerKey
  case object ExpirePaymentTimerKey

  def apply(cart: ActorRef[TypedCartActor.Command],
            orderManager: ActorRef[OrderManager.Command]): Behavior[Command] =
    Behaviors.withTimers(timers => new TypedCheckout(cart, orderManager, timers).start)
}

class TypedCheckout(cart: ActorRef[TypedCartActor.Command],
                    orderManager: ActorRef[OrderManager.Command],
                    timers: TimerScheduler[Command]) {
  import TypedCheckout._
  private val log = LoggerFactory.getLogger(getClass)

  val checkoutTimerDuration: FiniteDuration = 1 seconds
  val paymentTimerDuration: FiniteDuration  = 1 seconds

  def start: Behavior[Command] = Behaviors.receiveMessage {
    case StartCheckout =>
      scheduleExpireCheckout()
      selectingDelivery()
  }

  def selectingDelivery(): Behavior[Command] = Behaviors.receiveMessage {
    case SelectDeliveryMethod(method) =>
      selectingPaymentMethod()
    case CancelCheckout =>
      cancelled
    case ExpireCheckout =>
      cancelled
    case _msg =>
      log.warn(s"Received unexpected message ${_msg}")
      Behaviors.same
  }

  def selectingPaymentMethod(): Behavior[Command] = Behaviors.receive((context, msg) => {
    msg match {
      case SelectPayment(payment) =>
        cancelExpireCheckout()
        val paymentRef = context.spawnAnonymous(Payment(payment, orderManager, context.self))
        orderManager ! OrderManager.ConfirmPaymentStarted(paymentRef)
        scheduleExpirePayment()
        processingPayment()
      case CancelCheckout =>
        cancel()
      case ExpireCheckout =>
        cancel()
      case _msg =>
        log.warn(s"Received unexpected message ${_msg}")
        Behaviors.same
    }
  })

  def processingPayment(): Behavior[Command] = Behaviors.receiveMessage {
    case ConfirmPaymentReceived =>
      close()
    case CancelCheckout =>
      cancel()
    case ExpirePayment =>
      cancel()
    case _msg =>
      log.warn(s"Received unexpected message ${_msg}")
      Behaviors.same
  }

  def cancelled: Behavior[Command] = Behaviors.stopped

  def closed: Behavior[Command] = Behaviors.stopped

  def cancel(): Behavior[Command] = {
    cart ! TypedCartActor.ConfirmCheckoutCancelled
    cancelled
  }

  def close(): Behavior[Command] = {
    cart ! TypedCartActor.ConfirmCheckoutClosed
    closed
  }

  private def scheduleExpireCheckout(): Unit =
    timers.startSingleTimer(ExpireCheckoutTimerKey, ExpireCheckout, checkoutTimerDuration)

  private def cancelExpireCheckout(): Unit =
    timers.cancel(ExpireCheckoutTimerKey)

  private def scheduleExpirePayment(): Unit =
    timers.startSingleTimer(ExpirePaymentTimerKey, ExpirePayment, paymentTimerDuration)

}
