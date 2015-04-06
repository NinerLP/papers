package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An implicit list.
 *
 * @author Maxim Buzdalov
 */
trait IList {
  type Next[T] <: IList with LastElementIs[T]
  def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T]
}

object IList {
  trait LastElementIs[T] {
    def last: T
  }
  implicit def lastElement[T](arg: LastElementIs[T]) = arg.last
}
