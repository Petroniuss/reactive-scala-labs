package EShop.lab4

import EShop.lab2.{TypedCartActor, TypedCheckout}
import EShop.lab3.Payment
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit.SerializationSettings
import akka.persistence.typed.PersistenceId
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.duration._
import scala.util.Random

class PersistentCartActorRecoveryTest
  extends ScalaTestWithActorTestKit(EventSourcedBehaviorTestKit.config)
  with AnyFlatSpecLike
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

  import EShop.lab2.TypedCartActor._

  private val id = generatePersistenceId()

  private val eventSourcedTestKit = {
    val cartListener      = testKit.createTestProbe[TypedCartActor.Event].ref
    val checkoutListener  = testKit.createTestProbe[TypedCheckout.Event].ref
    val paymentListener   = testKit.createTestProbe[Payment.Event].ref
    val cartTimerDuration = 1.seconds

    EventSourcedBehaviorTestKit[Command, Event, State](
      system,
      PersistentCartActor(id, cartListener, checkoutListener, paymentListener, cartTimerDuration),
      SerializationSettings.disabled
    )
  }

  override def afterAll(): Unit = testKit.shutdownTestKit()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    eventSourcedTestKit.clear()
  }

  def generatePersistenceId(): String = Random.alphanumeric.take(256).mkString

  it should "recover added item" in {
    val events = ItemAdded("King Lear") :: Nil
    val result = recover(events)

    result.state.isInstanceOf[NonEmpty] shouldBe true
  }

  it should "recover empty cart state" in {
    val events = ItemAdded("King Lear") :: CartEmptied :: Nil
    val result = recover(events)

    result.state shouldBe Empty
  }

  it should "recover expired cart" in {
    val events = ItemAdded("King Lear") :: CartExpired :: Nil
    val result = recover(events)

    result.state shouldBe Empty
  }

  it should "recover inCheckout state" in {
    val events = ItemAdded("King Lear") :: CheckoutSStarted :: Nil
    val result = recover(events)

    result.state.isInstanceOf[InCheckout] shouldBe true
  }

  it should "recover expiry timers" in {
    val events = ItemAdded("King Lear") :: Nil
    val result = recover(events)

    Thread.sleep(1500)

    eventSourcedTestKit.getState() shouldBe Empty
  }

  it should "recover closed state" in {
    val events = ItemAdded("King Lear") :: CheckoutSStarted :: CheckoutClosed :: Nil
    val result = recover(events)

    result.state shouldBe Empty
  }

  it should "recover cancelled state" in {
    val events = ItemAdded("King Lear") :: CheckoutSStarted :: CheckoutCancelled :: Nil
    val result = recover(events)

    result.state.isInstanceOf[NonEmpty] shouldBe true
  }

  private def recover(events: Seq[Event]): EventSourcedBehaviorTestKit.RestartResult[State] = {
    val persistenceTestKit = eventSourcedTestKit.persistenceTestKit
    val persistenceId      = PersistenceId.of(PersistentCartActor.entityTypeHint, id)
    persistenceTestKit.persistForRecovery(persistenceId.id, events)
    eventSourcedTestKit.restart()
  }

}
