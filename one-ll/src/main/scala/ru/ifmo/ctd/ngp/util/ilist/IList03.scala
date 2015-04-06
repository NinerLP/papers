package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 3.
 *
 * @author Maxim Buzdalov
 */
class IList03[T0, T1, T2] private[ilist] (
  private[ilist] val t0: T0,
  private[ilist] val t1: T1,
  private[ilist] val t2: T2
) extends IList with LastElementIs[T2] {
  override def last = t2
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList04(t0, t1, t2, t)
  override type Next[T] = IList04[T0, T1, T2, T]
}

object IList03 {
  implicit def arg0[T](l: IList03[T, _, _]): T = l.t0
  implicit def arg1[T](l: IList03[_, T, _]): T = l.t1
  implicit def arg2[T](l: IList03[_, _, T]): T = l.t2
}
