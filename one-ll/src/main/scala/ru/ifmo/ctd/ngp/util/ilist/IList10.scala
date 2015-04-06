package ru.ifmo.ctd.ngp.util.ilist

import scala.language.{existentials, higherKinds, implicitConversions}
import ru.ifmo.ctd.ngp.util.ilist.IList.LastElementIs

/**
 * An instance of implicit list with size 10.
 *
 * @author Maxim Buzdalov
 */
class IList10[T0, T1, T2, T3, T4, T5, T6, T7, T8, T9] private[ilist] (
  private[ilist] val t0: T0,
  private[ilist] val t1: T1,
  private[ilist] val t2: T2,
  private[ilist] val t3: T3,
  private[ilist] val t4: T4,
  private[ilist] val t5: T5,
  private[ilist] val t6: T6,
  private[ilist] val t7: T7,
  private[ilist] val t8: T8,
  private[ilist] val t9: T9
) extends IList with LastElementIs[T9] {
  override def last = t9
  override def and[T](t: T)(implicit notAddingTwice: Next[T] => T): Next[T] =
    throw new NotImplementedError("IList11 is not implemented, sorry")
  override type Next[T] = Nothing
}

object IList10 {
  implicit def arg0[T](l: IList10[T, _, _, _, _, _, _, _, _, _]): T = l.t0
  implicit def arg1[T](l: IList10[_, T, _, _, _, _, _, _, _, _]): T = l.t1
  implicit def arg2[T](l: IList10[_, _, T, _, _, _, _, _, _, _]): T = l.t2
  implicit def arg3[T](l: IList10[_, _, _, T, _, _, _, _, _, _]): T = l.t3
  implicit def arg4[T](l: IList10[_, _, _, _, T, _, _, _, _, _]): T = l.t4
  implicit def arg5[T](l: IList10[_, _, _, _, _, T, _, _, _, _]): T = l.t5
  implicit def arg6[T](l: IList10[_, _, _, _, _, _, T, _, _, _]): T = l.t6
  implicit def arg7[T](l: IList10[_, _, _, _, _, _, _, T, _, _]): T = l.t7
  implicit def arg8[T](l: IList10[_, _, _, _, _, _, _, _, T, _]): T = l.t8
  implicit def arg9[T](l: IList10[_, _, _, _, _, _, _, _, _, T]): T = l.t9
}
