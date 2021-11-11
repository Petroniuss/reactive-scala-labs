package EShop.lab4

import EShop.lab2.{TypedCartActor, TypedCheckout}
import EShop.lab3.{OrderManager, Payment}
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit.SerializationSettings
import akka.persistence.typed.PersistenceId
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.duration._
import scala.util.Random

class PersistentCheckoutTest
  extends ScalaTestWithActorTestKit(EventSourcedBehaviorTestKit.config)
  with AnyFlatSpecLike
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

  override def afterAll(): Unit = testKit.shutdownTestKit()

  import EShop.lab2.TypedCheckout._

  private val cartActorProbe = testKit.createTestProbe[TypedCartActor.Command]()

  private val orderManagerProbe = testKit.createTestProbe[OrderManager.Command]

  private val eventSourcedTestKit = {
    val cartRef          = testKit.createTestProbe[TypedCartActor.Command].ref
    val checkoutListener = testKit.createTestProbe[TypedCheckout.Event].ref
    val paymentListener  = testKit.createTestProbe[Payment.Event].ref
    val persistenceId    = generatePersistenceId()
    val timerDuration    = 1.seconds

    EventSourcedBehaviorTestKit[Command, Event, State](
      system,
      PersistentCheckout(persistenceId, cartRef, checkoutListener, paymentListener, timerDuration),
      SerializationSettings.disabled
    )
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    eventSourcedTestKit.clear()
  }

  val deliveryMethod = "post"
  val paymentMethod  = "paypal"

  def generatePersistenceId(): PersistenceId = PersistenceId.ofUniqueId(Random.alphanumeric.take(256).mkString)

  it should "be in selectingDelivery state after checkout start" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery
  }

  it should "be in cancelled state after cancel message received in selectingDelivery State" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultCancelCheckout = eventSourcedTestKit.runCommand(CancelCheckout)

    resultCancelCheckout.event shouldBe CheckoutCancelled
    resultCancelCheckout.state shouldBe Cancelled
  }

  it should "be in cancelled state after expire checkout timeout in selectingDelivery state" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    Thread.sleep(2000)

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.hasNoEvents shouldBe true
    resultSelectDelivery.state shouldBe Cancelled
  }

  it should "be in selectingPayment state after delivery method selected" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod
  }

  it should "be in cancelled state after cancel message received in selectingPayment State" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod

    val resultCancelCheckout = eventSourcedTestKit.runCommand(CancelCheckout)

    resultCancelCheckout.event shouldBe CheckoutCancelled
    resultCancelCheckout.state shouldBe Cancelled
  }

  it should "be in cancelled state after expire checkout timeout in selectingPayment state" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod

    Thread.sleep(2000)

    val resultSelectPayment = eventSourcedTestKit.runCommand(SelectPayment(paymentMethod))

    resultSelectPayment.hasNoEvents shouldBe true
    resultSelectPayment.state shouldBe Cancelled
  }

  it should "be in processingPayment state after payment selected" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod

    val resultSelectPayment = eventSourcedTestKit.runCommand(SelectPayment(paymentMethod))

    resultSelectPayment.event.isInstanceOf[PaymentStarted] shouldBe true
    resultSelectPayment.state shouldBe ProcessingPayment
  }

  it should "be in cancelled state after cancel message received in processingPayment State" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod

    val resultSelectPayment = eventSourcedTestKit.runCommand(SelectPayment(paymentMethod))

    resultSelectPayment.event.isInstanceOf[PaymentStarted] shouldBe true
    resultSelectPayment.state shouldBe ProcessingPayment
    val resultCancelCheckout = eventSourcedTestKit.runCommand(CancelCheckout)

    resultCancelCheckout.event shouldBe CheckoutCancelled
    resultCancelCheckout.state shouldBe Cancelled
  }

  it should "be in cancelled state after expire checkout timeout in processingPayment state" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod

    val resultSelectPayment = eventSourcedTestKit.runCommand(SelectPayment(paymentMethod))

    resultSelectPayment.event.isInstanceOf[PaymentStarted] shouldBe true
    resultSelectPayment.state shouldBe ProcessingPayment
    Thread.sleep(2000)

    val resultReceivePayment = eventSourcedTestKit.runCommand(ConfirmPaymentReceived)

    resultReceivePayment.hasNoEvents shouldBe true
    resultReceivePayment.state shouldBe Cancelled
  }

  it should "be in closed state after payment completed" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod

    val resultSelectPayment = eventSourcedTestKit.runCommand(SelectPayment(paymentMethod))

    resultSelectPayment.event.isInstanceOf[PaymentStarted] shouldBe true
    resultSelectPayment.state shouldBe ProcessingPayment

    val resultReceivePayment = eventSourcedTestKit.runCommand(ConfirmPaymentReceived)

    resultReceivePayment.event shouldBe CheckOutClosed
    resultReceivePayment.state shouldBe Closed
  }

  it should "not change state after cancel msg in completed state" in {
    val resultStartCheckout = eventSourcedTestKit.runCommand(StartCheckout)

    resultStartCheckout.event shouldBe CheckoutStarted
    resultStartCheckout.state shouldBe SelectingDelivery

    val resultSelectDelivery = eventSourcedTestKit.runCommand(SelectDeliveryMethod(deliveryMethod))

    resultSelectDelivery.event.isInstanceOf[DeliveryMethodSelected] shouldBe true
    resultSelectDelivery.state shouldBe SelectingPaymentMethod

    val resultSelectPayment = eventSourcedTestKit.runCommand(SelectPayment(paymentMethod))

    resultSelectPayment.event.isInstanceOf[PaymentStarted] shouldBe true
    resultSelectPayment.state shouldBe ProcessingPayment
    val resultReceivePayment = eventSourcedTestKit.runCommand(ConfirmPaymentReceived)

    resultReceivePayment.event shouldBe CheckOutClosed
    resultReceivePayment.state shouldBe Closed

    val resultCancelCheckout = eventSourcedTestKit.runCommand(CancelCheckout)

    resultCancelCheckout.hasNoEvents shouldBe true
    resultCancelCheckout.state shouldBe Closed
  }
}
