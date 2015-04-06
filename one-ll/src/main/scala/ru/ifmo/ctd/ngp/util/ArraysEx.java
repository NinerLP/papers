package ru.ifmo.ctd.ngp.util;

/**
 * An utility class for arrays containing methods not found in {@link java.util.Arrays}.
 *
 * @author Maxim Buzdalov
 */
public final class ArraysEx {
    private ArraysEx() {
        Static.doNotCreateInstancesOf(ArraysEx.class);
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static boolean[][] copy(boolean[][] a) {
        if (a == null) {
            return null;
        }
        boolean[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static byte[][] copy(byte[][] a) {
        if (a == null) {
            return null;
        }
        byte[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static char[][] copy(char[][] a) {
        if (a == null) {
            return null;
        }
        char[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static short[][] copy(short[][] a) {
        if (a == null) {
            return null;
        }
        short[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static int[][] copy(int[][] a) {
        if (a == null) {
            return null;
        }
        int[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static long[][] copy(long[][] a) {
        if (a == null) {
            return null;
        }
        long[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static float[][] copy(float[][] a) {
        if (a == null) {
            return null;
        }
        float[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static double[][] copy(double[][] a) {
        if (a == null) {
            return null;
        }
        double[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }

    /**
     * Returns a full copy of the given two-dimensional array. If the argument is <tt>null</tt>,
     * then <tt>null</tt> is returned.
     *
     * The objects in the given array are not cloned, just copied by reference.
     *
     * @param a the array to be copied.
     * @return the copy of the given array.
     */
    public static <T> T[][] copy(T[][] a) {
        if (a == null) {
            return null;
        }
        T[][] rv = a.clone();
        for (int i = 0, x = rv.length; i < x; ++i) {
            rv[i] = rv[i].clone();
        }
        return rv;
    }
}
