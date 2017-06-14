package xyz.gnarbot.gnar.utils;

import net.dv8tion.jda.core.utils.SimpleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.WeakHashMap;

public class SimpleLogToSLF4JAdapter implements SimpleLog.LogListener {
    private final Map<SimpleLog, Logger> loggers;

    public static void install() {
        SimpleLog.addListener(new SimpleLogToSLF4JAdapter());
        SimpleLog.LEVEL = SimpleLog.Level.OFF;
    }

    private SimpleLogToSLF4JAdapter() {
        loggers = new WeakHashMap<>();
    }

    @Override
    public void onLog(SimpleLog simpleLog, SimpleLog.Level logLevel, Object message) {
        Logger log = convert(simpleLog);
        switch (logLevel) {
            case TRACE:
                if (log.isTraceEnabled()) {
                    log.trace(message.toString());
                }
                break;
            case DEBUG:
                if (log.isDebugEnabled()) {
                    log.debug(message.toString());
                }
                break;
            case INFO:
                log.info(message.toString());
                break;
            case WARNING:
                log.warn(message.toString());
                break;
            case FATAL:
                log.error(message.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(SimpleLog simpleLog, Throwable err) {
        convert(simpleLog).error("An exception occurred", err);
    }

    private Logger convert(SimpleLog log) {
        return loggers.computeIfAbsent(log, ignored -> LoggerFactory.getLogger(log.name));
    }
}