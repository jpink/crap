package fi.papinkivi.crap

import mu.*

abstract class Logger(func: () -> Unit, prefix: String = "") {
    private val logger by lazy { KotlinLogging.logger(func) }

    // TODO needs optimizing?
    private val prefix = if (prefix.isBlank()) "" else "$prefix - "

    protected fun debug(msg: () -> Any?) = logger.debug { "$prefix${msg()}" }

    protected fun error(msg: () -> Any?) = logger.error { "$prefix${msg()}" }

    protected fun error(t: Throwable?, msg: () -> Any?) = logger.error(t) { "$prefix${msg()}" }

    protected fun info(msg: () -> Any?) = logger.info { "$prefix${msg()}" }

    protected fun trace(msg: () -> Any?) = logger.trace { "$prefix${msg()}" }

    protected fun warn(msg: () -> Any?) = logger.warn { "$prefix${msg()}" }

}