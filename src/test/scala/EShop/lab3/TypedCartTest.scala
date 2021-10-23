package EShop.lab3

import EShop.lab2.{Cart, TypedCartActor, TypedCheckout}
import akka.actor.testkit.typed.Effect.SpawnedAnonymous
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, ScalaTestWithActorTestKit}
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

  override def afterAll: Unit =
    testKit.shutdownTestKit()

  import TypedCartActor._

  it should "add item properly" in {
    val orderManager = testKit.createTestProbe[OrderManager.Command].ref
    val cart         = testKit.spawn(TypedCartActor(orderManager))
    val probe        = testKit.createTestProbe[Cart]

    cart ! AddItem("foo")
    cart ! GetItems(probe.ref)

    probe.expectMessage(Cart(List("foo")))
  }

  it should "be empty after adding and removing the same item" in {
    val orderManager = testKit.createTestProbe[OrderManager.Command].ref
    val cart         = testKit.spawn(TypedCartActor(orderManager))
    val probe        = testKit.createTestProbe[Cart]

    val e = "bar"

    cart ! AddItem(e)
    cart ! RemoveItem(e)
    cart ! GetItems(probe.ref)

    probe.expectMessage(Cart(List()))
  }

  it should "start checkout" in {
    val orderManager = testKit.createTestProbe[OrderManager.Command].ref
    val kit = BehaviorTestKit(TypedCartActor(orderManager))

    kit.run(AddItem("foo"))
    kit.run(StartCheckout)

    val effectOption = kit.retrieveAllEffects()
      .collectFirst { case e: SpawnedAnonymous[TypedCheckout.Command] => e }
    effectOption should be (Symbol("defined"))

    val checkout = effectOption.get
    val checkoutInbox = kit.childInbox(checkout.ref)

    checkoutInbox.expectMessage(TypedCheckout.StartCheckout)
  }
}