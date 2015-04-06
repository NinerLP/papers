package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 2.
 *
 * @author Maxim Buzdalov
 */
class IList02[T0, T1] private[ilist] (
  private[ilist] val t0: T0,
  private[ilist] val t1: T1
) extends IList with LastElementIs[T1] {
  override def last = t1
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList03(t0, t1, t)
  override type Next[T] = IList03[T0, T1, T]
}

object IList02 {
  implicit def arg0[T](l: IList02[T, _]): T = l.t0
  implicit def arg1[T](l: IList02[_, T]): T = l.t1
}
