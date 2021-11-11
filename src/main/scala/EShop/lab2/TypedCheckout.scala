package EShop.lab2

import EShop.lab2.TypedCheckout.Command
import EShop.lab3.Payment
import akka.actor.Cancellable
import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps

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
  case object PaymentRejected                                                                extends Command
  case object PaymentRestarted                                                               extends Command

  sealed trait Event
  case object CheckOutClosed                                    extends Event
  case class PaymentStarted(payment: ActorRef[Payment.Command]) extends Event
  case object CheckoutStarted                                   extends Event
  case object CheckoutCancelled                                 extends Event
  case class DeliveryMethodSelected(method: String)             extends Event

  sealed abstract class State
  case object WaitingForStart        extends State
  case object SelectingDelivery      extends State
  case object SelectingPaymentMethod extends State
  case object Closed                 extends State
  case object Cancelled              extends State
  case object ProcessingPayment      extends State

  case object ExpireCheckoutTimerKey
  case object ExpirePaymentTimerKey

  def apply(
    cart: ActorRef[TypedCartActor.Command],
    orderManagerCheckoutListener: ActorRef[TypedCheckout.Event],
    orderManagerPaymentListener: ActorRef[Payment.Event]
  ): Behavior[Command] =
    Behaviors.withTimers(
      timers => new TypedCheckout(cart, orderManagerCheckoutListener, orderManagerPaymentListener, timers).start
    )
}

class TypedCheckout(
  cart: ActorRef[TypedCartActor.Command],
  orderManagerCheckoutListener: ActorRef[TypedCheckout.Event],
  orderManagerPaymentListener: ActorRef[Payment.Event],
  timers: TimerScheduler[Command]
) {
  import TypedCheckout._
  private val log = LoggerFactory.getLogger(getClass)

  val checkoutTimerDuration: FiniteDuration = 1 seconds
  val paymentTimerDuration: FiniteDuration  = 1 seconds

  def start: Behavior[Command] = Behaviors.receiveMessage {
    case StartCheckout =>
      scheduleExpireCheckout()
      selectingDelivery()
    case _msg =>
      log.warn(s"[start] Received unexpected message ${_msg}")
      Behaviors.same
  }

  def selectingDelivery(): Behavior[Command] = Behaviors.receiveMessage {
    case SelectDeliveryMethod(method) =>
      selectingPaymentMethod()
    case CancelCheckout =>
      cancelled
    case ExpireCheckout =>
      cancelled
    case _msg =>
      log.warn(s"[delivery] Received unexpected message ${_msg}")
      Behaviors.same
  }

  def selectingPaymentMethod(): Behavior[Command] =
    Behaviors.receive((context, msg) => {
      msg match {
        case SelectPayment(payment) =>
          cancelExpireCheckout()
          val paymentRef = context.spawnAnonymous(Payment(payment, orderManagerPaymentListener, context.self))
          orderManagerCheckoutListener ! PaymentStarted(paymentRef)
          scheduleExpirePayment()
          processingPayment()
        case CancelCheckout =>
          cancel()
        case ExpireCheckout =>
          cancel()
        case _msg =>
          log.warn(s"[selecting-payment] Received unexpected message ${_msg}")
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
      log.warn(s"[processing-payment] Received unexpected message ${_msg}")
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
