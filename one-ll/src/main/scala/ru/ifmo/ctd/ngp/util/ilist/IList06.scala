package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 6.
 *
 * @author Maxim Buzdalov
 */
class IList06[T0, T1, T2, T3, T4, T5] private[ilist] (
  private[ilist] val t0: T0,
  private[ilist] val t1: T1,
  private[ilist] val t2: T2,
  private[ilist] val t3: T3,
  private[ilist] val t4: T4,
  private[ilist] val t5: T5
) extends IList with LastElementIs[T5] {
  override def last = t5
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList07(t0, t1, t2, t3, t4, t5, t)
  override type Next[T] = IList07[T0, T1, T2, T3, T4, T5, T]
}

object IList06 {
  implicit def arg0[T](l: IList06[T, _, _, _, _, _]): T = l.t0
  implicit def arg1[T](l: IList06[_, T, _, _, _, _]): T = l.t1
  implicit def arg2[T](l: IList06[_, _, T, _, _, _]): T = l.t2
  implicit def arg3[T](l: IList06[_, _, _, T, _, _]): T = l.t3
  implicit def arg4[T](l: IList06[_, _, _, _, T, _]): T = l.t4
  implicit def arg5[T](l: IList06[_, _, _, _, _, T]): T = l.t5
}
