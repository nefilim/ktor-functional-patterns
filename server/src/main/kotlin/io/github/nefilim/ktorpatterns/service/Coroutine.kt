package io.github.nefilim.ktorpatterns.service

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import mu.KLogger

suspend fun <T>monitorFlow(
    flowName: String,
    createFlow: suspend () -> Flow<T>,
    coroutineContext: CoroutineContext,
    logger: KLogger,
) {
    val supervisor = SupervisorJob().also { it.invokeOnCompletion { logger.warn("Supervision Job [$flowName] has completed") } }

    tailrec suspend fun <T> monitorJobRecurse(flowName: String, createFlow: suspend () -> Flow<T>, coroutineContext: CoroutineContext, attempts: Int) {
        createFlow()
            .launchIn(CoroutineScope(coroutineContext + supervisor + CoroutineName("flow-monitor-$flowName")))
            .join()
        logger.warn("REstarting flow $flowName, attempt $attempts")
        monitorJobRecurse(flowName, createFlow, coroutineContext, attempts + 1)
    }

    logger.info("Starting flow $flowName")
    monitorJobRecurse(flowName, createFlow, coroutineContext, 0)
}