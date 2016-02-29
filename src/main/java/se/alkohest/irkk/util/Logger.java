package se.alkohest.irkk.util;

public class Logger {
    private static EventListener listener;

    public static void setEventListener(EventListener listener) {
        Logger.listener = listener;
    }

    public static void log(String tag, String msg) {
        if (listener == null) {
            System.out.println(tag + ":" + msg);
        }
        else {
            listener.log(tag, msg);
        }
    }

    public interface EventListener {
        void log(String tag, String message);
    }
}
