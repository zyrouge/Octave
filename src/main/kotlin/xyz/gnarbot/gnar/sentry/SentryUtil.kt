package xyz.gnarbot.gnar.sentry

import io.sentry.Sentry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import xyz.gnarbot.gnar.Bot

class SentryUtil(val bot: Bot, dsn: String){

    private val logger : Logger = LoggerFactory.getLogger(SentryUtil::class.java)

    init {
        Sentry.init(dsn)
        logger.info("Sentry loaded!")
    }

    fun logMessage(message: String, exception: Throwable?) {
        logger.error(message, exception)
    }

    fun logWithTags(message: String, exception: Throwable?) {
        // Weird and useless way to set the OS tag in Sentry
        when(System.getProperty("os.name")) {
            "win" -> MDC.put("OS", "Windows")
            "mac" -> MDC.put("OS", "Mac")
            "nix", "nux", "aix" -> MDC.put("OS", "Linux")
            else -> return
        }
        logger.error(message, exception)
    }

    fun unsafeMethod() {
        throw UnsupportedOperationException("You shoudn't call this!")
    }
}