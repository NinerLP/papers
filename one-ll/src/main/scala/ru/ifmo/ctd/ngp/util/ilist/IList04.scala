package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 4.
 *
 * @author Maxim Buzdalov
 */
class IList04[T0, T1, T2, T3] private[ilist] (
  private[ilist] val t0: T0,
  private[ilist] val t1: T1,
  private[ilist] val t2: T2,
  private[ilist] val t3: T3
) extends IList with LastElementIs[T3] {
  override def last = t3
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList05(t0, t1, t2, t3, t)
  override type Next[T] = IList05[T0, T1, T2, T3, T]
}

object IList04 {
  implicit def arg0[T](l: IList04[T, _, _, _]): T = l.t0
  implicit def arg1[T](l: IList04[_, T, _, _]): T = l.t1
  implicit def arg2[T](l: IList04[_, _, T, _]): T = l.t2
  implicit def arg3[T](l: IList04[_, _, _, T]): T = l.t3
}
