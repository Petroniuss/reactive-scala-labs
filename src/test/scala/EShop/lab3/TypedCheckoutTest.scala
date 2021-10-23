package EShop.lab3

import EShop.lab2.{TypedCartActor, TypedCheckout}
import akka.actor.testkit.typed.Effect.SpawnedAnonymous
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, ScalaTestWithActorTestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class TypedCheckoutTest
  extends ScalaTestWithActorTestKit
  with AnyFlatSpecLike
  with BeforeAndAfterAll
  with Matchers
  with ScalaFutures {

  import EShop.lab2.TypedCheckout._

  override def afterAll: Unit =
    testKit.shutdownTestKit()

  it should "Send close confirmation to cart" in {
    val orderManager = testKit.createTestProbe[OrderManager.Command]
    val cart = testKit.createTestProbe[TypedCartActor.Command]
    val kit = BehaviorTestKit(TypedCheckout(cart.ref, orderManager.ref))

    kit.run(StartCheckout)
    kit.run(SelectDeliveryMethod("m"))
    kit.run(SelectPayment("p"))
    kit.run(ConfirmPaymentReceived)

    cart.expectMessage(TypedCartActor.ConfirmCheckoutClosed)
  }

  it should "Spawn Payment actor when SelectPayment is received" in {
    val orderManager = testKit.createTestProbe[OrderManager.Command]
    val cart = testKit.createTestProbe[TypedCartActor.Command]
    val kit = BehaviorTestKit(TypedCheckout(cart.ref, orderManager.ref))

    kit.run(StartCheckout)
    kit.run(SelectDeliveryMethod("m"))
    kit.run(SelectPayment("p"))

    val effectOption = kit.retrieveAllEffects()
      .collectFirst { case e: SpawnedAnonymous[Payment.Command] => e }
    effectOption should be (Symbol("defined"))
  }

  it should "send cancel confirmation to cart actor when checkout is cancelled" in {
    val orderManager = testKit.createTestProbe[OrderManager.Command]
    val cart = testKit.createTestProbe[TypedCartActor.Command]
    val kit = BehaviorTestKit(TypedCheckout(cart.ref, orderManager.ref))

    kit.run(StartCheckout)
    kit.run(SelectDeliveryMethod("m"))
    kit.run(CancelCheckout)

    cart.expectMessage(TypedCartActor.ConfirmCheckoutCancelled)
  }

  it should "send cancel confirmation to cart actor when checkout is expired" in {
    val orderManager = testKit.createTestProbe[OrderManager.Command]
    val cart = testKit.createTestProbe[TypedCartActor.Command]
    val kit = BehaviorTestKit(TypedCheckout(cart.ref, orderManager.ref))

    kit.run(StartCheckout)
    kit.run(SelectDeliveryMethod("m"))
    kit.run(ExpireCheckout)

    cart.expectMessage(TypedCartActor.ConfirmCheckoutCancelled)
  }

}
