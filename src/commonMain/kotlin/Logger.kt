package fi.papinkivi.crap

import mu.*

abstract class Logger(func: () -> Unit) {
    private val logger by lazy { KotlinLogging.logger(func) }

    protected fun debug(msg: () -> Any?) = logger.debug(msg)

    protected fun info(msg: () -> Any?) = logger.info(msg)

    protected fun trace(msg: () -> Any?) = logger.trace(msg)

    protected fun warn(msg: () -> Any?) = logger.warn(msg)
}