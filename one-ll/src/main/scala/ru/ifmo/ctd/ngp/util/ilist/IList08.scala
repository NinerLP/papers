package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 8.
 *
 * @author Maxim Buzdalov
 */
class IList08[T0, T1, T2, T3, T4, T5, T6, T7] private[ilist] (
  private[ilist] val t0: T0,
  private[ilist] val t1: T1,
  private[ilist] val t2: T2,
  private[ilist] val t3: T3,
  private[ilist] val t4: T4,
  private[ilist] val t5: T5,
  private[ilist] val t6: T6,
  private[ilist] val t7: T7
) extends IList with LastElementIs[T7] {
  override def last = t7
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    new IList09(t0, t1, t2, t3, t4, t5, t6, t7, t)
  override type Next[T] = IList09[T0, T1, T2, T3, T4, T5, T6, T7, T]
}

object IList08 {
  implicit def arg0[T](l: IList08[T, _, _, _, _, _, _, _]): T = l.t0
  implicit def arg1[T](l: IList08[_, T, _, _, _, _, _, _]): T = l.t1
  implicit def arg2[T](l: IList08[_, _, T, _, _, _, _, _]): T = l.t2
  implicit def arg3[T](l: IList08[_, _, _, T, _, _, _, _]): T = l.t3
  implicit def arg4[T](l: IList08[_, _, _, _, T, _, _, _]): T = l.t4
  implicit def arg5[T](l: IList08[_, _, _, _, _, T, _, _]): T = l.t5
  implicit def arg6[T](l: IList08[_, _, _, _, _, _, T, _]): T = l.t6
  implicit def arg7[T](l: IList08[_, _, _, _, _, _, _, T]): T = l.t7
}
