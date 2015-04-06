package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 7.
 *
 * @author Maxim Buzdalov
 */
class IList07[T0, T1, T2, T3, T4, T5, T6] private[ilist] (
  private[ilist] val t0: T0,
  private[ilist] val t1: T1,
  private[ilist] val t2: T2,
  private[ilist] val t3: T3,
  private[ilist] val t4: T4,
  private[ilist] val t5: T5,
  private[ilist] val t6: T6
) extends IList with LastElementIs[T6] {
  override def last = t6
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList08(t0, t1, t2, t3, t4, t5, t6, t)
  override type Next[T] = IList08[T0, T1, T2, T3, T4, T5, T6, T]
}

object IList07 {
  implicit def arg0[T](l: IList07[T, _, _, _, _, _, _]): T = l.t0
  implicit def arg1[T](l: IList07[_, T, _, _, _, _, _]): T = l.t1
  implicit def arg2[T](l: IList07[_, _, T, _, _, _, _]): T = l.t2
  implicit def arg3[T](l: IList07[_, _, _, T, _, _, _]): T = l.t3
  implicit def arg4[T](l: IList07[_, _, _, _, T, _, _]): T = l.t4
  implicit def arg5[T](l: IList07[_, _, _, _, _, T, _]): T = l.t5
  implicit def arg6[T](l: IList07[_, _, _, _, _, _, T]): T = l.t6
}
