package EShop.lab2

case class Cart(items: List[String]) {
  def contains(item: String): Boolean = items.contains(item)
  def addItem(item: String): Cart     = Cart(item :: items)
  def removeItem(item: String): Cart  = Cart(items.filterNot(_ == item))
  def size: Int                       = items.size
  def isEmpty: Boolean                = size == 0
}

object Cart {
  def empty: Cart = Cart(Nil)
}
