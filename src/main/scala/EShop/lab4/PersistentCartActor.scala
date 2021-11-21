package EShop.lab4

import EShop.lab2.TypedCartActor._
import EShop.lab2.{Cart, TypedCartActor, TypedCheckout}
import EShop.lab3.Payment
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior, PreRestart}
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import akka.persistence.typed.{PersistenceId, RecoveryCompleted}

import scala.concurrent.duration._

object PersistentCartActor {
  def apply(
    id: String,
    orderManagerCartListener: ActorRef[Event],
    orderManagerCheckoutListener: ActorRef[TypedCheckout.Event],
    orderManagerPaymentListener: ActorRef[Payment.Event],
    cartTimerDuration: FiniteDuration = 100.seconds,
  ): Behavior[TypedCartActor.Command] = {
    Behaviors.setup { (context: ActorContext[Command]) =>
      Behaviors.withTimers { (timers: TimerScheduler[Command]) =>
        val persistenceId = PersistenceId.of(PersistentCartActor.entityTypeHint, id)
        val ref = new PersistentCartActor(
          context,
          timers,
          id,
          orderManagerCartListener,
          orderManagerCheckoutListener,
          orderManagerPaymentListener,
          cartTimerDuration
        )
        EventSourcedBehavior[Command, Event, State](
          persistenceId,
          Empty,
          ref.commandHandler(),
          ref.eventHandler()
        ).receiveSignal {
          case (state, RecoveryCompleted) =>
            context.log.info(s"Recovery completed $state")
        }
      }
    }
  }

  val entityTypeHint: String = "PersistentCartActor"
}

class PersistentCartActor(
  context: ActorContext[Command],
  timers: TimerScheduler[Command],
  id: String,
  orderManagerCartListener: ActorRef[Event],
  orderManagerCheckoutListener: ActorRef[TypedCheckout.Event],
  orderManagerPaymentListener: ActorRef[Payment.Event],
  cartTimerDuration: FiniteDuration
) {
  import EShop.lab2.TypedCartActor._

  // commandHandler should handle some sort of validation
  // via coordination with other actors etc after this operating it can do call Effect.thenRun
  // and do stateful things
  def commandHandler(): (State, Command) => Effect[Event, State] = (state, command) => {
    state match {
      case Empty =>
        command match {
          case AddItem(item) =>
            Effect.persist(ItemAdded(item))
          case GetItems(replyTo) =>
            Effect.reply(replyTo)(Cart.empty)
          case _ =>
            unhandled(state, command)
        }
      case NonEmpty(cart) =>
        command match {
          case AddItem(item) =>
            Effect.persist(ItemAdded(item))
          case RemoveItem(item) =>
            if (cart.contains(item)) {
              if (cart.size == 1)
                Effect.persist(CartEmptied)
              else
                Effect.persist(ItemRemoved(item))
            } else {
              Effect.none
            }
          case GetItems(replyTo) =>
            Effect.reply(replyTo)(cart)
          case ExpireCart =>
            println("Received Expire Cart!")
            Effect.persist(CartExpired)
          case StartCheckout =>
            Effect
              .persist(CheckoutSStarted)
              .thenRun {
                case InCheckout(_, checkoutRef) =>
                  checkoutRef ! TypedCheckout.StartCheckout
                  orderManagerCartListener ! CheckoutStarted(checkoutRef)
              }
          case _ =>
            unhandled(state, command)
        }
      case InCheckout(cart, _) =>
        command match {
          case GetItems(replyTo) =>
            Effect.reply(replyTo)(cart)
          case ConfirmCheckoutCancelled =>
            Effect.persist(CheckoutCancelled)
          case ConfirmCheckoutClosed =>
            Effect.persist(CheckoutClosed)
          case _ =>
            unhandled(state, command)
        }
    }
  }

  private def unhandled(state: State, command: Command): Effect[Event, State] =
    Effect.unhandled
      .thenRun { _ =>
        context.log.warn(s"Actor in [$state] received unexpected $command")
      }

  // checkout actor is a persistent actor so we need to create him it commandHandler
  // start/stop timers otherwise we wouldn't be able to recreate it.
  // timers?
  // As timers are a transient state they should be created in eventHandler.
  // transient actors should be created in EventHandler
  // we're creating checkout actor here as during recovering we want this actor
  // to recover its state
  def eventHandler(): (State, Event) => State = (state, event) => {
    event match {
      case ItemAdded(item) =>
        reScheduleExpireCart()
        NonEmpty(state.cart.addItem(item))
      case ItemRemoved(item) =>
        reScheduleExpireCart()
        NonEmpty(state.cart.removeItem(item))
      case CheckoutSStarted =>
        cancelExpireCartTimer()
        val checkoutBehavior = PersistentCheckout(
          PersistenceId.of(PersistentCheckout.entityTypeHint, id),
          context.self,
          orderManagerCheckoutListener,
          orderManagerPaymentListener
        )
        val checkoutRef = context.spawnAnonymous(checkoutBehavior)
        InCheckout(state.cart, checkoutRef)
      case CartEmptied | CartExpired =>
        cancelExpireCartTimer()
        Empty
      case CheckoutClosed =>
        Empty
      case CheckoutCancelled =>
        reScheduleExpireCart()
        NonEmpty(state.cart)
    }
  }

  private def reScheduleExpireCart(): Unit =
    timers.startSingleTimer(ExpireCartTimerKey, ExpireCart, 1.millisecond)

  private def cancelExpireCartTimer(): Unit =
    timers.cancel(ExpireCartTimerKey)
}
