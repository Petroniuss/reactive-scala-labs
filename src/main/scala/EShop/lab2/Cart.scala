package EShop.lab2

case class Cart(items: List[Any]) {
  def contains(item: Any): Boolean = items.contains(item)
  def addItem(item: Any): Cart     = Cart(item :: items)
  def removeItem(item: Any): Cart  = Cart(items.filterNot(_ == item))
  def size: Int                    = items.size
  def isEmpty: Boolean             = size == 0
}

object Cart {
  def empty: Cart = Cart(Nil)
}

trait CartCheckoutState
case object InCheckout    extends CartCheckoutState
case object NotInCheckout extends CartCheckoutState
