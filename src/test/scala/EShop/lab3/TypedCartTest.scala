package EShop.lab3

import EShop.lab2.{Cart, TypedCartActor, TypedCheckout}
import akka.actor.testkit.typed.Effect.SpawnedAnonymous
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, ScalaTestWithActorTestKit}
import akka.actor.typed.Behavior
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class TypedCartTest
  extends ScalaTestWithActorTestKit
  with AnyFlatSpecLike
  with BeforeAndAfterAll
  with Matchers
  with ScalaFutures {

  override def afterAll(): Unit =
    testKit.shutdownTestKit()

  import TypedCartActor._

  it should "add item properly" in {
    val cart  = testKit.spawn(cartBehavior())
    val probe = testKit.createTestProbe[Cart]

    cart ! AddItem("foo")
    cart ! GetItems(probe.ref)

    probe.expectMessage(Cart(List("foo")))
  }

  it should "be empty after adding and removing the same item" in {
    val cart  = testKit.spawn(cartBehavior())
    val probe = testKit.createTestProbe[Cart]

    val e = "bar"

    cart ! AddItem(e)
    cart ! RemoveItem(e)
    cart ! GetItems(probe.ref)

    probe.expectMessage(Cart(List()))
  }

  it should "be empty after cart expiring" in {
    val cart  = testKit.spawn(cartBehavior())
    val probe = testKit.createTestProbe[Cart]

    val e = "bar"

    cart ! AddItem(e)
    cart ! ExpireCart
    cart ! GetItems(probe.ref)

    probe.expectMessage(Cart(List()))
  }

  it should "not be empty after cancelling checkout" in {
    val cart  = testKit.spawn(cartBehavior())
    val probe = testKit.createTestProbe[Cart]

    val e = "bar"

    cart ! AddItem(e)
    cart ! StartCheckout
    cart ! ConfirmCheckoutCancelled
    cart ! GetItems(probe.ref)

    probe.expectMessage(Cart(List(e)))
  }

  it should "be empty after checkout" in {
    val cart  = testKit.spawn(cartBehavior())
    val probe = testKit.createTestProbe[Cart]

    val e = "bar"

    cart ! AddItem(e)
    cart ! StartCheckout
    cart ! ConfirmCheckoutClosed
    cart ! GetItems(probe.ref)

    probe.expectMessage(Cart(List()))
  }

  it should "spawn checkoutActor and send startCheckout" in {
    val cart = BehaviorTestKit(cartBehavior())

    cart.run(AddItem("foo"))
    cart.run(StartCheckout)

    val effectOption = cart
      .retrieveAllEffects()
      .collectFirst { case e: SpawnedAnonymous[TypedCheckout.Command] => e }
    effectOption should be(Symbol("defined"))

    val checkout      = effectOption.get
    val checkoutInbox = cart.childInbox(checkout.ref)

    checkoutInbox.expectMessage(TypedCheckout.StartCheckout)
  }

  def cartBehavior(): Behavior[Command] = {
    val cartListener     = testKit.createTestProbe[TypedCartActor.Event].ref
    val checkoutListener = testKit.createTestProbe[TypedCheckout.Event].ref
    val paymentListener  = testKit.createTestProbe[Payment.Event].ref

    TypedCartActor(cartListener, checkoutListener, paymentListener)
  }
}
