package ru.ifmo.ctd.ngp.util;

import javax.swing.*;

/**
 * Utilities for working with Swing not found in standard library.
 *
 * @author Maxim Buzdalov
 */
public final class SwingEx {
    private SwingEx() {
        Static.doNotCreateInstancesOf(SwingEx.class);
    }

    /**
     * Invokes the specified runnable immediately if the calling thread is the
     * event dispatch thread, or invokes it later in the event dispatch thread otherwise.
     * @param runnable the runnable to be invoked.
     */
    public static void invokeInEventDispatchThread(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }
}
