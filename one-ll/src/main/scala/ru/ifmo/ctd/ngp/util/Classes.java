package ru.ifmo.ctd.ngp.util;

import java.lang.instrument.Instrumentation;

/**
 * Static utility class for working with classes
 *
 * @author Maxim Buzdalov
 */
public final class Classes {
    private Classes() {
        Static.doNotCreateInstancesOf(Classes.class);
    }

    private static Instrumentation instrumentation = null;

    private static void setInstrumentation(Instrumentation instrumentation) {
        Classes.instrumentation = instrumentation;
    }

    public static Instrumentation instrumentation() {
        if (instrumentation == null) {
            throw new InstrumentationNotLoadedException();
        } else {
            return instrumentation;
        }
    }

    public static class InstrumentationNotLoadedException extends IllegalStateException {
        private static final long serialVersionUID = -1805704901955744722L;
    }

    public static final class Agent {
        public static void premain(String agentArgs, Instrumentation inst) {
            Classes.setInstrumentation(inst);
        }
    }
}
