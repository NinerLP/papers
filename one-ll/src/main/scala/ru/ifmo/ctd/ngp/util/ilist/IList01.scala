package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 1.
 *
 * @author Maxim Buzdalov
 */
class IList01[T0] private[ilist] (
  private[ilist] val t0: T0
) extends IList with LastElementIs[T0] {
  override def last = t0
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList02(t0, t)
  override type Next[T] = IList02[T0, T]
}

object IList01 {
  implicit def arg0[T](l: IList01[T]): T = l.t0
}
