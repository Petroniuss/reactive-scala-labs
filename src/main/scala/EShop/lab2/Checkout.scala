package EShop.lab2

import EShop.lab2.CartActor.{ExpireCart, ExpireCartTimerKey}
import EShop.lab2.Checkout._
import akka.actor.{Actor, ActorRef, Cancellable, Props, Timers}
import akka.event.{Logging, LoggingReceive}

import scala.concurrent.duration._
import scala.language.postfixOps

object Checkout {

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
  case object CheckoutClosed                   extends Event
  case class PaymentStarted(payment: ActorRef) extends Event

  case object CheckoutTimerKey
  case object PaymentTimerKey

  def props(cartActor: ActorRef): Props = Props(new Checkout(cartActor))
}

class Checkout(cartActor: ActorRef) extends Actor with Timers {

  private val scheduler = context.system.scheduler
  private val log       = Logging(context.system, this)

  val checkoutTimerDuration: FiniteDuration = 1.seconds
  val paymentTimerDuration: FiniteDuration  = 1.seconds

  private def scheduleCheckoutTimer(): Unit =
    timers.startSingleTimer(CheckoutTimerKey, ExpireCheckout, checkoutTimerDuration)

  private def cancelCheckoutTimer(): Unit =
    timers.cancel(ExpireCartTimerKey)

  private def schedulePaymentTimer(): Unit =
    timers.startSingleTimer(PaymentTimerKey, ExpirePayment, paymentTimerDuration)

  private def cancelPaymentTimer(): Unit =
    timers.cancel(PaymentTimerKey)

  def receive: Receive = LoggingReceive {
    case StartCheckout =>
      scheduleCheckoutTimer()
      context become selectingDelivery
  }

  def selectingDelivery: Receive = LoggingReceive {
    case SelectDeliveryMethod(_method) =>
      context become selectingPaymentMethod
    case ExpireCheckout =>
      cancel()
    case CancelCheckout =>
      cancel()
  }

  def selectingPaymentMethod: Receive = LoggingReceive {
    case SelectPayment(_payment) =>
      schedulePaymentTimer()
      cancelCheckoutTimer()
      context become processingPayment
    case ExpireCheckout =>
      cancel()
    case CancelCheckout =>
      cancel()
  }

  def processingPayment: Receive = LoggingReceive {
    case ConfirmPaymentReceived =>
      cancelPaymentTimer()
      close()
    case ExpirePayment =>
      cancel()
    case CancelCheckout =>
      cancel()
  }

  def cancelled: Receive = LoggingReceive {
    case _ =>
      log.info("Checkout cancelled")
  }

  def closed: Receive = LoggingReceive {
    case _ =>
      log.info("Checkout closed")
  }

  def cancel(): Unit = {
    cartActor ! CartActor.ConfirmCheckoutCancelled
    context become cancelled
  }

  def close(): Unit = {
    cartActor ! CartActor.ConfirmCheckoutClosed
    context become closed
  }

}
