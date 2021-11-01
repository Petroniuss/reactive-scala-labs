package EShop.lab4

import EShop.lab2.TypedCheckout.{Command, WaitingForStart}
import EShop.lab2.{TypedCartActor, TypedCheckout}
import EShop.lab3.Payment
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import akka.persistence.typed.{PersistenceId, RecoveryCompleted}
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}

import scala.concurrent.duration._

object PersistentCheckout {
  def apply(
    persistenceId: PersistenceId,
    cartRef: ActorRef[TypedCartActor.Command],
    orderManagerCheckoutListener: ActorRef[TypedCheckout.Event],
    orderManagerPaymentListener: ActorRef[Payment.Event],
    timerDuration: FiniteDuration = 100.seconds
  ): Behavior[Command] = {
    Behaviors.setup { (context: ActorContext[Command]) =>
      Behaviors.withTimers { (timers: TimerScheduler[Command]) =>
        val ref = new PersistentCheckout(
          context,
          timers,
          cartRef,
          orderManagerCheckoutListener,
          orderManagerPaymentListener,
          timerDuration
        )
        EventSourcedBehavior(
          persistenceId,
          WaitingForStart,
          ref.commandHandler(),
          ref.eventHandler()
        ).receiveSignal {
          case (state, RecoveryCompleted) =>
            context.log.info(s"Recovery completed $state")
        }
      }
    }
  }

  val entityTypeHint: String = "PersistentCheckout"
}

class PersistentCheckout(
  context: ActorContext[Command],
  timers: TimerScheduler[Command],
  cartRef: ActorRef[TypedCartActor.Command],
  orderManagerCheckoutListener: ActorRef[TypedCheckout.Event],
  orderManagerPaymentListener: ActorRef[Payment.Event],
  timerDuration: FiniteDuration = 1.seconds
) {
  import EShop.lab2.TypedCheckout._

  def commandHandler(): (State, Command) => Effect[Event, State] = (state, command) => {
    state match {
      case WaitingForStart =>
        command match {
          case StartCheckout =>
            Effect.persist(CheckoutStarted)
          case _ =>
            unhandled(state, command)
        }

      case SelectingDelivery =>
        command match {
          case SelectDeliveryMethod(method) =>
            Effect.persist(DeliveryMethodSelected(method))
          case CancelCheckout | ExpireCheckout =>
            cancel
          case _ =>
            unhandled(state, command)
        }

      case SelectingPaymentMethod =>
        command match {
          case SelectPayment(payment) =>
            val paymentBehavior = Payment(payment, orderManagerPaymentListener, context.self)
            val paymentRef      = context.spawnAnonymous(paymentBehavior)
            val event           = PaymentStarted(paymentRef)
            orderManagerCheckoutListener ! event
            Effect.persist(event)
          case CancelCheckout | ExpireCheckout =>
            cancel
          case _ =>
            unhandled(state, command)
        }

      case ProcessingPayment =>
        command match {
          case ConfirmPaymentReceived =>
            close
          case CancelCheckout | ExpirePayment =>
            cancel
          case _ =>
            unhandled(state, command)
        }

      case Cancelled =>
        unhandled(state, command)

      case Closed =>
        unhandled(state, command)
    }
  }

  private val close: Effect[Event, State] =
    Effect.persist(CheckOutClosed)

  private val cancel: Effect[Event, State] =
    Effect.persist(CheckoutCancelled)

  private def unhandled(state: State, command: Command): Effect[Event, State] =
    Effect.unhandled
      .thenRun { _ =>
        context.log.warn(s"Actor in [$state] received unexpected $command")
      }

  def eventHandler(): (State, Event) => State = (_, event) => {
    event match {
      case CheckoutStarted =>
        reScheduleExpireCheckout()
        SelectingDelivery
      case DeliveryMethodSelected(_) =>
        SelectingPaymentMethod
      case PaymentStarted(_) =>
        cancelExpireCheckout()
        reScheduleExpirePayment()
        ProcessingPayment
      case CheckOutClosed =>
        cancelTimers()
        Closed
      case CheckoutCancelled =>
        cancelTimers()
        Cancelled
    }
  }

  private def cancelTimers(): Unit = {
    cancelExpireCheckout()
    cancelExpirePayment()
  }

  private def reScheduleExpireCheckout(): Unit =
    timers.startSingleTimer(ExpireCheckoutTimerKey, ExpireCheckout, timerDuration)

  private def cancelExpireCheckout(): Unit =
    timers.cancel(ExpireCheckoutTimerKey)

  private def reScheduleExpirePayment(): Unit =
    timers.startSingleTimer(ExpirePaymentTimerKey, ExpirePayment, timerDuration)

  private def cancelExpirePayment(): Unit =
    timers.cancel(ExpirePaymentTimerKey, ExpirePayment, timerDuration)
}
