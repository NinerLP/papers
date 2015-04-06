package ru.ifmo.ctd.ngp.util.ilist

/**
 * An empty implicit list.
 *
 * @author Maxim Buzdalov
 */
object INil extends IList {
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList01[T](t)
  override type Next[T] = IList01[T]
}
