package EShop.lab2

import EShop.lab2.TypedCheckout.Command
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

  def apply(cart: ActorRef[TypedCartActor.Command]): Behavior[TypedCheckout.Command] =
    Behaviors.withTimers(timers => new TypedCheckout(cart, timers).start)
}

class TypedCheckout(cart: ActorRef[TypedCartActor.Command], timers: TimerScheduler[Command]) {
  import TypedCheckout._
  private val log = LoggerFactory.getLogger(TypedCheckout.getClass)

  val checkoutTimerDuration: FiniteDuration = 1 seconds
  val paymentTimerDuration: FiniteDuration  = 1 seconds

  def start: Behavior[TypedCheckout.Command] = {
    scheduleExpireCheckout()
    selectingDelivery()
  }

  def selectingDelivery(): Behavior[TypedCheckout.Command] = Behaviors.receiveMessage {
    case SelectDeliveryMethod(method) =>
      selectingPaymentMethod()
    case TypedCheckout.CancelCheckout =>
      cancelled
    case TypedCheckout.ExpireCheckout =>
      cancelled
    case _msg =>
      log.warn(s"Received unexpected message ${_msg}")
      Behaviors.same
  }

  def selectingPaymentMethod(): Behavior[TypedCheckout.Command] = Behaviors.receiveMessage {
    case SelectPayment(payment) =>
      cancelExpireCheckout()
      scheduleExpirePayment()
      processingPayment()
    case TypedCheckout.CancelCheckout =>
      cancel()
    case TypedCheckout.ExpireCheckout =>
      cancel()
    case _msg =>
      log.warn(s"Received unexpected message ${_msg}")
      Behaviors.same
  }

  def processingPayment(): Behavior[TypedCheckout.Command] = Behaviors.receiveMessage {
    case ConfirmPaymentReceived =>
      close()
    case TypedCheckout.CancelCheckout =>
      cancel()
    case TypedCheckout.ExpirePayment =>
      cancel()
    case _msg =>
      log.warn(s"Received unexpected message ${_msg}")
      Behaviors.same
  }

  def cancelled: Behavior[TypedCheckout.Command] = Behaviors.receiveMessage { _ =>
    log.warn("Cancelled")
    Behaviors.same
  }

  def closed: Behavior[TypedCheckout.Command] = Behaviors.receiveMessage { _ =>
    log.warn("Closed")
    Behaviors.same
  }

  def cancel(): Behavior[TypedCheckout.Command] = {
    cart ! TypedCartActor.ConfirmCheckoutCancelled
    cancelled
  }

  def close(): Behavior[TypedCheckout.Command] = {
    cancelExpirePayment()
    cart ! TypedCartActor.ConfirmCheckoutClosed
    closed
  }

  private def scheduleExpireCheckout(): Unit =
    timers.startSingleTimer(ExpireCheckoutTimerKey, ExpireCheckout, checkoutTimerDuration)

  private def cancelExpireCheckout(): Unit =
    timers.cancel(ExpireCheckoutTimerKey)

  private def scheduleExpirePayment(): Unit =
    timers.startSingleTimer(ExpirePaymentTimerKey, ExpirePayment, paymentTimerDuration)

  private def cancelExpirePayment(): Unit =
    timers.cancel(ExpirePaymentTimerKey)

}
