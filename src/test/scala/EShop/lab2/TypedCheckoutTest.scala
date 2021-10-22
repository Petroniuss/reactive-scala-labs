package EShop.lab2

import EShop.lab3.OrderManager
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, ScalaTestWithActorTestKit}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration.{FiniteDuration, _}

class TypedCheckoutTest extends ScalaTestWithActorTestKit with AnyFlatSpecLike with BeforeAndAfterAll {

  val deliveryMethod = "post"
  val paymentMethod  = "paypal"

  import TypedCheckoutTest._
  import TypedCheckout._

  it should "be in selectingDelivery state after checkout start" in {
    val probe         = testKit.createTestProbe[String]
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
  }

  it should "be in cancelled state after cancel message received in selectingDelivery State" in {
    val probe         = testKit.createTestProbe[String]
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
    checkoutActor ! CancelCheckout
    probe.expectMessage(cancelledMsg)
  }

  it should "be in cancelled state after expire checkout timeout in selectingDelivery state" in {
    val probe = testKit.createTestProbe[String]
    val checkoutActor = testKit.spawn {
      Behaviors.withTimers[TypedCheckout.Command](timers => {
        val (orderManager, cart) = env(testKit)
        val checkout = new TypedCheckout(cart, orderManager, timers) {
          override val checkoutTimerDuration: FiniteDuration = 1.seconds

          override def cancelled: Behavior[TypedCheckout.Command] =
            Behaviors.receiveMessage({ _ =>
              probe.ref ! cancelledMsg
              Behaviors.same
            })
        }
        checkout.start
      })
    }

    checkoutActor ! StartCheckout
    Thread.sleep(2000)
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    probe.expectMessage(cancelledMsg)
  }

  it should "be in selectingPayment state after delivery method selected" in {
    val probe         = testKit.createTestProbe[String]
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    probe.expectMessage(selectingPaymentMethodMsg)
  }

  it should "be in cancelled state after cancel message received in selectingPayment State" in {
    val probe         = testKit.createTestProbe[String]
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    probe.expectMessage(selectingPaymentMethodMsg)
    checkoutActor ! CancelCheckout
    probe.expectMessage(cancelledMsg)
  }

  it should "be in cancelled state after expire checkout timeout in selectingPayment state" in {
    val probe = testKit.createTestProbe[String]
    val checkoutActor = testKit.spawn {
      Behaviors.withTimers[TypedCheckout.Command](timers => {
        val (orderManager, cart) = env(testKit)
        val checkout = new TypedCheckout(cart, orderManager, timers) {
          override val checkoutTimerDuration: FiniteDuration = 1.seconds

          override def cancelled: Behavior[TypedCheckout.Command] =
            Behaviors.receiveMessage({ _ =>
              probe.ref ! cancelledMsg
              Behaviors.same
            })
        }
        checkout.start
      })
    }

    checkoutActor ! StartCheckout
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    Thread.sleep(2000)
    checkoutActor ! SelectPayment(paymentMethod)
    probe.expectMessage(cancelledMsg)
  }

  it should "be in processingPayment state after payment selected" in {
    val probe         = testKit.createTestProbe[String]
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    probe.expectMessage(selectingPaymentMethodMsg)
    checkoutActor ! SelectPayment(paymentMethod)
    probe.expectMessage(processingPaymentMsg)
  }

  it should "be in cancelled state after cancel message received in processingPayment State" in {
    val probe         = testKit.createTestProbe[String]
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    probe.expectMessage(selectingPaymentMethodMsg)
    checkoutActor ! SelectPayment(paymentMethod)
    probe.expectMessage(processingPaymentMsg)
    checkoutActor ! CancelCheckout
    probe.expectMessage(cancelledMsg)
  }

  it should "be in cancelled state after expire checkout timeout in processingPayment state" in {
    val probe = testKit.createTestProbe[String]
    val checkoutActor = testKit.spawn {
      Behaviors.withTimers[TypedCheckout.Command](timers => {
        val (orderManager, cart) = env(testKit)
        val checkout = new TypedCheckout(cart, orderManager, timers) {
          override val paymentTimerDuration: FiniteDuration = 1.seconds

          override def cancelled: Behavior[TypedCheckout.Command] =
            Behaviors.receiveMessage({ _ =>
              probe.ref ! cancelledMsg
              Behaviors.same
            })
        }
        checkout.start
      })
    }

    checkoutActor ! StartCheckout
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    checkoutActor ! SelectPayment(paymentMethod)
    Thread.sleep(2000)
    checkoutActor ! ConfirmPaymentReceived
    probe.expectMessage(cancelledMsg)
  }

  it should "be in closed state after payment completed" in {
    val probe         = testKit.createTestProbe[String]()
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    probe.expectMessage(selectingPaymentMethodMsg)
    checkoutActor ! SelectPayment(paymentMethod)
    probe.expectMessage(processingPaymentMsg)
    checkoutActor ! ConfirmPaymentReceived
    probe.expectMessage(closedMsg)
  }

  it should "not change state after cancel msg in completed state" in {
    val probe         = testKit.createTestProbe[String]()
    val checkoutActor = checkoutActorWithResponseOnStateChange(testKit, probe.ref)

    probe.expectMessage(emptyMsg)
    checkoutActor ! StartCheckout
    probe.expectMessage(selectingDeliveryMsg)
    checkoutActor ! SelectDeliveryMethod(deliveryMethod)
    probe.expectMessage(selectingPaymentMethodMsg)
    checkoutActor ! SelectPayment(paymentMethod)
    probe.expectMessage(processingPaymentMsg)
    checkoutActor ! ConfirmPaymentReceived
    probe.expectMessage(closedMsg)
    checkoutActor ! CancelCheckout
    probe.expectNoMessage()
  }

}

object TypedCheckoutTest {

  val emptyMsg                  = "empty"
  val selectingDeliveryMsg      = "selectingDelivery"
  val selectingPaymentMethodMsg = "selectingPaymentMethod"
  val processingPaymentMsg      = "processingPayment"
  val cancelledMsg              = "cancelled"
  val closedMsg                 = "closed"

  def env(testKit: ActorTestKit): (ActorRef[OrderManager.Command], ActorRef[TypedCartActor.Command]) = {
    val orderManager = testKit.spawn(OrderManager())
    val cart = testKit.spawn(TypedCartActor(orderManager))

    (orderManager, cart)
  }

  def checkoutActorWithResponseOnStateChange(
    testKit: ActorTestKit,
    probe: ActorRef[String]
  ): ActorRef[TypedCheckout.Command] =
    testKit.spawn {
      Behaviors.withTimers[TypedCheckout.Command](timers => {
        val (orderManager, cart) = env(testKit)
        val checkout = new TypedCheckout(cart, orderManager, timers) {
          override def start: Behavior[TypedCheckout.Command] =
            Behaviors.setup(_ => {
              probe ! emptyMsg
              super.start
            })

          override def selectingDelivery(): Behavior[TypedCheckout.Command] =
            Behaviors.setup(_ => {
              val result = super.selectingDelivery()
              probe ! selectingDeliveryMsg
              result
            })

          override def selectingPaymentMethod(): Behavior[TypedCheckout.Command] =
            Behaviors.setup(_ => {
              probe ! selectingPaymentMethodMsg
              super.selectingPaymentMethod()
            })

          override def processingPayment(): Behavior[TypedCheckout.Command] =
            Behaviors.setup(_ => {
              probe ! processingPaymentMsg
              super.processingPayment()
            })

          override def cancelled: Behavior[TypedCheckout.Command] =
            Behaviors.setup(_ => {
              probe ! cancelledMsg
              super.cancelled
            })

          override def closed: Behavior[TypedCheckout.Command] =
            Behaviors.setup(_ => {
              probe ! closedMsg
              super.closed
            })
        }
        checkout.start
      })
    }
}
